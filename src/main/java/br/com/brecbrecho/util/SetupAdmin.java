package br.com.brecbrecho.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

import br.com.brecbrecho.dao.AdministradorDAO;
import br.com.brecbrecho.model.Administrador;

/**
 * CLASSE TEMPORÁRIA
 * Rode esta classe UMA VEZ como "Java Application"
 * para criar o usuário admin corretamente no banco.
 */
public class SetupAdmin {

    public static void main(String[] args) {
        
        String email = "admin@brecbrecho.com";
        String senhaPura = "admin123";

        // 1. Limpa o admin antigo (para garantir)
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Administrador WHERE email = ?")) {
             
             ps.setString(1, email);
             int rowsDeleted = ps.executeUpdate();
             System.out.println("Admin antigo deletado (se existia): " + rowsDeleted);
             
        } catch (Exception e) {
            System.err.println("Erro ao limpar admin antigo:");
            e.printStackTrace();
            return; // Sai se não conseguir limpar
        }

        // 2. Cria o novo admin usando o DAO
        System.out.println("Tentando criar novo admin...");
        AdministradorDAO dao = new AdministradorDAO();
        Administrador admin = new Administrador();
        
        admin.setNome("Admin Principal");
        admin.setEmail(email);
        admin.setSenha(senhaPura); // Passa a senha PURA para o DAO

        boolean sucesso = dao.cadastrarAdministrador(admin);

        if (sucesso) {
            System.out.println("==================================================");
            System.out.println("SUCESSO! Usuário Admin criado no banco.");
            System.out.println("Email: " + email);
            System.out.println("Senha: " + senhaPura);
            System.out.println("==================================================");
        } else {
            System.err.println("==================================================");
            System.err.println("FALHA! Não foi possível criar o usuário admin.");
            System.err.println("==================================================");
        }
    }
}