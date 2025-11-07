package br.com.brecbrecho.model;

import java.sql.Timestamp;
import java.util.List; // Para guardar os Itens do Pedido

public class Pedido {

    private int idPedido;
    private int idCliente;
    private Timestamp dataPedido;
    private double valorTotal;
    private String formaPagamento;
    private String statusPedido;

    // Este atributo NÃO existe na tabela.
    // É usado apenas para carregar os itens JUNTOS com o pedido.
    private List<ItemPedido> itens; 
    
    // Construtor vazio
    public Pedido() {
    }

    // Getters e Setters

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Timestamp getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(Timestamp dataPedido) {
        this.dataPedido = dataPedido;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(String statusPedido) {
        this.statusPedido = statusPedido;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }
}