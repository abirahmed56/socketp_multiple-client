import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerToSpecificClient implements Runnable {


    public Socket serverClient;
    public ServerSocket serverSocket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public static ArrayList<ServerToSpecificClient> serverToSpecificClients = new ArrayList<>();
    public String clientUserName;

    ServerToSpecificClient(Socket serverClient, ServerSocket serverSocket) {
        try {
            this.serverClient = serverClient;
            this.serverSocket = serverSocket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(serverClient.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(serverClient.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            serverToSpecificClients.add(this);
        } catch (IOException e) {
            closeEverything(serverClient, bufferedReader, bufferedWriter);
        }

    }
    @Override
    public void run() {
        String messageFromClient;

        while (serverClient.isConnected()){
            try{
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        Scanner scanner = new Scanner(System.in);
                        String msg = scanner.nextLine();
                        specificClient(msg);
                    }
                }).start();

                messageFromClient = bufferedReader.readLine();
                System.out.println(messageFromClient);
                //broadcastMessage(messageFromClient);

          /*  } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
            try {*/

            } catch (IOException e) {
                closeEverything(serverClient, bufferedReader, bufferedWriter);
                break;
            }
        }


    }
    private void specificClient(String msg) {
        String words[] = msg.split("\\$");
        for(ServerToSpecificClient serverToSpecificClient : serverToSpecificClients){
            try {
                if(serverToSpecificClient.clientUserName.toLowerCase().equals(words[0])){
                    serverToSpecificClient.bufferedWriter.write("Server :"+words[1]);
                    serverToSpecificClient.bufferedWriter.newLine();
                    serverToSpecificClient.bufferedWriter.flush();
                }
            }catch (IOException e) {
                closeEverything(serverClient, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeServerToSpecificClient(){
        serverToSpecificClients.remove(this);
       // broadcastMessage("SERVER: "+ clientUserName + "has left the chat");
    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader ,BufferedWriter bufferedWriter ){
        removeServerToSpecificClient();
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null)
            {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
}