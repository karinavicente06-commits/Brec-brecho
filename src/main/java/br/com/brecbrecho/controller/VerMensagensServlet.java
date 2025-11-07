package br.com.brecbrecho.controller;

import java.io.IOException;
import java.util.List;

// Nossos pacotes
import br.com.brecbrecho.dao.MensagemSACDAO;
import br.com.brecbrecho.model.MensagemSAC;

// Imports Jakarta EE
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para o Admin ver as mensagens do SAC.
 * Protegido pelo AdminAuthFilter (pois está em "/admin/*")
 */
@WebServlet("/admin/verMensagens")
public class VerMensagensServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private MensagemSACDAO mensagemDAO;

    @Override
    public void init() throws ServletException {
        mensagemDAO = new MensagemSACDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. (Opcional) Processar ações, como "marcar como lida"
            String acao = request.getParameter("acao");
            if ("marcarLida".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                mensagemDAO.marcarComoLida(id);
                // Redireciona de volta para a lista limpa
                response.sendRedirect("verMensagens"); 
                return;
            }

            // 2. Buscar a lista de mensagens do banco
            List<MensagemSAC> listaMensagens = mensagemDAO.listarTodasMensagens();

            // 3. Anexar a lista à requisição
            request.setAttribute("listaMensagens", listaMensagens);

            // 4. Encaminhar para a página JSP (Visão)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/verMensagens.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de erro, pode redirecionar para uma página de erro do admin
            response.sendRedirect(request.getContextPath() + "/index.jsp?msg=erro_admin");
        }
    }
}