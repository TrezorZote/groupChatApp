import java.io.*;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
    public class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private String clientName;

        public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();

        //Konstruktor für unsere ClientHandler Objekt wobei unsere clientName aus unsere sockets bufferedReader gelesen und die Liste aktualisiert wird
        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientName = bufferedReader.readLine();
                clientHandlers.add(this);
                broadcast(this.clientName + " entered the group");

            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                e.printStackTrace();

            }
        }

        //override die Run Methode aus Runnable Interface damit keine blockierende Methode wie sendMessage aus Client Klasse uns warten macht
        @Override
        public void run() {
            String messageFromClient;
           while(this.socket.isConnected()){
               try {
                   messageFromClient=bufferedReader.readLine();
                   broadcastMessage(messageFromClient);
               }catch (IOException e){
                   closeEverything(socket, bufferedWriter, bufferedReader);
                   break;
               }
               if(!socket.isConnected()){
                   broadcast(clientName + " left the chat");
               }else if(messageFromClient=="exit"){
                   closeEverything(this.socket,this.bufferedWriter,this.bufferedReader);
               }
           }
        }
        public void broadcastMessage(String message){
            for (ClientHandler i : clientHandlers) {
                try {
                    if (i.clientName != this.clientName) {
                        i.bufferedWriter.write(this.clientName+" :" +"\n" + message + "\n"+ java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                        i.bufferedWriter.newLine();
                        i.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }
            }
        }
        public void broadcast(String message){
            for (ClientHandler i : clientHandlers) {
                try {
                    if (i.clientName != this.clientName) {
                        i.bufferedWriter.write( message );
                        i.bufferedWriter.newLine();
                        i.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }
            }
        }
        public void removeClientHandler(){
            clientHandlers.remove(this);
            broadcast(this.clientName + " left the chat");
        }
        public void closeEverything (Socket socketToClose, BufferedWriter bufferedWriterToClose, BufferedReader bufferedReaderToClose){
            removeClientHandler();
            try {
                if (socketToClose != null) {
                    socketToClose.close();
                }
                if (bufferedReaderToClose != null) {
                    bufferedReaderToClose.close();
                }
                if (bufferedWriterToClose != null) {
                    bufferedWriterToClose.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

