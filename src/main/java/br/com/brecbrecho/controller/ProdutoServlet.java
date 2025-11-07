package br.com.brecbrecho.controller;

// Imports Java (para lidar com arquivos e Base64)
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

// Imports Jakarta EE (Tomcat 10+)
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // <-- IMPORTANTE para upload de arquivos
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; // <-- IMPORTANTE para upload de arquivos

// Nossos pacotes
import br.com.brecbrecho.dao.ProdutoDAO;
import br.com.brecbrecho.model.Fornecedor;
import br.com.brecbrecho.model.Produto;

/**
 * Servlet "CRUD" para Produtos.
 * Ouve na URL /produto
 * Requer configuração para arquivos (Multipart)
 */
@WebServlet("/produto")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
    maxFileSize = 1024 * 1024 * 10, // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class ProdutoServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private ProdutoDAO produtoDAO;

    public void init() {
        produtoDAO = new ProdutoDAO();
    }

    /**
     * doGet() lida com ações que vêm de links (Excluir, Carregar para Editar, Novo).
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // --- Verificação de Segurança ---
        if (session == null || !"fornecedor".equals(session.getAttribute("tipoUsuario"))) {
            response.sendRedirect("login.jsp?msg=acesso_negado");
            return;
        }
        
        Fornecedor fornecedorLogado = (Fornecedor) session.getAttribute("usuarioLogado");
        String acao = request.getParameter("acao");

        try {
            if ("excluir".equals(acao)) {
                // --- AÇÃO DE EXCLUIR ---
                int idProduto = Integer.parseInt(request.getParameter("id"));
                
                // Verificação de segurança: O produto é deste fornecedor?
                Produto p = produtoDAO.buscarProdutoPorId(idProduto);
                if (p != null && p.getIdFornecedor() == fornecedorLogado.getIdFornecedor()) {
                    produtoDAO.excluirProduto(idProduto);
                    response.sendRedirect("meus-produtos?msg=excluido_sucesso");
                } else {
                    response.sendRedirect("meus-produtos?msg=erro_excluir");
                }
                
            } else if ("carregar".equals(acao)) {
                // --- AÇÃO DE CARREGAR PARA EDITAR ---
                int idProduto = Integer.parseInt(request.getParameter("id"));
                
                Produto p = produtoDAO.buscarProdutoPorId(idProduto);
                
                // Verificação de segurança
                if (p != null && p.getIdFornecedor() == fornecedorLogado.getIdFornecedor()) {
                    request.setAttribute("produtoParaEditar", p);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("cadastrar-produto.jsp");
                    dispatcher.forward(request, response);
                } else {
                    response.sendRedirect("meus-produtos?msg=erro_carregar");
                }
                
            } else {
                // --- AÇÃO DE NOVO PRODUTO (link "Cadastrar Novo") ---
                // Apenas encaminha para o formulário em branco
                RequestDispatcher dispatcher = request.getRequestDispatcher("cadastrar-produto.jsp");
                dispatcher.forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("meus-produtos?msg=erro_geral");
        }
    }

    /**
     * doPost() lida com o envio do formulário (Salvar Novo ou Salvar Edição).
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // --- Verificação de Segurança ---
        if (session == null || !"fornecedor".equals(session.getAttribute("tipoUsuario"))) {
            response.sendRedirect("login.jsp?msg=acesso_negado");
            return;
        }
        
        Fornecedor fornecedorLogado = (Fornecedor) session.getAttribute("usuarioLogado");

        try {
            // 1. Coletar dados do formulário
            String nome = request.getParameter("nomeProduto");
            String descricao = request.getParameter("descricaoProduto");
            String tamanho = request.getParameter("tamanhoProduto");
            int estoque = Integer.parseInt(request.getParameter("quantidadeProduto"));
            double preco = Double.parseDouble(request.getParameter("precoProduto"));
            int prazo = Integer.parseInt(request.getParameter("prazoProduto"));
            
            // Verifica se é uma edição (campo 'idProduto' escondido no form)
            String idProdutoStr = request.getParameter("idProduto");

            // 2. Lidar com a Foto (Upload -> InputStream -> byte[] -> Base64)
            Part fotoPart = request.getPart("fotoProduto");
            String fotoBase64 = null;

            if (fotoPart != null && fotoPart.getSize() > 0) {
                // Se um NOVO arquivo foi enviado
                String mimeType = fotoPart.getContentType();
                InputStream inputStream = fotoPart.getInputStream();
                
                // Converte InputStream para byte[]
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                byte[] imageBytes = buffer.toByteArray();
                
                // Converte byte[] para String Base64
                String base64Encoded = Base64.getEncoder().encodeToString(imageBytes);
                
                // Adiciona o prefixo Data-URI (para o <img src="...">)
                fotoBase64 = "data:" + mimeType + ";base64," + base64Encoded;
            }

            // 3. Montar o objeto Produto
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setTamanho(tamanho);
            produto.setEstoque(estoque);
            produto.setPreco(preco);
            produto.setPrazoLocacaoDias(prazo);
            produto.setIdFornecedor(fornecedorLogado.getIdFornecedor()); // Dono do produto

            // 4. Decidir se é CADASTRAR (Create) ou ATUALIZAR (Update)
            if (idProdutoStr == null || idProdutoStr.isEmpty()) {
                // --- CADASTRAR (CREATE) ---
                produto.setFotoBase64(fotoBase64); // Salva a nova foto (ou null se nenhuma foi enviada)
                produtoDAO.cadastrarProduto(produto);
                
            } else {
                // --- ATUALIZAR (UPDATE) ---
                int idProduto = Integer.parseInt(idProdutoStr);
                produto.setIdProduto(idProduto);
                
                if (fotoBase64 == null) {
                    // Nenhuma foto nova foi enviada. Manter a foto antiga.
                    Produto produtoExistente = produtoDAO.buscarProdutoPorId(idProduto);
                    
                    // (Segurança) Garante que o fornecedor só edite o que é seu
                    if (produtoExistente.getIdFornecedor() != fornecedorLogado.getIdFornecedor()) {
                         response.sendRedirect("meus-produtos?msg=erro_permissao");
                         return;
                    }
                    produto.setFotoBase64(produtoExistente.getFotoBase64());
                } else {
                    // Uma foto nova foi enviada
                    produto.setFotoBase64(fotoBase64);
                }
                
                produtoDAO.atualizarProduto(produto);
            }
            
            // 5. Redirecionar para a lista de produtos
            response.sendRedirect("meus-produtos?msg=salvo_sucesso");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("cadastrar-produto.jsp?msg=erro_salvar");
        }
    }
}