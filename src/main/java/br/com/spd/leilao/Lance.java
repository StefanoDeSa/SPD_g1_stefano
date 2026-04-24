package br.com.spd.leilao;

import java.time.LocalDateTime;

public class Lance {
    private String autor;
    private double valor;
    private LocalDateTime dataHora;

    public Lance() {
    }

    public Lance(String autor, double valor, LocalDateTime dataHora) {
        this.autor = autor;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}