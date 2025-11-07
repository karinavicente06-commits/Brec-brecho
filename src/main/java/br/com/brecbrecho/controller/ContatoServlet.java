package br.com.brecbrecho.controller;

import java.io.IOException;

import br.com.brecbrecho.dao.MensagemSACDAO;
import br.com.brecbrecho.model.MensagemSAC;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/contato")
public class ContatoServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private MensagemSACDAO mensagemDAO;

    @Override
    public void init() throws ServletException {
        mensagemDAO = new MensagemSACDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Pegar os dados do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String mensagem = request.getParameter("mensagem");

        // 2. Criar o objeto Model
        MensagemSAC novaMensagem = new MensagemSAC();
        novaMensagem.setNome(nome);
        novaMensagem.setEmail(email);
        novaMensagem.setMensagem(mensagem);

        // 3. Chamar o DAO para salvar
        boolean sucesso = mensagemDAO.salvarMensagem(novaMensagem);

        // 4. Redirecionar de volta para a página de contato com uma mensagem
        if (sucesso) {
            response.sendRedirect("contato.jsp?msg=sucesso");
        } else {
            response.sendRedirect("contato.jsp?msg=erro");
        }
    }
}