package br.com.brecbrecho.controller;

// Imports Java (para lidar com arquivos e Base64)
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
// import java.util.List; // Não é usado diretamente aqui

// Imports Jakarta EE (Tomcat 10+)
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

// Nossos pacotes
import br.com.brecbrecho.dao.ProdutoDAO;
import br.com.brecbrecho.model.Fornecedor;
import br.com.brecbrecho.model.Produto;
import br.com.brecbrecho.model.Administrador; // Precisamos saber o que é um Admin

/**
 * Servlet "CRUD" para Produtos.
 * Agora pode ser usado por FORNECEDORES (para seus produtos)
 * e ADMINS (para qualquer produto).
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
     * doGet() lida com (Excluir, Carregar para Editar, Novo).
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // --- 1. Verificação de Segurança (CORRIGIDA) ---
        String tipoUsuario = (session != null) ? (String) session.getAttribute("tipoUsuario") : null;

        // Se a sessão não existe OU o usuário não é nem fornecedor NEM admin, expulsa.
        if (tipoUsuario == null || (!"fornecedor".equals(tipoUsuario) && !"admin".equals(tipoUsuario))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=acesso_negado");
            return;
        }
        
        // --- FIM DA CORREÇÃO ---
        
        String acao = request.getParameter("acao");

        try {
            if ("excluir".equals(acao)) {
                // --- AÇÃO DE EXCLUIR ---
                int idProduto = Integer.parseInt(request.getParameter("id"));
                Produto p = produtoDAO.buscarProdutoPorId(idProduto);
                
                boolean temPermissao = false;

                // Se for admin, tem permissão
                if ("admin".equals(tipoUsuario)) {
                    temPermissao = true;
                } 
                // Se for fornecedor, checa se é o dono
                else if ("fornecedor".equals(tipoUsuario)) {
                    Fornecedor f = (Fornecedor) session.getAttribute("usuarioLogado");
                    if (p != null && p.getIdFornecedor() == f.getIdFornecedor()) {
                        temPermissao = true;
                    }
                }

                if (temPermissao) {
                    produtoDAO.excluirProduto(idProduto);
                    // Redireciona de volta para a página correta
                    String redirectURL = "admin".equals(tipoUsuario) ? "admin/gerenciarProdutos" : "meus-produtos";
                    response.sendRedirect(redirectURL + "?msg=excluido_sucesso");
                } else {
                    String redirectURL = "admin".equals(tipoUsuario) ? "admin/gerenciarProdutos" : "meus-produtos";
                    response.sendRedirect(redirectURL + "?msg=erro_excluir");
                }
                
            } else if ("carregar".equals(acao)) {
                // --- AÇÃO DE CARREGAR PARA EDITAR ---
                int idProduto = Integer.parseInt(request.getParameter("id"));
                Produto p = produtoDAO.buscarProdutoPorId(idProduto);
                
                boolean temPermissao = false;

                if ("admin".equals(tipoUsuario)) {
                    temPermissao = true;
                } 
                else if ("fornecedor".equals(tipoUsuario)) {
                    Fornecedor f = (Fornecedor) session.getAttribute("usuarioLogado");
                    if (p != null && p.getIdFornecedor() == f.getIdFornecedor()) {
                        temPermissao = true;
                    }
                }

                if (p != null && temPermissao) {
                    request.setAttribute("produtoParaEditar", p);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("cadastrar-produto.jsp");
                    dispatcher.forward(request, response);
                } else {
                    String redirectURL = "admin".equals(tipoUsuario) ? "admin/gerenciarProdutos" : "meus-produtos";
                    response.sendRedirect(redirectURL + "?msg=erro_carregar");
                }
                
            } else if ("novo".equals(acao) && "admin".equals(tipoUsuario)) {
                 // --- ADMIN NÃO PODE CRIAR PRODUTO NOVO ---
                 response.sendRedirect(request.getContextPath() + "/admin/gerenciarProdutos?msg=admin_nao_cria");
            
            } else {
                // --- AÇÃO DE NOVO PRODUTO (para Fornecedor) ---
                RequestDispatcher dispatcher = request.getRequestDispatcher("cadastrar-produto.jsp");
                dispatcher.forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            String redirectURL = "admin".equals(tipoUsuario) ? "admin/gerenciarProdutos" : "meus-produtos";
            response.sendRedirect(redirectURL + "?msg=erro_geral");
        }
    }

    /**
     * doPost() lida com o envio do formulário (Salvar Novo ou Salvar Edição).
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // --- 1. Verificação de Segurança (CORRIGIDA) ---
        String tipoUsuario = (session != null) ? (String) session.getAttribute("tipoUsuario") : null;

        if (tipoUsuario == null || (!"fornecedor".equals(tipoUsuario) && !"admin".equals(tipoUsuario))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=acesso_negado");
            return;
        }
        // --- FIM DA CORREÇÃO ---

        String redirectURL = "admin".equals(tipoUsuario) ? "admin/gerenciarProdutos" : "meus-produtos";

        try {
            // 1. Coletar dados do formulário
            String nome = request.getParameter("nomeProduto");
            String descricao = request.getParameter("descricaoProduto");
            String tamanho = request.getParameter("tamanhoProduto");
            int estoque = Integer.parseInt(request.getParameter("quantidadeProduto"));
            double preco = Double.parseDouble(request.getParameter("precoProduto"));
            int prazo = Integer.parseInt(request.getParameter("prazoProduto"));
            
            String idProdutoStr = request.getParameter("idProduto");

            // 2. Lidar com a Foto
            Part fotoPart = request.getPart("fotoProduto");
            String fotoBase64 = null;

            if (fotoPart != null && fotoPart.getSize() > 0) {
                String mimeType = fotoPart.getContentType();
                InputStream inputStream = fotoPart.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                byte[] imageBytes = buffer.toByteArray();
                String base64Encoded = Base64.getEncoder().encodeToString(imageBytes);
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

            // 4. Decidir se é CADASTRAR (Create) ou ATUALIZAR (Update)
            if (idProdutoStr == null || idProdutoStr.isEmpty()) {
                // --- CADASTRAR (CREATE) ---
                
                // Admin não pode criar produtos, só fornecedor
                if ("admin".equals(tipoUsuario)) {
                     response.sendRedirect(request.getContextPath() + "/admin/gerenciarProdutos?msg=admin_nao_cria");
                     return;
                }
                
                Fornecedor fornecedorLogado = (Fornecedor) session.getAttribute("usuarioLogado");
                produto.setIdFornecedor(fornecedorLogado.getIdFornecedor()); // Dono do produto
                produto.setFotoBase64(fotoBase64); 
                produtoDAO.cadastrarProduto(produto);
                
            } else {
                // --- ATUALIZAR (UPDATE) ---
                int idProduto = Integer.parseInt(idProdutoStr);
                produto.setIdProduto(idProduto);
                
                Produto produtoExistente = produtoDAO.buscarProdutoPorId(idProduto);
                if (produtoExistente == null) {
                    response.sendRedirect(redirectURL + "?msg=erro_nao_encontrado");
                    return;
                }
                
                // (Segurança) Garante que o fornecedor só edite o que é seu
                if ("fornecedor".equals(tipoUsuario)) {
                    Fornecedor fornecedorLogado = (Fornecedor) session.getAttribute("usuarioLogado");
                    if (produtoExistente.getIdFornecedor() != fornecedorLogado.getIdFornecedor()) {
                         response.sendRedirect("meus-produtos?msg=erro_permissao");
                         return;
                    }
                }
                // Admin pula essa checagem
                
                // Define o ID do fornecedor (para não perdê-lo)
                produto.setIdFornecedor(produtoExistente.getIdFornecedor());
                
                if (fotoBase64 == null) {
                    // Manter a foto antiga
                    produto.setFotoBase64(produtoExistente.getFotoBase64());
                } else {
                    // Salvar a foto nova
                    produto.setFotoBase64(fotoBase64);
                }
                
                produtoDAO.atualizarProduto(produto);
            }
            
            // 5. Redirecionar para a lista de produtos correta
            response.sendRedirect(redirectURL + "?msg=salvo_sucesso");

        } catch (Exception e) {
            e.printStackTrace();
            // Se der erro (ex: ClassCastException se o admin tentar criar)
            // redireciona para a página de origem correta
            response.sendRedirect(redirectURL + "?msg=erro_salvar");
        }
    }
}