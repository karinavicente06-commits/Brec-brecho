package br.com.brecbrecho.controller;

import java.io.IOException;
import java.util.List;

import br.com.brecbrecho.dao.ProdutoDAO;
import br.com.brecbrecho.model.Produto;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para o Admin ver TODOS os produtos de TODOS os fornecedores.
 * Protegido pelo AdminAuthFilter (pois está em "/admin/*")
 */
@WebServlet("/admin/gerenciarProdutos")
public class GerenciarProdutosServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private ProdutoDAO produtoDAO;

    @Override
    public void init() throws ServletException {
        produtoDAO = new ProdutoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Buscar a lista de TODOS os produtos
            // (Já temos esse método no ProdutoDAO, que o /catalogo usa!)
            List<Produto> listaProdutos = produtoDAO.listarTodosProdutos();

            // 2. Anexar a lista à requisição
            request.setAttribute("listaProdutos", listaProdutos);

            // 3. Encaminhar para a página JSP (Visão)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/gerenciarProdutos.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erro", "Erro ao carregar produtos.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/verMensagens.jsp"); // Volta p/ pág principal do admin
            dispatcher.forward(request, response);
        }
    }
}