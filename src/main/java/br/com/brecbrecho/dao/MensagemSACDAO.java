package br.com.brecbrecho.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList; // Para o futuro painel admin
import java.util.List;     // Para o futuro painel admin
import java.sql.ResultSet;  // Para o futuro painel admin

import br.com.brecbrecho.model.MensagemSAC;
import br.com.brecbrecho.util.ConexaoDB;

public class MensagemSACDAO {

    /**
     * Salva uma nova mensagem do formulário de contato no banco.
     */
    public boolean salvarMensagem(MensagemSAC msg) {
        String sql = "INSERT INTO MensagemSAC (nome, email, mensagem) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, msg.getNome());
            ps.setString(2, msg.getEmail());
            ps.setString(3, msg.getMensagem());
            // data_envio e lida usam o valor DEFAULT do banco

            ps.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /* * Métodos para o futuro Painel Admin (Não usaremos agora, mas já estão prontos)
     */
     
    public List<MensagemSAC> listarTodasMensagens() {
        List<MensagemSAC> mensagens = new ArrayList<>();
        String sql = "SELECT * FROM MensagemSAC ORDER BY data_envio DESC";
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while(rs.next()) {
                MensagemSAC msg = new MensagemSAC();
                msg.setIdMensagem(rs.getInt("id_mensagem"));
                msg.setNome(rs.getString("nome"));
                msg.setEmail(rs.getString("email"));
                msg.setMensagem(rs.getString("mensagem"));
                msg.setDataEnvio(rs.getTimestamp("data_envio"));
                msg.setLida(rs.getBoolean("lida"));
                mensagens.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mensagens;
    }
    
    public boolean marcarComoLida(int idMensagem) {
        String sql = "UPDATE MensagemSAC SET lida = TRUE WHERE id_mensagem = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMensagem);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}