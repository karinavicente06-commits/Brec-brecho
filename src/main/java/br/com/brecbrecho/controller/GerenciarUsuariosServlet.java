package br.com.brecbrecho.controller;

import java.io.IOException;
import java.util.List;

// Nossos pacotes
import br.com.brecbrecho.dao.ClienteDAO;
import br.com.brecbrecho.dao.FornecedorDAO;
import br.com.brecbrecho.model.Cliente;
import br.com.brecbrecho.model.Fornecedor;

// Imports Jakarta EE
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para o Admin gerenciar (listar/excluir) Clientes e Fornecedores.
 * Protegido pelo AdminAuthFilter (pois está em "/admin/*")
 */
@WebServlet("/admin/gerenciarUsuarios")
public class GerenciarUsuariosServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private ClienteDAO clienteDAO;
    private FornecedorDAO fornecedorDAO;

    @Override
    public void init() throws ServletException {
        clienteDAO = new ClienteDAO();
        fornecedorDAO = new FornecedorDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String acao = request.getParameter("acao");
        String msg = null; // Para mensagens de sucesso/erro

        try {
            // --- 1. Processar Ações (Ex: Excluir) ---
            if (acao != null) {
                int id = Integer.parseInt(request.getParameter("id"));
                
                if ("excluirCliente".equals(acao)) {
                    boolean sucesso = clienteDAO.excluirCliente(id);
                    msg = sucesso ? "Cliente excluído com sucesso." : "Erro ao excluir cliente (pode ter pedidos associados).";
                
                } else if ("excluirFornecedor".equals(acao)) {
                    boolean sucesso = fornecedorDAO.excluirFornecedor(id);
                    msg = sucesso ? "Fornecedor e seus produtos excluídos com sucesso." : "Erro ao excluir fornecedor.";
                }
            }

            // --- 2. Buscar as Listas para Exibição ---
            List<Cliente> listaClientes = clienteDAO.listarTodosClientes();
            List<Fornecedor> listaFornecedores = fornecedorDAO.listarTodosFornecedores();

            // --- 3. Anexar Dados à Requisição ---
            request.setAttribute("listaClientes", listaClientes);
            request.setAttribute("listaFornecedores", listaFornecedores);
            if (msg != null) {
                request.setAttribute("mensagem", msg); // Envia a mensagem de sucesso/erro
            }

            // --- 4. Encaminhar para a Página JSP (View) ---
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/gerenciarUsuarios.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensagem", "Erro crítico: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/gerenciarUsuarios.jsp");
            dispatcher.forward(request, response);
        }
    }
}