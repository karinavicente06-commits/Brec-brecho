package br.com.brecbrecho.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // Importar ArrayList
import java.util.List;     // Importar List

import br.com.brecbrecho.util.ConexaoDB;
import br.com.brecbrecho.model.Produto;

public class ProdutoDAO {

    /**
     * Método para CADASTRAR um novo produto.
     * (Baseado no seu 'produto.js' -> salvarProduto)
     */
    public boolean cadastrarProduto(Produto produto) {
        String sql = "INSERT INTO Produto (id_fornecedor, nome, descricao, tamanho, foto_base64, estoque, preco, prazo_locacao_dias) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, produto.getIdFornecedor());
            ps.setString(2, produto.getNome());
            ps.setString(3, produto.getDescricao());
            ps.setString(4, produto.getTamanho());
            ps.setString(5, produto.getFotoBase64());
            ps.setInt(6, produto.getEstoque());
            ps.setDouble(7, produto.getPreco());
            ps.setInt(8, produto.getPrazoLocacaoDias());
            
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método para ATUALIZAR um produto existente.
     * (Baseado no seu 'produto.js' -> salvarProduto em modo de edição)
     */
    public boolean atualizarProduto(Produto produto) {
        String sql = "UPDATE Produto SET nome = ?, descricao = ?, tamanho = ?, " +
                     "foto_base64 = ?, estoque = ?, preco = ?, prazo_locacao_dias = ? " +
                     "WHERE id_produto = ?";
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setString(3, produto.getTamanho());
            ps.setString(4, produto.getFotoBase64());
            ps.setInt(5, produto.getEstoque());
            ps.setDouble(6, produto.getPreco());
            ps.setInt(7, produto.getPrazoLocacaoDias());
            ps.setInt(8, produto.getIdProduto()); // Cláusula WHERE
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Retorna true se a linha foi (rowsAffected == 1)
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método para EXCLUIR um produto.
     * (Baseado no seu 'produto.js' -> excluirProduto)
     */
    public boolean excluirProduto(int idProduto) {
        String sql = "DELETE FROM Produto WHERE id_produto = ?";
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProduto);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Isso irá falhar se o produto estiver em um ItemPedido (DELETE RESTRICT)
            // O que é o comportamento correto.
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método para LISTAR TODOS os produtos (para o catálogo público).
     * (Baseado no seu 'catalogo.js' -> carregarCatalogo)
     */
    public List<Produto> listarTodosProdutos() {
        String sql = "SELECT * FROM Produto ORDER BY data_cadastro DESC";
        List<Produto> produtos = new ArrayList<>();
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                produtos.add(populateProduto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    /**
     * Método para LISTAR produtos de UM FORNECEDOR específico.
     * (Usado na área "Meus Produtos" do fornecedor, em 'produto.js' -> carregarCatalogo)
     */
    public List<Produto> listarProdutosPorFornecedor(int idFornecedor) {
        String sql = "SELECT * FROM Produto WHERE id_fornecedor = ? ORDER BY data_cadastro DESC";
        List<Produto> produtos = new ArrayList<>();
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idFornecedor);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    produtos.add(populateProduto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    /**
     * Método para BUSCAR UM produto pelo ID.
     * (Usado no 'catalogo.js' -> adicionarAoCarrinho para pegar os dados do produto)
     */
    public Produto buscarProdutoPorId(int idProduto) {
        String sql = "SELECT * FROM Produto WHERE id_produto = ?";
        Produto produto = null;
        
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProduto);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    produto = populateProduto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produto;
    }

    /**
     * Método auxiliar PRIVADO para "popular" um objeto Produto
     * a partir de um ResultSet, evitando repetição de código.
     */
    private Produto populateProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setIdProduto(rs.getInt("id_produto"));
        produto.setIdFornecedor(rs.getInt("id_fornecedor"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setTamanho(rs.getString("tamanho"));
        produto.setFotoBase64(rs.getString("foto_base64"));
        produto.setEstoque(rs.getInt("estoque"));
        produto.setPreco(rs.getDouble("preco"));
        produto.setPrazoLocacaoDias(rs.getInt("prazo_locacao_dias"));
        produto.setDataCadastro(rs.getTimestamp("data_cadastro"));
        return produto;
    }
}