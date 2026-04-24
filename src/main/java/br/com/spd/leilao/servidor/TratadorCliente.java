package br.com.spd.leilao.servidor;

import br.com.spd.leilao.modelos.Usuario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TratadorCliente implements Runnable {
    private final Socket socket;
    private final ServidorLeilao servidor;

    private BufferedReader entrada;
    private PrintWriter saida;
    private Usuario usuarioAutenticado;

    public TratadorCliente(Socket socket, ServidorLeilao servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);

            saida.println("OK|Conexão estabelecida com o servidor.");

            String linha;
            while ((linha = entrada.readLine()) != null) {
                String resposta = processarComando(linha);

                if (resposta != null) {
                    saida.println(resposta);
                }

                if ("SAIR".equalsIgnoreCase(linha.trim())) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Conexão encerrada com cliente: " + e.getMessage());
        } finally {
            encerrarConexao();
        }
    }

    private String processarComando(String comandoCompleto) {
        if (comandoCompleto == null || comandoCompleto.isBlank()) {
            return "ERRO|Comando vazio.";
        }

        String[] partes = comandoCompleto.split("\\|");
        String comando = partes[0].trim().toUpperCase();

        try {
            switch (comando) {
                case "LOGIN":
                    return processarLogin(partes);

                case "CADASTRAR_ITEM":
                    return processarCadastroItem(partes);

                case "LANCE":
                    return processarLance(partes);

                case "ESTADO":
                    return servidor.processarConsultaEstado();

                case "ENCERRAR":
                    if (!usuarioLogado()) {
                        return "ERRO|Você precisa fazer login antes.";
                    }
                    return servidor.processarEncerramento(usuarioAutenticado);

                case "SAIR":
                    return "OK|Conexão encerrada.";

                default:
                    return "ERRO|Comando inválido.";
            }
        } catch (NumberFormatException e) {
            return "ERRO|Valor numérico inválido.";
        } catch (Exception e) {
            return "ERRO|Falha ao processar comando: " + e.getMessage();
        }
    }

    private String processarLogin(String[] partes) {
        if (partes.length < 4) {
            return "ERRO|Use: LOGIN|usuario|senha|portaUdp";
        }

        String login = partes[1].trim();
        String senha = partes[2].trim();
        int portaUdp = Integer.parseInt(partes[3].trim());

        Usuario usuario = servidor.autenticar(login, senha);

        if (usuario == null) {
            return "ERRO|Login ou senha inválidos.";
        }

        this.usuarioAutenticado = usuario;
        servidor.registrarClienteUdp(login, socket.getInetAddress(), portaUdp);

        return "OK|Login realizado com sucesso|PAPEL|" + usuario.getPapel();
    }

    private String processarCadastroItem(String[] partes) {
        if (!usuarioLogado()) {
            return "ERRO|Você precisa fazer login antes.";
        }

        if (partes.length < 4) {
            return "ERRO|Use: CADASTRAR_ITEM|nome|descricao|valorInicial";
        }

        String nome = partes[1].trim();
        String descricao = partes[2].trim();
        double valorInicial = Double.parseDouble(partes[3].trim());

        return servidor.processarCadastroItem(usuarioAutenticado, nome, descricao, valorInicial);
    }

    private String processarLance(String[] partes) {
        if (!usuarioLogado()) {
            return "ERRO|Você precisa fazer login antes.";
        }

        if (partes.length < 2) {
            return "ERRO|Use: LANCE|valor";
        }

        double valor = Double.parseDouble(partes[1].trim());
        return servidor.processarLance(usuarioAutenticado, valor);
    }

    private boolean usuarioLogado() {
        return usuarioAutenticado != null;
    }

    private void encerrarConexao() {
        try {
            if (usuarioAutenticado != null) {
                servidor.removerCliente(usuarioAutenticado.getLogin());
            }

            if (entrada != null) {
                entrada.close();
            }

            if (saida != null) {
                saida.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao encerrar conexão do cliente: " + e.getMessage());
        }
    }
}