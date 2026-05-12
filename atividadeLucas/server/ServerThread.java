import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ServerThread implements Runnable {

    private Socket cliente;
    private String ipCliente;
    private Map<String, RespostaInterface> baseDeDados;
    private Semaphore limiteClientes;

    public ServerThread(Socket cliente, String ipCliente, Map<String, RespostaInterface> baseDeDados, Semaphore limiteClientes) {
        this.cliente = cliente;
        this.ipCliente = ipCliente;
        this.baseDeDados = baseDeDados;
        this.limiteClientes = limiteClientes;
    }

    @Override
    public void run() {

        try (
                Socket cliente = this.cliente;
                BufferedReader leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintStream escritor = new PrintStream(cliente.getOutputStream(), true)) {

            escritor.println("Digite seu nome:");
            String nomeCliente = leitor.readLine();

            System.out.println("Cliente conectado: " + nomeCliente + " (" + ipCliente + ")");

            escritor.println("Bem-vindo, " + nomeCliente
                    + "! Perguntas: CLIMA, TEMPERATURA, UMIDADE, VENTO. Digite SAIR para encerrar.");

            String msgCliente;

            while ((msgCliente = leitor.readLine()) != null) {
                msgCliente = msgCliente.toUpperCase().trim();

                System.out.println(nomeCliente + " enviou: " + msgCliente);

                if (msgCliente.equals("SAIR")) {
                    escritor.println("Desconectando... Até logo!");
                    break;
                }

                RespostaInterface resposta = baseDeDados.get(msgCliente);

                if (resposta != null) {
                    escritor.println(resposta.responder());
                } else {
                    escritor.println("Comando inválido.");
                }
            }

        } catch (Exception e) {
            System.err.println("Erro na comunicação com o cliente " + ipCliente);
        } finally {
            limiteClientes.release();
            System.out.println("Cliente " + ipCliente + " desconectado.");
        }
    }
}