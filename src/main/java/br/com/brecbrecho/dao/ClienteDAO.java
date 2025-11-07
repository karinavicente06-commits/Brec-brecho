package br.com.brecbrecho.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

// Importa sua classe de conexão
import br.com.brecbrecho.util.ConexaoDB;
// Importa o Model
import br.com.brecbrecho.model.Cliente;

public class ClienteDAO {

	/**
	 * Método para cadastrar um novo cliente no banco de dados. (Baseado no seu
	 * 'cliente.js')
	 */
	public boolean cadastrarCliente(Cliente cliente) {

		// 1. Comando SQL de inserção
		String sql = "INSERT INTO Cliente (nome, email, senha, cpf, cep, rua, numero, bairro, cidade, estado) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String hash = BCrypt.hashpw(cliente.getSenha(), BCrypt.gensalt());

		// 2. Usamos try-with-resources para garantir que a conexão e o statement fechem
		// Note o uso da sua classe: ConexaoDB.getConexao()
		try (Connection conn = ConexaoDB.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// 3. Define os parâmetros (?) do SQL com os dados do objeto Cliente
			ps.setString(1, cliente.getNome());
			ps.setString(2, cliente.getEmail());

			// ATENÇÃO: HASH DE SENHA
			// Por segurança, NUNCA salve senhas em texto puro.
			// Vamos salvar por enquanto, mas o ideal é usar uma biblioteca (ex: BCrypt)
			// String hash = BCrypt.hashpw(cliente.getSenha(), BCrypt.gensalt());
			// ps.setString(3, hash);
			ps.setString(3,hash); // Provisório

			ps.setString(4, cliente.getCpf());
			ps.setString(5, cliente.getCep());
			ps.setString(6, cliente.getRua());
			ps.setString(7, cliente.getNumero());
			ps.setString(8, cliente.getBairro());
			ps.setString(9, cliente.getCidade());
			ps.setString(10, cliente.getEstado());

			// 4. Executa o comando
			ps.executeUpdate();

			return true; // Retorna true se a inserção foi bem-sucedida

		} catch (SQLException e) {
			e.printStackTrace();
			// Você pode querer tratar exceções específicas, como email/CPF duplicado
			return false; // Retorna false se deu erro
		}
	}

	/**
	 * Método para validar o login de um cliente. (Baseado no seu 'login.js')
	 */
	public Cliente validarLogin(String email, String senha) {

	    // 1. O SQL busca APENAS pelo email.
	    String sql = "SELECT * FROM Cliente WHERE email = ?";
	    Cliente cliente = null;

	    try (Connection conn = ConexaoDB.getConexao(); 
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, email);

	        try (ResultSet rs = ps.executeQuery()) {

	            // 2. Verifica se o usuário (email) existe no banco
	            if (rs.next()) {
	                // 3. Se existe, pega o HASH que está salvo no banco
	                String hashArmazenado = rs.getString("senha");

	                // 4. Compara a SENHA DO FORM (texto puro) com o HASH DO BANCO
	                if (BCrypt.checkpw(senha, hashArmazenado)) {
	                    
	                    // 5. Senha correta! Monta o objeto Cliente para a sessão
	                    cliente = new Cliente();
	                    cliente.setIdCliente(rs.getInt("id_cliente"));
	                    cliente.setNome(rs.getString("nome"));
	                    cliente.setEmail(rs.getString("email"));
	                    cliente.setCpf(rs.getString("cpf"));
	                    cliente.setCep(rs.getString("cep"));
	                    cliente.setRua(rs.getString("rua"));
	                    cliente.setNumero(rs.getString("numero"));
	                    cliente.setBairro(rs.getString("bairro"));
	                    cliente.setCidade(rs.getString("cidade"));
	                    cliente.setEstado(rs.getString("estado"));
	                    // Não populamos a senha por segurança
	                }
	                // Se checkpw for falso, a senha está errada e o 'cliente' continua null.
	            }
	            // Se rs.next() for falso, o email não existe e o 'cliente' continua null.
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return cliente; // Retorna o objeto Cliente preenchido se o login for válido, ou null se for inválido.
	}

	public boolean atualizarCliente(Cliente cliente) {

		// O SQL de UPDATE. Não permitimos alterar CPF ou ID.
		String sql = "UPDATE Cliente SET nome = ?, email = ?, senha = ?, cep = ?, "
				+ "rua = ?, numero = ?, bairro = ?, cidade = ?, estado = ? " + "WHERE id_cliente = ?";

		String hash = BCrypt.hashpw(cliente.getSenha(), BCrypt.gensalt());
		try (Connection conn = ConexaoDB.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, cliente.getNome());
			ps.setString(2, cliente.getEmail());
			ps.setString(3, hash); // Lembre-se do HASH
			ps.setString(4, cliente.getCep());
			ps.setString(5, cliente.getRua());
			ps.setString(6, cliente.getNumero());
			ps.setString(7, cliente.getBairro());
			ps.setString(8, cliente.getCidade());
			ps.setString(9, cliente.getEstado());

			// Cláusula WHERE
			ps.setInt(10, cliente.getIdCliente());

			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0; // Retorna true se 1 linha foi atualizada

		} catch (SQLException e) {
			e.printStackTrace();
			// Pode falhar se o email novo já existir (UNIQUE constraint)
			return false;
		}
	}

	// TODO: Criar método para deletar conta
	// public boolean deletarCliente(int idCliente) { ... }

}