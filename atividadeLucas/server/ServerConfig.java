package atividadeLucas.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConfig {
    private ServerSocket servidor;
    private ExecutorService pool;
    // amrmazena ips
    private Set<String> ipsAtivos;
    //perguntas
    private Map<String, String> baseDeDados;

    public void startServer(int porta) {
        ipsAtivos = ConcurrentHashMap.newKeySet();
        pool = Executors.newFixedThreadPool(5);
        inicializarBaseDeDados();

        try {
            servidor = new ServerSocket(porta);
            System.out.println("Servidor Climático operando na porta " + porta);

            while (true) {
                Socket cliente = servidor.accept();
                String ipCliente = cliente.getInetAddress().getHostAddress();

                // Validação de IP e limite de usuários
                if (ipsAtivos.size() >= 5) {
                    recusarConexao(cliente, "Servidor lotado. Máximo de 5 conexões atingido.");
                } else if (ipsAtivos.contains(ipCliente)) {
                    recusarConexao(cliente, "Seu endereço IP já possui uma conexão ativa.");
                } else {
                    System.out.println("Cliente " + ipCliente + " conectado.");
                    ipsAtivos.add(ipCliente); // Adiciona o IP à lista de ativos
                    pool.execute(new ServerThread(cliente, ipCliente, ipsAtivos, baseDeDados));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    private void inicializarBaseDeDados() {
        baseDeDados = new HashMap<>();
        baseDeDados.put("CLIMA", "O clima atual é ensolarado com poucas nuvens.");
        baseDeDados.put("TEMPERATURA", "A temperatura atual é de 25°C.");
        baseDeDados.put("UMIDADE", "A umidade relativa do ar está em 60%.");
        baseDeDados.put("VENTO", "Ventos moderados soprando a 15 km/h na direção Norte.");
    }

    private void recusarConexao(Socket cliente, String motivo) {
        try {
            PrintStream escritor = new PrintStream(cliente.getOutputStream(), true);
            escritor.println("CONEXÃO RECUSADA: " + motivo);
            cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}