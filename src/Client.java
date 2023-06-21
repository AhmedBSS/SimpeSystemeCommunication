import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable{
  
    public Socket clientSocket;
    public DataInputStream input;
    public DataOutputStream output;
    public static Scanner read = new Scanner(System.in);
    public static final int port = 2002;
    public static final String host = "localhost";

  public static void main(String[] args) throws UnknownHostException, IOException {
    Client myC = new Client();    
    
    myC.clientSocket = new Socket(Client.host,Client.port);
    myC.input = new DataInputStream(myC.clientSocket.getInputStream());
    myC.output = new DataOutputStream(myC.clientSocket.getOutputStream());

    new Thread(myC).start();

    while (true){
      System.out.println(myC.input.readUTF());

    }
  }


  public void run() {
    
    try {
      System.out.print("Tappez Votre Nom : ");
      
      this.output.writeUTF(read.nextLine());

      while (true) {
        try {
          this.output.writeUTF(read.nextLine());
        } catch (IOException e) {
          e.printStackTrace();
        }  
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}