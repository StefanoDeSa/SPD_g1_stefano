package br.com.spd.leilao;

public class Usuario {
    private String login;
    private String senha;
    private String papel;

    public Usuario() {
    }

    public Usuario(String login, String senha, String papel) {
        this.login = login;
        this.senha = senha;
        this.papel = papel;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(papel);
    }

    public boolean isComprador() {
        return "COMPRADOR".equalsIgnoreCase(papel);
    }
}