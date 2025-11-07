package br.com.brecbrecho.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Imports Jakarta EE
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Nossos pacotes
import br.com.brecbrecho.dao.PedidoDAO;
import br.com.brecbrecho.dao.ProdutoDAO;
import br.com.brecbrecho.model.Cliente;
import br.com.brecbrecho.model.ItemPedido;
import br.com.brecbrecho.model.Pedido;
import br.com.brecbrecho.model.Produto;

/**
 * Servlet para gerenciar o Carrinho de Compras, que é armazenado na Sessão.
 * Ouve na URL /carrinho
 */
@WebServlet("/carrinho")
public class CarrinhoServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // Constantes do seu carrinho.js
    private static final double FRETE = 15.00;
    private static final double DESCONTO_PIX_BOLETO = 0.10; // 10%

    private ProdutoDAO produtoDAO;
    private PedidoDAO pedidoDAO;

    public void init() {
        produtoDAO = new ProdutoDAO();
        pedidoDAO = new PedidoDAO();
    }

    /**
     * doGet lida com ações de link: Adicionar, Remover, Esvaziar, ou simplesmente Ver o Carrinho.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String acao = request.getParameter("acao");
        if (acao == null) {
            acao = "ver"; // Ação padrão
        }

        switch (acao) {
            case "adicionar":
                adicionarItem(request, response);
                break;
            case "remover":
                removerItem(request, response);
                break;
            case "esvaziar":
                esvaziarCarrinho(request, response);
                break;
            case "ver":
            default:
                // Ação padrão: apenas mostra a página carrinho.jsp
                RequestDispatcher dispatcher = request.getRequestDispatcher("carrinho.jsp");
                dispatcher.forward(request, response);
                break;
        }
    }

    /**
     * doPost lida com a submissão do formulário de Finalizar Compra.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String acao = request.getParameter("acao");

        if ("finalizar".equals(acao)) {
            finalizarCompra(request, response);
        } else {
            // Se não for "finalizar", apenas redireciona para o GET
            doGet(request, response);
        }
    }

    // --- Métodos de Ação (GET) ---

    private void adicionarItem(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        
        // O carrinho será um Mapa<ID_Produto, ItemPedido>
        Map<Integer, ItemPedido> carrinho = (Map<Integer, ItemPedido>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new HashMap<>();
        }

        try {
            int idProduto = Integer.parseInt(request.getParameter("id"));
            Produto produto = produtoDAO.buscarProdutoPorId(idProduto);

            if (produto == null || produto.getEstoque() <= 0) {
                // Produto não existe ou está sem estoque
                response.sendRedirect("catalogo?msg=erro_estoque");
                return;
            }

            ItemPedido itemNoCarrinho = carrinho.get(idProduto);

            if (itemNoCarrinho == null) {
                // Adiciona novo item ao carrinho
                ItemPedido novoItem = new ItemPedido();
                novoItem.setIdProduto(idProduto);
                novoItem.setQuantidade(1);
                novoItem.setPrecoUnitarioVenda(produto.getPreco());
                novoItem.setProduto(produto); // Anexa o objeto produto para usarmos no JSP (nome, foto)
                carrinho.put(idProduto, novoItem);
            } else {
                // Aumenta a quantidade se houver estoque
                if (itemNoCarrinho.getQuantidade() < produto.getEstoque()) {
                    itemNoCarrinho.setQuantidade(itemNoCarrinho.getQuantidade() + 1);
                } else {
                    // Estoque máximo atingido
                    session.setAttribute("msg_carrinho", "Estoque máximo atingido para este item.");
                    response.sendRedirect("carrinho");
                    return;
                }
            }

            session.setAttribute("carrinho", carrinho);
            response.sendRedirect("carrinho"); // Redireciona para ver o carrinho

        } catch (NumberFormatException e) {
            response.sendRedirect("catalogo?msg=erro_id");
        }
    }

    private void removerItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Map<Integer, ItemPedido> carrinho = (Map<Integer, ItemPedido>) session.getAttribute("carrinho");

        if (carrinho != null) {
            try {
                int idProduto = Integer.parseInt(request.getParameter("id"));
                carrinho.remove(idProduto); // Remove o item do Mapa
                session.setAttribute("carrinho", carrinho);
            } catch (NumberFormatException e) {
                // ID inválido, não faz nada
            }
        }
        response.sendRedirect("carrinho");
    }

    private void esvaziarCarrinho(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("carrinho"); // Remove o atributo inteiro da sessão
        response.sendRedirect("carrinho");
    }

    // --- Método de Ação (POST) ---

    private void finalizarCompra(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false); // Pega a sessão, não cria uma

        // --- 1. Verificação de Segurança (Login) ---
        if (session == null || session.getAttribute("usuarioLogado") == null || !"cliente".equals(session.getAttribute("tipoUsuario"))) {
            response.sendRedirect("login.jsp?msg=erro_finalizar");
            return;
        }
        Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");

        // --- 2. Pega o Carrinho da Sessão ---
        Map<Integer, ItemPedido> carrinho = (Map<Integer, ItemPedido>) session.getAttribute("carrinho");
        if (carrinho == null || carrinho.isEmpty()) {
            response.sendRedirect("carrinho?msg=vazio");
            return;
        }
        
        // --- 3. Pega dados do formulário ---
        String formaPagamento = request.getParameter("formaPagamento"); // (pix, boleto, cartao)

        try {
            // --- 4. Calcular o Total (SEMPRE no Backend) ---
            double subtotal = 0.0;
            for (ItemPedido item : carrinho.values()) {
                subtotal += item.getPrecoUnitarioVenda() * item.getQuantidade();
            }
            
            double totalFinal = subtotal + FRETE; // Adiciona frete
            
            if ("pix".equals(formaPagamento) || "boleto".equals(formaPagamento)) {
                totalFinal = totalFinal * (1.0 - DESCONTO_PIX_BOLETO); // Aplica desconto
            }

            // --- 5. Montar o Objeto Pedido ---
            Pedido novoPedido = new Pedido();
            novoPedido.setIdCliente(clienteLogado.getIdCliente());
            novoPedido.setFormaPagamento(formaPagamento);
            novoPedido.setValorTotal(totalFinal);
            novoPedido.setStatusPedido("PENDENTE"); // O PedidoDAO já faz isso, mas é bom garantir
            
            // Adiciona a lista de itens ao pedido
            novoPedido.setItens(new ArrayList<>(carrinho.values()));

            // --- 6. Salvar no Banco (usando a Transação do DAO) ---
            boolean sucesso = pedidoDAO.salvarPedido(novoPedido);

            // --- 7. Resposta ---
            if (sucesso) {
                session.removeAttribute("carrinho"); // Limpa o carrinho
                // TODO: Criar a página "meus-pedidos.jsp"
                response.sendRedirect("meus-pedidos.jsp?msg=compra_sucesso");
            } else {
                // Falha (ex: estoque acabou durante a transação)
                response.sendRedirect("carrinho?msg=erro_finalizar");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("carrinho?msg=erro_excecao");
        }
    }
}