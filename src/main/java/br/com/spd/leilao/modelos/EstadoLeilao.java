package br.com.spd.leilao.modelos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstadoLeilao {
    private ItemLeilao item;
    private boolean aberto;
    private boolean encerrado;
    private Lance maiorLance;
    private final List<Lance> lances;

    public EstadoLeilao() {
        this.lances = new ArrayList<>();
        this.aberto = false;
        this.encerrado = false;
    }

    public synchronized String cadastrarItem(String nome, String descricao, double valorInicial) {
        if (aberto) {
            return "ERRO|Já existe um leilão em andamento.";
        }

        if (encerrado) {
            return "ERRO|O leilão anterior já foi encerrado. Reinicie o servidor para um novo teste.";
        }

        this.item = new ItemLeilao(nome, descricao, valorInicial);
        this.maiorLance = null;
        this.lances.clear();
        this.aberto = true;

        return "OK|Item cadastrado com sucesso.";
    }

    public synchronized String registrarLance(String autor, double valor) {
        if (!aberto || item == null) {
            return "ERRO|Não há leilão aberto.";
        }

        if (encerrado) {
            return "ERRO|O leilão já foi encerrado.";
        }

        double valorAtual = getValorAtual();

        if (valor <= valorAtual) {
            return "ERRO|O lance deve ser maior que o valor atual.";
        }

        Lance lance = new Lance(autor, valor, LocalDateTime.now());
        this.maiorLance = lance;
        this.lances.add(lance);

        return "OK|Lance registrado com sucesso.";
    }

    public synchronized String encerrarLeilao() {
        if (!aberto || item == null) {
            return "ERRO|Não há leilão aberto.";
        }

        if (encerrado) {
            return "ERRO|O leilão já foi encerrado.";
        }

        this.aberto = false;
        this.encerrado = true;

        if (maiorLance == null) {
            return "OK|Leilão encerrado sem lances.";
        }

        return "OK|Leilão encerrado. Vencedor: " + maiorLance.getAutor() + " | Valor: " + maiorLance.getValor();
    }

    public synchronized double getValorAtual() {
        if (maiorLance != null) {
            return maiorLance.getValor();
        }

        if (item != null) {
            return item.getValorInicial();
        }

        return 0.0;
    }

    public synchronized String getAutorMaiorLance() {
        return maiorLance != null ? maiorLance.getAutor() : "Ninguém";
    }

    public synchronized String consultarEstado() {
        if (item == null) {
            return "ESTADO|SEM_ITEM";
        }

        String status;
        if (encerrado) {
            status = "ENCERRADO";
        } else if (aberto) {
            status = "ABERTO";
        } else {
            status = "INATIVO";
        }

        return "ESTADO|" + status +
                "|" + item.getNome() +
                "|" + getValorAtual() +
                "|" + getAutorMaiorLance();
    }

    public synchronized ItemLeilao getItem() {
        return item;
    }

    public synchronized Lance getMaiorLance() {
        return maiorLance;
    }

    public synchronized List<Lance> getLances() {
        return new ArrayList<>(lances);
    }

    public synchronized boolean isAberto() {
        return aberto;
    }

    public synchronized boolean isEncerrado() {
        return encerrado;
    }
}