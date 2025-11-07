package br.com.brecbrecho.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

// Nossos pacotes
import br.com.brecbrecho.util.ConexaoDB;
import br.com.brecbrecho.model.Fornecedor;

public class FornecedorDAO {

	/**
	 * Método para cadastrar um novo fornecedor no banco de dados. (Baseado no seu
	 * 'fornecedor.js')
	 */
	public boolean cadastrarFornecedor(Fornecedor fornecedor) {

		String sql = "INSERT INTO Fornecedor (nome_loja, email, senha, cpf_cnpj, telefone, descricao, cep, rua, numero, bairro, cidade, estado) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String hash = BCrypt.hashpw(fornecedor.getSenha(), BCrypt.gensalt());
		 

		try (Connection conn = ConexaoDB.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, fornecedor.getNomeLoja());
			ps.setString(2, fornecedor.getEmail());
			ps.setString(3, hash); // ATENÇÃO: Aplicar HASH aqui também
			ps.setString(4, fornecedor.getCpfCnpj());
			ps.setString(5, fornecedor.getTelefone());
			ps.setString(6, fornecedor.getDescricao());
			ps.setString(7, fornecedor.getCep());
			ps.setString(8, fornecedor.getRua());
			ps.setString(9, fornecedor.getNumero());
			ps.setString(10, fornecedor.getBairro());
			ps.setString(11, fornecedor.getCidade());
			ps.setString(12, fornecedor.getEstado());

			ps.executeUpdate();
			return true; // Sucesso

		} catch (SQLException e) {
			e.printStackTrace();
			return false; // Falha
		}
	}

	/**
	 * Método para validar o login de um fornecedor. (Baseado no seu 'login.js')
	 */
	public Fornecedor validarLogin(String email, String senha) {

	    // 1. O SQL busca APENAS pelo email.
	    String sql = "SELECT * FROM Fornecedor WHERE email = ?";
	    Fornecedor fornecedor = null;

	    try (Connection conn = ConexaoDB.getConexao(); 
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, email);

	        try (ResultSet rs = ps.executeQuery()) {

	            // 2. Verifica se o usuário (email) existe
	            if (rs.next()) {
	                
	                // 3. Pega o HASH do banco
	                String hashArmazenado = rs.getString("senha");

	                // 4. Compara a SENHA DO FORM (texto puro) com o HASH DO BANCO
	                if (BCrypt.checkpw(senha, hashArmazenado)) {
	                    
	                    // 5. Senha correta! Monta o objeto Fornecedor
	                    fornecedor = new Fornecedor();
	                    fornecedor.setIdFornecedor(rs.getInt("id_fornecedor"));
	                    fornecedor.setNomeLoja(rs.getString("nome_loja"));
	                    fornecedor.setEmail(rs.getString("email"));
	                    fornecedor.setCpfCnpj(rs.getString("cpf_cnpj"));
	                    fornecedor.setTelefone(rs.getString("telefone"));
	                    fornecedor.setDescricao(rs.getString("descricao"));
	                    fornecedor.setCep(rs.getString("cep"));
	                    fornecedor.setRua(rs.getString("rua"));
	                    fornecedor.setNumero(rs.getString("numero"));
	                    fornecedor.setBairro(rs.getString("bairro"));
	                    fornecedor.setCidade(rs.getString("cidade"));
	                    fornecedor.setEstado(rs.getString("estado"));
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return fornecedor; // Retorna o Fornecedor ou null
	}
	public boolean atualizarFornecedor(Fornecedor fornecedor) {

		// O SQL de UPDATE. Não permitimos alterar CPF/CNPJ ou ID.
		String sql = "UPDATE Fornecedor SET nome_loja = ?, email = ?, senha = ?, "
				+ "telefone = ?, descricao = ?, cep = ?, rua = ?, numero = ?, " + "bairro = ?, cidade = ?, estado = ? "
				+ "WHERE id_fornecedor = ?";
		
		String hash = BCrypt.hashpw(fornecedor.getSenha(), BCrypt.gensalt());


		try (Connection conn = ConexaoDB.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, fornecedor.getNomeLoja());
			ps.setString(2, fornecedor.getEmail());
			ps.setString(3, hash); // Lembre-se do HASH
			ps.setString(4, fornecedor.getTelefone());
			ps.setString(5, fornecedor.getDescricao());
			ps.setString(6, fornecedor.getCep());
			ps.setString(7, fornecedor.getRua());
			ps.setString(8, fornecedor.getNumero());
			ps.setString(9, fornecedor.getBairro());
			ps.setString(10, fornecedor.getCidade());
			ps.setString(11, fornecedor.getEstado());

			// Cláusula WHERE
			ps.setInt(12, fornecedor.getIdFornecedor());

			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0; // true se atualizou

		} catch (SQLException e) {
			e.printStackTrace();
			// Pode falhar se o email novo já existir (UNIQUE constraint)
			return false;
		}
	}

}