import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    //server Klasse mit einem Attribute das ein Serversocket Objekt ist
        private final ServerSocket serversocket;

        //mein Konstruktor wo ich das serversocket konfiguriere
        public Server(ServerSocket serversocket) {
            this.serversocket = serversocket;
        }

        //startServerMethode um mein Server bereit für Verbindungen zu machen
        public void startServer(){
            try {
                while (!serversocket.isClosed()){
                    Socket socket = serversocket.accept();
                    System.out.println("a new client has been connected ");
                    ClientHandler clienthandler= new ClientHandler(socket);
                    Thread thread= new Thread(clienthandler);
                    thread.start();
                }
            }catch (  IOException e){
                closeServerSocket();
            }

        }

        //  closeServerSocket Methode um den serversocket zu schliessen
        public void closeServerSocket(){
            try {
                if(serversocket != null){
                    serversocket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        //main Methode wo ich den port yum Verbindung verifiziere und auf mein neuer Server Objekt die start Methode Aufruft
        public static void main(String[] args) throws IOException {

                ServerSocket serverSocket = new ServerSocket(2023);
                Server server = new Server(serverSocket);
                server.startServer();
        }
    }


