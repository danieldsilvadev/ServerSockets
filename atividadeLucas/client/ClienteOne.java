package atividadeLucas.client;

public class ClienteOne {
    public static void main(String[] args) {
        ClienteConfig clienteOne = new ClienteConfig();
        clienteOne.abrirConexao("localhost",  8089);
    }
}
