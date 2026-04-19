package atividadeLucas.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ServerThread implements Runnable {
    private Socket cliente;
    private String ipCliente;
    private Set<String> ipsAtivos;
    private Map<String, String> baseDeDados;

    public ServerThread(Socket cliente, String ipCliente, Set<String> ipsAtivos, Map<String, String> baseDeDados) {
        this.cliente = cliente;
        this.ipCliente = ipCliente;
        this.ipsAtivos = ipsAtivos;
        this.baseDeDados = baseDeDados;
    }

    @Override
    public void run() {
        try (
            Scanner leitor = new Scanner(cliente.getInputStream());
            PrintStream escritor = new PrintStream(cliente.getOutputStream(), true)
        ) {
            escritor.println("Bem-vindo ao Servidor Climático! Perguntas disponíveis: CLIMA, TEMPERATURA, UMIDADE, VENTO.");

            while (leitor.hasNextLine()) {
                String mensagem = leitor.nextLine().toUpperCase().trim();

                if (mensagem.equals("SAIR")) {
                    escritor.println("Desconectando... Até logo!");
                    break;
                }

                String resposta = baseDeDados.get(mensagem);

                if (resposta != null) {
                    escritor.println(resposta);
                } else {
                    escritor.println("Pergunta não reconhecida. Tente: CLIMA, TEMPERATURA, UMIDADE, VENTO ou SAIR.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro na comunicação com o cliente " + ipCliente);
        } finally {
            try {
                ipsAtivos.remove(ipCliente);
                cliente.close();
                System.out.println("Cliente " + ipCliente + " desconectado.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}