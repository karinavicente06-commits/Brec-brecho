package br.com.brecbrecho.controller;

// ATENÇÃO: Imports são 'jakarta', não 'javax'
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Nossos pacotes de Model e DAO
import br.com.brecbrecho.model.Cliente;
import br.com.brecbrecho.dao.ClienteDAO;

/**
 * Servlet para lidar com o cadastro de clientes.
 * Ele "ouve" na URL /cadastrarCliente (definido no @WebServlet)
 */
@WebServlet("/cadastrarCliente") 
public class ClienteServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L; // Padrão para servlets
    private ClienteDAO clienteDAO;

    // init() é chamado quando o servlet é carregado pela primeira vez
    public void init() {
        clienteDAO = new ClienteDAO(); // Instanciamos o DAO uma vez
    }

    /**
     * O método doPost() é chamado quando o formulário é enviado com method="POST"
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Coletar os dados do formulário (via request.getParameter)
            //    Os nomes "nome", "email", etc., devem ser EXATAMENTE iguais
            //    aos atributos 'name' dos <input> no seu formulário.
            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String cpf = request.getParameter("cpf");
            String senha = request.getParameter("senha"); // (Lembrar de implementar HASH)
            String cep = request.getParameter("cep");
            String rua = request.getParameter("rua");
            String numero = request.getParameter("numero");
            String bairro = request.getParameter("bairro");
            String cidade = request.getParameter("cidade");
            String estado = request.getParameter("estado");
            
            // 2. Criar o objeto Model (Cliente)
            Cliente novoCliente = new Cliente();
            novoCliente.setNome(nome);
            novoCliente.setEmail(email);
            novoCliente.setCpf(cpf);
            novoCliente.setSenha(senha);
            novoCliente.setCep(cep);
            novoCliente.setRua(rua);
            novoCliente.setNumero(numero);
            novoCliente.setBairro(bairro);
            novoCliente.setCidade(cidade);
            novoCliente.setEstado(estado);

            // 3. Chamar o DAO para salvar no banco
            boolean sucesso = clienteDAO.cadastrarCliente(novoCliente);

            // 4. Redirecionar o usuário
            if (sucesso) {
                // Deu certo: envia para a página de login com uma mensagem de sucesso
                // (Vamos criar o login.jsp em breve)
                response.sendRedirect("login.jsp?msg=sucesso");
            } else {
                // Deu erro: devolve para a página de cadastro com mensagem de erro
                // (Vamos criar o cliente.jsp em breve)
                response.sendRedirect("cliente.jsp?msg=erro");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de exceção, devolve para a página de cadastro
            response.sendRedirect("cliente.jsp?msg=erro_excecao");
        }
    }
}