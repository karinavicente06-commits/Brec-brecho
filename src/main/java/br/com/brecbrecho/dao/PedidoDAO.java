package br.com.brecbrecho.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Importar java.sql.Statement
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap; // Para manter a ordem dos pedidos

import br.com.brecbrecho.util.ConexaoDB;
import br.com.brecbrecho.model.Pedido;
import br.com.brecbrecho.model.ItemPedido;
import br.com.brecbrecho.model.Produto;

public class PedidoDAO {

    /**
     * Salva um pedido completo (Pedido + Itens) usando uma transação.
     * (Baseado no seu 'carrinho.js' -> btnFinalizarCompra)
     */
    public boolean salvarPedido(Pedido pedido) {
        
        String sqlPedido = "INSERT INTO Pedido (id_cliente, valor_total, forma_pagamento, status_pedido) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO ItemPedido (id_pedido, id_produto, quantidade, preco_unitario_venda) VALUES (?, ?, ?, ?)";
        String sqlEstoque = "UPDATE Produto SET estoque = estoque - ? WHERE id_produto = ?";

        Connection conn = null;
        PreparedStatement psPedido = null;
        PreparedStatement psItem = null;
        PreparedStatement psEstoque = null;
        ResultSet generatedKeys = null;
        boolean sucesso = false;

        try {
            conn = ConexaoDB.getConexao();
            // Desliga o AutoCommit para iniciar a TRANSAÇÃO
            conn.setAutoCommit(false); 

            // --- 1. Inserir o Pedido ---
            psPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setInt(1, pedido.getIdCliente());
            psPedido.setDouble(2, pedido.getValorTotal());
            psPedido.setString(3, pedido.getFormaPagamento());
            psPedido.setString(4, "PENDENTE"); // Status inicial
            psPedido.executeUpdate();

            // --- 2. Obter o ID gerado para o Pedido ---
            generatedKeys = psPedido.getGeneratedKeys();
            int idPedidoGerado;
            if (generatedKeys.next()) {
                idPedidoGerado = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Falha ao obter ID do pedido, nenhum ID gerado.");
            }

            // --- 3. Inserir os Itens do Pedido ---
            psItem = conn.prepareStatement(sqlItem);
            // --- 4. Atualizar o Estoque ---
            psEstoque = conn.prepareStatement(sqlEstoque);

            // Loop sobre os itens que estão dentro do objeto Pedido
            for (ItemPedido item : pedido.getItens()) {
                // Adiciona o item ao batch do psItem
                psItem.setInt(1, idPedidoGerado);
                psItem.setInt(2, item.getIdProduto());
                psItem.setInt(3, item.getQuantidade());
                psItem.setDouble(4, item.getPrecoUnitarioVenda());
                psItem.addBatch(); // Adiciona para execução em lote

                // Adiciona a atualização de estoque ao batch do psEstoque
                psEstoque.setInt(1, item.getQuantidade()); // A quantidade a ser subtraída
                psEstoque.setInt(2, item.getIdProduto());
                psEstoque.addBatch();
            }

            psItem.executeBatch(); // Executa todos os INSERTs de ItemPedido
            psEstoque.executeBatch(); // Executa todos os UPDATEs de Produto (estoque)

            // Se tudo deu certo até aqui, comita a transação
            conn.commit(); 
            sucesso = true;

        } catch (SQLException e) {
            // Se qualquer erro ocorrer, faz o ROLLOBACK
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Fecha todas as conexões e reativa o autoCommit
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
            try { if (psPedido != null) psPedido.close(); } catch (SQLException e) {}
            try { if (psItem != null) psItem.close(); } catch (SQLException e) {}
            try { if (psEstoque != null) psEstoque.close(); } catch (SQLException e) {}
            try { 
                if (conn != null) { 
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } 
            } catch (SQLException e) {}
        }
        
        return sucesso;
    }

    /**
     * Lista todos os pedidos de um cliente específico (para a pág "Meus Pedidos").
     */
    public List<Pedido> listarPedidosPorCliente(int idCliente) {
        
        // Query complexa que junta Pedido, ItemPedido e Produto
        String sql = "SELECT p.*, i.*, pr.nome as nome_produto, pr.foto_base64 " +
                       "FROM Pedido p " +
                       "JOIN ItemPedido i ON p.id_pedido = i.id_pedido " +
                       "JOIN Produto pr ON i.id_produto = pr.id_produto " +
                       "WHERE p.id_cliente = ? " +
                       "ORDER BY p.data_pedido DESC, i.id_item_pedido ASC";

        // Usamos um Map para agrupar os itens dentro de seus respectivos pedidos
        // Um LinkedHashMap é usado para manter a ordem de inserção (pedidos mais recentes primeiro)
        Map<Integer, Pedido> pedidoMap = new LinkedHashMap<>();

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPedido = rs.getInt("p.id_pedido");
                    
                    // Verifica se já criamos o objeto Pedido para este ID
                    Pedido pedido = pedidoMap.get(idPedido);

                    // Se for a primeira vez que vemos este ID de pedido, criamos o objeto Pedido
                    if (pedido == null) {
                        pedido = new Pedido();
                        pedido.setIdPedido(idPedido);
                        pedido.setIdCliente(rs.getInt("p.id_cliente"));
                        pedido.setDataPedido(rs.getTimestamp("p.data_pedido"));
                        pedido.setValorTotal(rs.getDouble("p.valor_total"));
                        pedido.setFormaPagamento(rs.getString("p.forma_pagamento"));
                        pedido.setStatusPedido(rs.getString("p.status_pedido"));
                        pedido.setItens(new ArrayList<>()); // Inicializa a lista de itens
                        
                        // Adiciona o pedido recém-criado ao Map
                        pedidoMap.put(idPedido, pedido);
                    }

                    // Agora, criamos o objeto ItemPedido para esta LINHA do ResultSet
                    ItemPedido item = new ItemPedido();
                    item.setIdItemPedido(rs.getInt("i.id_item_pedido"));
                    item.setIdPedido(idPedido);
                    item.setIdProduto(rs.getInt("i.id_produto"));
                    item.setQuantidade(rs.getInt("i.quantidade"));
                    item.setPrecoUnitarioVenda(rs.getDouble("i.preco_unitario_venda"));
                    
                    // Também populamos o objeto Produto aninhado dentro do Item
                    Produto produto = new Produto();
                    produto.setIdProduto(rs.getInt("i.id_produto"));
                    produto.setNome(rs.getString("nome_produto"));
                    produto.setFotoBase64(rs.getString("pr.foto_base64"));
                    item.setProduto(produto); // Anexa o produto ao item

                    // Adiciona este item à lista de itens do seu Pedido "pai"
                    pedido.getItens().add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Retorna a lista de Pedidos (já com seus itens aninhados)
        return new ArrayList<>(pedidoMap.values());
    }
}