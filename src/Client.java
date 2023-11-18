import java.io.*;
import java.net.Socket;
import java.util.Scanner;


    //Client Klasse mit vier Attributen
    public class Client {
        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private String clientUsername;

        //Client Klasse Konstruktor wo ein socket und ein Name zu dem socket und Name dieses Objekt konfiguriert wird
        public Client(Socket socket, String clientUsername)  {
            try {
                this.socket = socket;
                this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.clientUsername= clientUsername;
            }
            catch (IOException e){
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }

        //sendMessage Methode zum Senden von Anfragen an den Server mithilfe des Scanners Objekt
        public void sendMessage() {
            try {
                bufferedWriter.write(clientUsername);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                Scanner message = new Scanner(System.in);
                System.out.println("welcome " + clientUsername + " you can start chatting with friends" );
                while (socket.isConnected()){
                    String  messageToSend =  message.nextLine();
                    if(!messageToSend.isEmpty()){
                        bufferedWriter.write(messageToSend);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
            }catch (IOException e ){
                closeEverything( socket, bufferedWriter, bufferedReader);
            }
        }


        //receiveMessage Methode die dazu dient Antworten des Servers zu bekommen und es auszugeben
        public void receiveMessage() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String messageClient;
                    try {
                        while (socket.isConnected()){
                            messageClient = bufferedReader.readLine();
                            System.out.println(messageClient);
                        }
                    }catch (IOException e ){
                        closeEverything( socket, bufferedWriter, bufferedReader);
                    }
                }
            }).start();

        }
        //closeEverything Methode um alles zu schliessen
        public void closeEverything( Socket socketToClose , BufferedWriter bufferedWriterToClose, BufferedReader bufferedReaderToClose){

            try {
                if (socketToClose!= null){
                    socketToClose.close();
                }
                if (bufferedReaderToClose!= null){
                    bufferedReaderToClose.close();
                }
                if (bufferedWriterToClose!= null){
                    bufferedWriterToClose.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }



        }

        public static void main (String[]args) throws IOException {

                    Scanner scanner = new Scanner(System.in);
                    System.out.println(" please enter your name to enter the group chat");
                    String name = scanner.nextLine();
                    try {
                        Socket socket = new Socket("localhost", 2023);
                        Client client = new Client(socket, name);
                        client.receiveMessage();
                        client.sendMessage();
                    }
                    catch (IOException e){
                        System.out.println(" an error occured try restarting the program");
                    }
                }

    }

