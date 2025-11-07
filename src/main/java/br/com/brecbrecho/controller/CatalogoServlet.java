package br.com.brecbrecho.controller;

// Imports Jakarta EE (Tomcat 10+)
import jakarta.servlet.RequestDispatcher; // Importar o RequestDispatcher
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List; // Importar List

// Nossos pacotes
import br.com.brecbrecho.dao.ProdutoDAO;
import br.com.brecbrecho.model.Produto;

/**
 * Servlet para CARREGAR e EXIBIR os produtos no catálogo.
 * Ouve na URL /catalogo
 */
@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private ProdutoDAO produtoDAO;

    public void init() {
        produtoDAO = new ProdutoDAO(); // Instancia o DAO
    }

    /**
     * doGet() é chamado quando o usuário ACESSA a página /catalogo (clicando em um link).
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Buscar todos os produtos do banco de dados
            List<Produto> listaDeProdutos = produtoDAO.listarTodosProdutos();
            
            // 2. Anexar a lista à requisição (request).
            // O JSP poderá acessar essa lista usando o nome "listaProdutos".
            request.setAttribute("listaProdutos", listaDeProdutos);
            
            // 3. Encaminhar (forward) a requisição para o arquivo JSP.
            // Diferente do sendRedirect(), o forward() mantém a requisição atual
            // e os dados que anexamos (a lista de produtos).
            RequestDispatcher dispatcher = request.getRequestDispatcher("catalogo.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Se der um erro, podemos enviar para a index com uma mensagem.
            request.setAttribute("erro", "Não foi possível carregar o catálogo.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Boa prática: Se o usuário tentar acessar /catalogo via POST,
     * apenas redirecione para o doGet.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}