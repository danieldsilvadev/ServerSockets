import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteConfig {

    public void conectar(String ip, int porta) {

        try (
            Socket socket = new Socket(ip, porta);
            Scanner teclado = new Scanner(System.in);
            Scanner servidor = new Scanner(socket.getInputStream());
            PrintStream out = new PrintStream(socket.getOutputStream(), true)
        ) {

            // thread de recepção
            new Thread(() -> {
                while (servidor.hasNextLine()) {
                    System.out.println("Servidor - " + servidor.nextLine());
                }
            }).start();

            // envio principal
            while (true) {

                String msg = teclado.nextLine();

                out.println(msg);

                if (msg.equalsIgnoreCase("SAIR")) {
                    System.err.println("Desconectado");
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Erro cliente");
        }
    }
}