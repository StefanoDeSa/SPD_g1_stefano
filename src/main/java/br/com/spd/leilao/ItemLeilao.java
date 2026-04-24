package br.com.spd.leilao;

public class ItemLeilao {
    private String nome;
    private String descricao;
    private double valorInicial;

    public ItemLeilao() {
    }

    public ItemLeilao(String nome, String descricao, double valorInicial) {
        this.nome = nome;
        this.descricao = descricao;
        this.valorInicial = valorInicial;
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

    public double getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(double valorInicial) {
        this.valorInicial = valorInicial;
    }
}