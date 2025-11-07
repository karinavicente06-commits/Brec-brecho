package br.com.brecbrecho.controller;

// Imports Jakarta EE (Tomcat 10+)
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Importar HttpSession
import java.io.IOException;

/**
 * Servlet para lidar com o logout (sair) do usuário.
 * Ouve na URL /logout
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    /**
     * O logout é geralmente feito via um link (clique), que é um método GET.
     * Por isso, usamos doGet() aqui.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Obter a sessão atual. 
            // O 'false' significa: "pegue a sessão se ela existir, mas não crie uma nova".
            HttpSession session = request.getSession(false); 
            
            if (session != null) {
                // 2. Invalidar a sessão.
                // Isso remove todos os atributos ("usuarioLogado", "tipoUsuario", etc.)
                session.invalidate();
            }
            
            // 3. Redirecionar o usuário de volta para a página inicial.
            response.sendRedirect("index.jsp?msg=logout_sucesso");
            
        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de erro, apenas redireciona para a index
            response.sendRedirect("index.jsp");
        }
    }
    
    /**
     * Boa prática: Se o usuário tentar acessar /logout via POST por algum motivo,
     * apenas redirecione para o doGet.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}