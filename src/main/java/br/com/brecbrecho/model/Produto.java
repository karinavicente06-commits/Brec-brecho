package br.com.brecbrecho.model;

import java.sql.Timestamp; // Usaremos Timestamp para a data

public class Produto {

    private int idProduto;
    private int idFornecedor; // Chave estrangeira
    private String nome;
    private String descricao;
    private String tamanho;
    private String fotoBase64; // (para o LONGTEXT)
    private int estoque;
    private double preco; // (double para DECIMAL)
    private int prazoLocacaoDias;
    private Timestamp dataCadastro;

    // Construtor vazio
    public Produto() {
    }
    
    // Getters e Setters

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getPrazoLocacaoDias() {
        return prazoLocacaoDias;
    }

    public void setPrazoLocacaoDias(int prazoLocacaoDias) {
        this.prazoLocacaoDias = prazoLocacaoDias;
    }

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}