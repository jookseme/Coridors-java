package ClientServer;

import io.reactivex.subjects.PublishSubject;
import Serialize.InitMassage;
import Serialize.TurnMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    static ExecutorService executeIt = Executors.newFixedThreadPool(2);
    static String[] players =new String[] {"aqua", "purple"};
    static ArrayList<Server> playersServers  = new ArrayList<>();
    static int countPlayers = 0;
    static int flag = 0;

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(1111);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("ClientServer.Server socket created, command console reader for listen to server commands");
            while (!server.isClosed()) {

                // waiting
                Socket client = server.accept();
                if(countPlayers<2) {
                    Server newPlayer =  new Server(client, players[countPlayers], 1 - countPlayers);
                    playersServers.add(newPlayer);
                    executeIt.execute(newPlayer);
                    countPlayers++;
                    System.out.print("Connection accepted.");
                }
                if(countPlayers == 2 && flag == 0) {
                    flag++;
                    playersServers.get(0).actionPlayerForServer.subscribe(turnMessage -> {
                        playersServers.get(1).actionOpponentPlayerForServer.onNext(turnMessage);
                    });
                    playersServers.get(1).actionPlayerForServer.subscribe(turnMessage -> {
                        playersServers.get(0).actionOpponentPlayerForServer.onNext(turnMessage);
                    });
                }
            }

            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Socket clientDialog;
    private static String player;
    private static int turn;
    private static InitMassage initMassage;
    public PublishSubject<TurnMessage> actionPlayerForServer = PublishSubject.create();
    public PublishSubject<TurnMessage> actionOpponentPlayerForServer = PublishSubject.create();
    public Server(Socket client, String player, int turn) {
        Server.clientDialog = client;
        Server.player = player;
        Server.turn = turn;
        Server.initMassage = new InitMassage(player, turn);
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream  out = new ObjectOutputStream(clientDialog.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientDialog.getInputStream());
            System.out.println("DataInputStream created");

            System.out.println("DataOutputStream  created");
            actionOpponentPlayerForServer.subscribe(turnMessage -> {
                out.writeObject(turnMessage);
                out.flush();
            });
            out.writeObject(Server.initMassage);
            out.flush();
            System.out.println("ClientServer.Server send to client");
            while (!clientDialog.isClosed()) {

                TurnMessage turnMessage = (TurnMessage) in.readObject();
                System.out.println("READ from client - " + turnMessage.idStick+ " , " + turnMessage.resultTurn);
                actionPlayerForServer.onNext(turnMessage);
            }

            System.out.println("ClientServer.Client disconnected");
            System.out.println("Closing connections & channels.");

            in.close();
            out.close();

            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}