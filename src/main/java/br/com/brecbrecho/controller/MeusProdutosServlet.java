package br.com.brecbrecho.controller;

// Imports Jakarta EE
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Para verificar o login
import java.io.IOException;
import java.util.List;

// Nossos pacotes
import br.com.brecbrecho.dao.ProdutoDAO;
import br.com.brecbrecho.model.Fornecedor; // Precisamos do Fornecedor
import br.com.brecbrecho.model.Produto;

/**
 * Servlet para carregar a lista de produtos de um fornecedor específico.
 * Ouve na URL /meus-produtos
 */
@WebServlet("/meus-produtos")
public class MeusProdutosServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private ProdutoDAO produtoDAO;

    public void init() {
        produtoDAO = new ProdutoDAO();
    }

    /**
     * doGet() é chamado quando o fornecedor clica no link "Meus Produtos".
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // Pega a sessão, não cria uma nova

        // --- Verificação de Segurança ---
        // 1. Verifica se a sessão existe E se o usuário é um "fornecedor"
        if (session == null || !"fornecedor".equals(session.getAttribute("tipoUsuario"))) {
            // Se não for fornecedor, manda para o login
            response.sendRedirect("login.jsp?msg=acesso_negado");
            return; // Encerra a execução
        }
        // --- Fim da Verificação ---

        try {
            // Se passou na verificação, pega o objeto Fornecedor da sessão
            Fornecedor fornecedorLogado = (Fornecedor) session.getAttribute("usuarioLogado");
            int idFornecedor = fornecedorLogado.getIdFornecedor();

            // 2. Busca no DAO APENAS os produtos deste fornecedor
            List<Produto> listaMeusProdutos = produtoDAO.listarProdutosPorFornecedor(idFornecedor);
            
            // 3. Anexa a lista à requisição
            request.setAttribute("listaMeusProdutos", listaMeusProdutos);
            
            // 4. Encaminha para a página JSP (que criaremos em breve)
            // Esta página (meus-produtos.jsp) mostrará a lista e
            // terá os links para "Editar" e "Excluir".
            RequestDispatcher dispatcher = request.getRequestDispatcher("meus-produtos.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erro", "Não foi possível carregar seus produtos.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        }
    }
}