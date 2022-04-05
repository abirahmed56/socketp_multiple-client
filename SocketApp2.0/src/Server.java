import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    public ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){

        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            System.out.println("A new client has connected");

            System.out.println("Chose which type of chat you want?");
            System.out.println("1.Group chat");
            System.out.println("2.Server to special client");
//            System.out.println("3.server to all client");

            Scanner id = new Scanner(System.in);
            System.out.print("Enter your choice :");
            int choice = id.nextInt();
            if (choice == 1) {
                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);

                    Thread thread = new Thread(clientHandler);
                    thread.start();

                }
            }
            else if(choice == 2){
                while (!serverSocket.isClosed()){
                    Socket socket = serverSocket.accept();
                    ServerToSpecificClient serverToSpecificClient = new ServerToSpecificClient(socket, serverSocket);
                    Thread thread = new Thread(serverToSpecificClient);
                    thread.start();
                }
            }
            else{
                System.out.println("You choose wrong key!!");
            }
        }catch (IOException e){
            closeServerSocket();
        }
    }


    public void closeServerSocket(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }

}
