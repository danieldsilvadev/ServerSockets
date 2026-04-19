package atividadeLucas.client;

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

            // Inicia a thread que apenas escuta o servidor
            Thread threadRecepcao = new Thread(this::receberMsg);
            threadRecepcao.start();
            
            enviarMsg();

        } catch (IOException e) {
            System.err.println("Não foi possível conectar ao servidor: " + e.getMessage());
        } finally {
            fecharConexao();
        }
    }

    private void receberMsg() {
        try {
            while (conectado && leitorServidor.hasNextLine()) {
                String mensagem = leitorServidor.nextLine();
                System.out.println("\n>> Servidor: " + mensagem);
                System.out.print("> "); // Mantém a setinha para você continuar digitando

                if (mensagem.startsWith("CONEXÃO RECUSADA")) {
                    conectado = false;
                    break;
                }
            }

            // A MÁGICA ACONTECE AQUI:
            // Se o loop do Scanner acabou e nós não pedimos para sair (conectado ainda é
            // true),
            // significa que o servidor derrubou a conexão do nada.
            if (conectado) {
                System.out.println("\n[!] O Servidor foi encerrado abruptamente.");
            }

        } catch (Exception e) {
            // Só vai cair aqui se for um erro muito bizarro de memória ou thread
            if (conectado) {
                System.out.println("\n[!] Erro crítico na conexão: " + e.getMessage());
            }
        } finally {
            if (conectado) {
                conectado = false;
                System.out.println("Encerrando o cliente...");
                System.exit(0);
            }
        }
    }

    private void enviarMsg() {
        while (conectado && teclado.hasNextLine()) {
            String mensagem = teclado.nextLine();

            if (!conectado)
                break; // Para o loop se o servidor recusou a conexão

            escritor.println(mensagem);

            if (mensagem.equalsIgnoreCase("SAIR")) {
                conectado = false; // Sinaliza para a thread de recepção parar
                break;
            }
        }
    }

    private void fecharConexao() {
        try {
            System.out.println("Encerrando cliente...");
            conectado = false;

            if (teclado != null)
                teclado.close();
            if (leitorServidor != null)
                leitorServidor.close();
            if (escritor != null)
                escritor.close();
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }
            System.out.println("Cliente encerrado com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}