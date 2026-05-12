
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteConfig {

    private Socket socketCliente;
    private Scanner leitorServidor;
    private PrintStream escritor;
    private Scanner teclado;

    private volatile boolean conectado = true;

    public void abrirConexao(String ip, int porta) {
        try {
            socketCliente = new Socket(ip, porta);
            leitorServidor = new Scanner(socketCliente.getInputStream());
            escritor = new PrintStream(socketCliente.getOutputStream(), true);
            teclado = new Scanner(System.in);

            System.out.println("Conectado ao servidor.");

            Thread threadRecepcao = new Thread(this::receberMsg);
            Thread threadEnvio = new Thread(this::enviarMsg);

            threadRecepcao.start();
            threadEnvio.start();

        } catch (IOException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            fecharConexao();
        }
    }

    private void receberMsg() {
        try {
            while (conectado && leitorServidor.hasNextLine()) {
                String mensagem = leitorServidor.nextLine();

                System.out.println("\n>> Servidor: " + mensagem);
                System.out.print("> ");

                if (mensagem.startsWith("Servidor cheio")) {
                    System.out.println("Servidor recusou a conexão.");
                    break;
                }
            }

            if (conectado) {
                System.out.println("\n[!] Servidor desconectado.");
            }

        } catch (Exception e) {
            if (conectado) {
                System.out.println("\n[!] Erro na recepção: " + e.getMessage());
            }
        } finally {
            fecharConexao();
        }
    }

    private void enviarMsg() {
        try {
            while (conectado && teclado.hasNextLine()) {
                String mensagem = teclado.nextLine();

                if (!conectado) break;

                escritor.println(mensagem);

                if (mensagem.equalsIgnoreCase("SAIR")) {
                    System.out.println("Encerrando conexão...");
                    break;
                }
            }
        } catch (Exception e) {
            if (conectado) {
                System.out.println("[!] Erro no envio: " + e.getMessage());
            }
        } finally {
            fecharConexao();
        }
    }

    private synchronized void fecharConexao() {
        if (!conectado) return; 

        conectado = false;

        try {
            System.out.println("Encerrando cliente...");

            if (teclado != null) teclado.close();
            if (leitorServidor != null) leitorServidor.close();
            if (escritor != null) escritor.close();
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }

            System.out.println("Cliente encerrado.");

        } catch (IOException e) {
            System.out.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
