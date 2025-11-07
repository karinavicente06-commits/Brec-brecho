package br.com.brecbrecho.controller;

import java.io.IOException;

// Imports Jakarta EE
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Nossos pacotes
import br.com.brecbrecho.dao.ClienteDAO;
import br.com.brecbrecho.dao.FornecedorDAO;
import br.com.brecbrecho.model.Cliente;
import br.com.brecbrecho.model.Fornecedor;

/**
 * Servlet para ATUALIZAR os dados do perfil (Cliente ou Fornecedor).
 * Ouve na URL /salvarPerfil
 */
@WebServlet("/salvarPerfil")
public class PerfilServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    private ClienteDAO clienteDAO;
    private FornecedorDAO fornecedorDAO;

    public void init() {
        clienteDAO = new ClienteDAO();
        fornecedorDAO = new FornecedorDAO();
    }

    /**
     * doPost() é chamado quando o formulário de 'perfil.jsp' é enviado.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // Pega a sessão existente
        
        // --- 1. Verificação de Segurança ---
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect("login.jsp?msg=acesso_negado");
            return;
        }

        // Pega o tipo de usuário da sessão
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        // Pega os dados comuns do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String cep = request.getParameter("cep");
        String rua = request.getParameter("rua");
        String numero = request.getParameter("numero");
        String bairro = request.getParameter("bairro");
        String cidade = request.getParameter("cidade");
        String estado = request.getParameter("estado");
        String senha = request.getParameter("senha"); // Lembre-se do HASH

        boolean sucesso = false;

        try {
            if ("cliente".equals(tipoUsuario)) {
                // --- ATUALIZAR CLIENTE ---
                
                // Pega o objeto ATUAL da sessão
                Cliente clienteAtual = (Cliente) session.getAttribute("usuarioLogado");
                
                // Atualiza os campos dele com os dados do formulário
                clienteAtual.setNome(nome);
                clienteAtual.setEmail(email);
                clienteAtual.setCep(cep);
                clienteAtual.setRua(rua);
                clienteAtual.setNumero(numero);
                clienteAtual.setBairro(bairro);
                clienteAtual.setCidade(cidade);
                clienteAtual.setEstado(estado);
                clienteAtual.setSenha(senha); 
                // O ID e CPF (que não estão no form) são preservados no objeto

                // Chama o DAO para salvar no banco
                sucesso = clienteDAO.atualizarCliente(clienteAtual);
                
                if (sucesso) {
                    // Se salvou no banco, ATUALIZA o objeto na sessão
                    session.setAttribute("usuarioLogado", clienteAtual);
                }

            } else if ("fornecedor".equals(tipoUsuario)) {
                // --- ATUALIZAR FORNECEDOR ---

                // Pega o objeto ATUAL da sessão
                Fornecedor fornecedorAtual = (Fornecedor) session.getAttribute("usuarioLogado");
                
                // Pega os campos extras de fornecedor
                String telefone = request.getParameter("telefone");
                String descricao = request.getParameter("descricao");

                // Atualiza os campos dele com os dados do formulário
                fornecedorAtual.setNomeLoja(nome); // Note que o campo 'nome' do form vai para 'nomeLoja'
                fornecedorAtual.setEmail(email);
                fornecedorAtual.setCep(cep);
                fornecedorAtual.setRua(rua);
                fornecedorAtual.setNumero(numero);
                fornecedorAtual.setBairro(bairro);
                fornecedorAtual.setCidade(cidade);
                fornecedorAtual.setEstado(estado);
                fornecedorAtual.setSenha(senha);
                fornecedorAtual.setTelefone(telefone);
                fornecedorAtual.setDescricao(descricao);
                // O ID e CPF/CNPJ são preservados

                // Chama o DAO
                sucesso = fornecedorDAO.atualizarFornecedor(fornecedorAtual);
                
                if (sucesso) {
                    // Se salvou no banco, ATUALIZA o objeto na sessão
                    session.setAttribute("usuarioLogado", fornecedorAtual);
                }
            }

            // --- 3. Redirecionar ---
            if (sucesso) {
                response.sendRedirect("perfil.jsp?msg=sucesso");
            } else {
                response.sendRedirect("perfil.jsp?msg=erro_atualizar");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("perfil.jsp?msg=erro_excecao");
        }
    }
}