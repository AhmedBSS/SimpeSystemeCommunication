import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static ServerSocket server;
    public static int nbr = 0;


  public static void main(String[] args) throws IOException {
    server = new ServerSocket(Client.port);
    System.out.println("Le Serveur Attend La Connexion....");
    while (true) {  
      Socket clientSocket = server.accept();
      nbr++;
      System.out.println("Un client {"+ nbr +"} a ete connectee");
      new Thread(new ClientHandler(clientSocket)).start();
    }

  }  
}
