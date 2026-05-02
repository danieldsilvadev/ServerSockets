import java.io.PrintStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import perguntas.*;

public class ServerConfig {

    private Semaphore limiteClientes = new Semaphore(5);
    private Map<String, RespostaInterface> baseDeDados;

    public void start(int porta) {
        inicializarbd();
        try (ServerSocket server = new ServerSocket(porta)) {
            System.out.println("Servidor rodando na porta " + porta);

            while (true) {
                Socket cliente = server.accept();
                String ipCliente = cliente.getInetAddress().getHostAddress();

                if (!limiteClientes.tryAcquire()) {
                    PrintStream out = new PrintStream(cliente.getOutputStream(), true);
                    out.println("Servidor cheio");
                    cliente.close(); 
                    continue;
                }

               new Thread(new ServerThread(cliente, ipCliente, baseDeDados, limiteClientes)).start();
            }
        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    private void inicializarbd() {
        baseDeDados = new HashMap<>();
        baseDeDados.put("CLIMA", new ClimaResposta());
        baseDeDados.put("TEMPERATURA", new TemperaturaResposta());
        baseDeDados.put("UMIDADE", new UmidadeResposta());
        baseDeDados.put("VENTO", new VentoResposta());
    }
}