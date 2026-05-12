
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ServerConfig {

    private Semaphore limiteClientes = new Semaphore(5);
    private Map<String, RespostaInterface> baseDeDados;

    private void inicializarBaseDeDados() {
        baseDeDados = new HashMap<String, RespostaInterface>();
        baseDeDados.put("CLIMA", new ClimaResposta());
        baseDeDados.put("TEMPERATURA", new TemperaturaResposta());
        baseDeDados.put("UMIDADE", new UmidadeResposta());
        baseDeDados.put("VENTO", new VentoResposta());
    }

    public void start(int porta) {
        inicializarBaseDeDados();

        try (
                ServerSocket server = new ServerSocket(porta)

        ) {

            System.out.println("Servidor rodando na porta " + porta);

            while (true) {
                Socket cliente = server.accept();
                String ipCliente = cliente.getInetAddress().getHostAddress();
                System.out.println("Nova conexão detectada do IP: " + ipCliente);

                if (!limiteClientes.tryAcquire()) {
                    PrintStream escritor = new PrintStream(cliente.getOutputStream(), true);
                    escritor.println("Servidor cheio");
                    cliente.close();
                    continue;
                }

                new Thread(
                        new ServerThread(cliente, ipCliente, baseDeDados, limiteClientes)).start();
            }

        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}
