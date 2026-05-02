public class Cliente {
    public static void main(String[] args) {
        ClienteConfig cliente = new ClienteConfig();
        cliente.conectar("localhost", 8089);
    }
}