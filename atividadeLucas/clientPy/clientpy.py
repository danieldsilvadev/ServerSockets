import socket
import threading

HOST = "localhost"
PORT = 8089

def receber(sock):
    while True:
        try:
            msg = sock.recv(1024).decode()
            if not msg:
                print("\nServidor desconectou")
                break
            print("\nServidor:", msg.strip())
            print("> ", end="", flush=True)
        except:
            break

def enviar(sock):
    while True:
        try:
            msg = input("> ")
            sock.sendall((msg + "\n").encode())

            if msg.upper() == "SAIR":
                break
        except:
            break

def main():
    cliente = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    cliente.connect((HOST, PORT))

    print("Conectado")

    t_receber = threading.Thread(target=receber, args=(cliente,))
    t_receber.daemon = True
    t_receber.start()

   
    t_enviar = threading.Thread(target=enviar, args=(cliente,))
    t_enviar.start()

    t_enviar.join()

    cliente.close()
    print("Fim")

if __name__ == "__main__":
    main()
