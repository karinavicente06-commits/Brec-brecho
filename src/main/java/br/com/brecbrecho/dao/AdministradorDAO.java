package br.com.brecbrecho.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt; // Importa o BCrypt

import br.com.brecbrecho.model.Administrador;
import br.com.brecbrecho.util.ConexaoDB;

public class AdministradorDAO {

    /**
     * Valida o login de um Administrador.
     */
    public Administrador validarLogin(String email, String senha) {
        
        // 1. O SQL busca APENAS pelo email.
        String sql = "SELECT * FROM Administrador WHERE email = ?";
        Administrador admin = null;

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                // 2. Verifica se o admin (email) existe no banco
                if (rs.next()) {
                    // 3. Se existe, pega o HASH que está salvo no banco
                    String hashArmazenado = rs.getString("senha");

                    // 4. Compara a SENHA DO FORM (texto puro) com o HASH DO BANCO
                    if (BCrypt.checkpw(senha, hashArmazenado)) {
                        
                        // 5. Senha correta! Monta o objeto Administrador
                        admin = new Administrador();
                        admin.setIdAdmin(rs.getInt("id_admin"));
                        admin.setNome(rs.getString("nome"));
                        admin.setEmail(rs.getString("email"));
                        // Não populamos a senha por segurança
                    }
                    // Se checkpw for falso, a senha está errada e 'admin' continua null.
                }
                // Se rs.next() for falso, o email não existe e 'admin' continua null.
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admin; // Retorna o objeto Admin preenchido se o login for válido, ou null se for inválido.
    }
 // Dentro da classe AdministradorDAO.java ...

    /**
     * Cadastra um novo administrador no banco, já criptografando a senha.
     */
    public boolean cadastrarAdministrador(Administrador admin) {
        
        String sql = "INSERT INTO Administrador (nome, email, senha) VALUES (?, ?, ?)";
        
        // Pega a senha PURA e cria o HASH
        String hash = BCrypt.hashpw(admin.getSenha(), BCrypt.gensalt());

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, admin.getNome());
            ps.setString(2, admin.getEmail());
            ps.setString(3, hash); // Salva o HASH correto

            ps.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}