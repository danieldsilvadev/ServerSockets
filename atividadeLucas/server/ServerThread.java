import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Semaphore;
import perguntas.RespostaInterface;

public class ServerThread implements Runnable {

    private Socket cliente;
    private Map<String, RespostaInterface> baseDeDados;
    private Semaphore limite;
    private String ipCliente;

    public ServerThread(Socket cliente, String ipCliente, Map<String, RespostaInterface> baseDeDados, Semaphore limite) {
        this.cliente = cliente;
        this.ipCliente = ipCliente;
        this.baseDeDados = baseDeDados;
        this.limite = limite;
    }

    @Override
    public void run() {
        String nome = "";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream())); 
             PrintStream out = new PrintStream(cliente.getOutputStream(), true)) {
            out.println("Digite seu nome:");
            nome = in.readLine();

            System.out.println("Cliente conectado: " + nome + " (" + ipCliente + ")");

            out.println("Bem-vindo " + nome + ". Comandos: CLIMA, TEMPERATURA, UMIDADE, VENTO");

            String msgClient;
            while ((msgClient = in.readLine()) != null) {
                msgClient = msgClient.toUpperCase().trim();
                System.out.println(nome + " enviou - " + msgClient);

                if (msgClient.equals("SAIR")) {
                    System.out.println(nome + " (" + ipCliente + ") desconectado");
                    break;
                }

                RespostaInterface resp = baseDeDados.get(msgClient);
                out.println(resp != null ? resp.responder() : "Comando inválido");
            }
        } catch (Exception e) {
            System.out.println("Erro no cliente " + nome + " IP: " + ipCliente);
        } finally {
            limite.release();
            try {
                cliente.close();
            } catch (Exception e) {
            }
        }
    }
}