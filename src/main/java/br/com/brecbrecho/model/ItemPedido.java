
package br.com.brecbrecho.model;

public class ItemPedido {

    private int idItemPedido;
    private int idPedido;
    private int idProduto;
    private int quantidade;
    private double precoUnitarioVenda;

    // Este atributo NÃO existe na tabela.
    // É usado para carregar os dados do produto junto com o item.
    private Produto produto;

    // Construtor vazio
    public ItemPedido() {
    }

    // Getters e Setters

    public int getIdItemPedido() {
        return idItemPedido;
    }

    public void setIdItemPedido(int idItemPedido) {
        this.idItemPedido = idItemPedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitarioVenda() {
        return precoUnitarioVenda;
    }

    public void setPrecoUnitarioVenda(double precoUnitarioVenda) {
        this.precoUnitarioVenda = precoUnitarioVenda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}