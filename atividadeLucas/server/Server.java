package atividadeLucas.server;

public class Server {
    public static void main(String[] args) {
        ServerConfig servidor = new ServerConfig();
        servidor.startServer(8089);
    }
}