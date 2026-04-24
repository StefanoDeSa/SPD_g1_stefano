package br.com.spd.leilao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorLeilao {
    private static final int PORTA_TCP = 5000;
    private static final String CAMINHO_USUARIOS = "dados/usuarios.json";
    private static final String CAMINHO_HISTORICO = "dados/historico-leilao.json";

    private final EstadoLeilao estadoLeilao;
    private final List<Usuario> usuarios;
    private final Map<String, ClienteUdpInfo> clientesUdp;

    public ServidorLeilao() throws IOException {
        this.estadoLeilao = new EstadoLeilao();
        this.usuarios = JsonUtil.carregarUsuarios(CAMINHO_USUARIOS);
        this.clientesUdp = new ConcurrentHashMap<>();
    }

    public void iniciar() throws IOException {
        try (ServerSocket servidor = new ServerSocket(PORTA_TCP, 50, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("Servidor de leilão iniciado na porta TCP " + PORTA_TCP);
            System.out.println("Servidor escutando em 0.0.0.0 (todas as interfaces de rede)");

            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Novo cliente conectado: " + socketCliente.getInetAddress().getHostAddress());

                TratadorCliente tratador = new TratadorCliente(socketCliente, this);
                Thread thread = new Thread(tratador);
                thread.start();
            }
        }
    }
    public Usuario autenticar(String login, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login) && usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }
        return null;
    }

    public void registrarClienteUdp(String login, InetAddress endereco, int portaUdp) {
        clientesUdp.put(login, new ClienteUdpInfo(endereco, portaUdp));
        System.out.println("Cliente UDP registrado: " + login + " -> " + endereco.getHostAddress() + ":" + portaUdp);
    }

    public void removerCliente(String login) {
        if (login != null) {
            clientesUdp.remove(login);
            System.out.println("Cliente removido: " + login);
        }
    }

    public synchronized String processarCadastroItem(Usuario usuario, String nome, String descricao, double valorInicial) {
        if (usuario == null || !usuario.isAdmin()) {
            return "ERRO|Apenas o administrador pode cadastrar item.";
        }

        String resposta = estadoLeilao.cadastrarItem(nome, descricao, valorInicial);

        if (resposta.startsWith("OK|")) {
            enviarNotificacaoUdpParaTodos("ITEM_CADASTRADO|" + nome + "|" + valorInicial);
        }

        return resposta;
    }

    public synchronized String processarLance(Usuario usuario, double valor) {
        if (usuario == null || !usuario.isComprador()) {
            return "ERRO|Apenas compradores podem enviar lances.";
        }

        String resposta = estadoLeilao.registrarLance(usuario.getLogin(), valor);

        if (resposta.startsWith("OK|")) {
            enviarNotificacaoUdpParaTodos("NOVO_LANCE|" + usuario.getLogin() + "|" + valor);
        }

        return resposta;
    }

    public String processarConsultaEstado() {
        return estadoLeilao.consultarEstado();
    }

    public synchronized String processarEncerramento(Usuario usuario) {
        if (usuario == null || !usuario.isAdmin()) {
            return "ERRO|Apenas o administrador pode encerrar o leilão.";
        }

        String resposta = estadoLeilao.encerrarLeilao();

        if (resposta.startsWith("OK|")) {
            try {
                JsonUtil.salvarHistorico(CAMINHO_HISTORICO, estadoLeilao);
            } catch (IOException e) {
                return "ERRO|Leilão encerrado, mas houve falha ao salvar o histórico: " + e.getMessage();
            }

            if (estadoLeilao.getMaiorLance() == null) {
                enviarNotificacaoUdpParaTodos("LEILAO_ENCERRADO|SEM_VENCEDOR|0.0");
            } else {
                enviarNotificacaoUdpParaTodos(
                        "LEILAO_ENCERRADO|" +
                                estadoLeilao.getMaiorLance().getAutor() +
                                "|" +
                                estadoLeilao.getMaiorLance().getValor()
                );
            }
        }

        return resposta;
    }

    private void enviarNotificacaoUdpParaTodos(String mensagem) {
        byte[] dados = mensagem.getBytes(StandardCharsets.UTF_8);

        try (DatagramSocket socketUdp = new DatagramSocket()) {
            for (ClienteUdpInfo cliente : clientesUdp.values()) {
                DatagramPacket pacote = new DatagramPacket(
                        dados,
                        dados.length,
                        cliente.endereco(),
                        cliente.portaUdp()
                );
                socketUdp.send(pacote);
            }
        } catch (IOException e) {
            System.out.println("Falha ao enviar notificação UDP: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            ServidorLeilao servidor = new ServidorLeilao();
            servidor.iniciar();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private record ClienteUdpInfo(InetAddress endereco, int portaUdp) {
    }
}