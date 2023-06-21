import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

  public Socket clientSocket;
  public DataInputStream input;
  public DataOutputStream output;
  private static List<String> ClientsNames = new ArrayList<>();
  private static List<Socket> ClientsSockets = new ArrayList<>();
  public String name;

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {

    try {
      input = new DataInputStream(clientSocket.getInputStream());
      output = new DataOutputStream(clientSocket.getOutputStream());

      name = input.readUTF();

      ClientsNames.add(name);
      ClientsSockets.add(clientSocket);

      String BonjourMessage = "Bonjour : " + this.name;
      BonjourMessage += "\nPar default Les messages envoyent pour tous";
      BonjourMessage += "\nPour Envoye Les messages pour un seul Client : @ClientName \"Message\"";
      BonjourMessage += "\nPour Envoye Les messages pour tous sauf un client : !ClientName \"Message\"";
      BonjourMessage += "\nPour Quittez Tapper /exit";

      output.writeUTF(BonjourMessage);

      while (true) {
        if (traitement(input.readUTF(), name) == 0)
          break;
      }

      ClientsNames.remove(name);
      ClientsSockets.remove(clientSocket);

      output.writeUTF("Au revoire " + name);
      System.out.println("Le Client : " + name + " a ete deconnecte ");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public int traitement(String reponse, String name) throws IOException {
    if (reponse.startsWith("/exit") == true)
      return 0;
    if (reponse.startsWith("@") == true) {
      sendToOneClient(reponse, name);
      return 1;
    }
    if (reponse.startsWith("!") == true) {
      sendToAllClientExceptedOne(reponse, name);
      return 2;
    } else {
      sendToAllClient(reponse, name);
      return 3;
    }
  }

  public void sendToAllClient(String reponse, String name) throws IOException {

    for (Socket socket : ClientsSockets) {

      if (socket == clientSocket)
        continue;
      new DataOutputStream(socket.getOutputStream())
          .writeUTF("< " + name + " > : to all : " + reponse);
    }

  }

  public void sendToOneClient(String reponse, String name) throws IOException {

    String[] nameAndMessage = splitByOp(reponse, "@");

    int i = ClientsNames.indexOf(nameAndMessage[0]);

    if (i != -1)
      new DataOutputStream(ClientsSockets.get(i).getOutputStream())
          .writeUTF("< " + name + " > : just for you: " + nameAndMessage[1]);
    else
      new DataOutputStream(clientSocket.getOutputStream())
          .writeUTF(nameAndMessage[0] + "Non trouvable ");

  }

  public void sendToAllClientExceptedOne(String reponse, String name) throws IOException {

    String[] nameAndMessage = splitByOp(reponse, "!");

    int i = ClientsNames.indexOf(nameAndMessage[0]);
    if (i != -1) {

      for (Socket socket : ClientsSockets) {

        if (socket == ClientsSockets.get(i) || socket == clientSocket)
          continue;
        new DataOutputStream(socket.getOutputStream())
        .writeUTF("< " + name + " > : for all except "+ nameAndMessage[0] +" : " + nameAndMessage[1]);

      }
    }
    else{
      new DataOutputStream(clientSocket.getOutputStream())
      .writeUTF(nameAndMessage[0] + "Non trouvable ");
    }

  }

  public String[] splitByOp(String reponse, String op) {

    String[] splitOp = reponse.split(op,2);
		String[] spliteNameLine = splitOp[1].split(" ",2);
		return spliteNameLine;
    }

}
