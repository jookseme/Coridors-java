package ClientServer;

import Serialize.InitMassage;
import Serialize.TurnMessage;
import models.UIGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client2 {
    public static void main(String[] args) throws InterruptedException {
        try(Socket socket = new Socket("localhost", 1111);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());)
        {

            System.out.println("ClientServer.Client connected to socket ");
            System.out.println("ClientServer.Client create gameField ");
            UIGame uiGame = null;

            InitMassage initMassage = (InitMassage) ois.readObject();
            if(uiGame==null) {
                uiGame = new UIGame(initMassage.player, Integer.parseInt(initMassage.turn));
                uiGame.actionPlayerUI.subscribe(turnMessage -> {
                    oos.writeObject(turnMessage);
                    oos.flush();
                    System.out.println("ClientServer.Client sent message " + turnMessage.idStick + " " + turnMessage.resultTurn);//сериализация
                });
            }
            while(!socket.isOutputShutdown()){
                TurnMessage opponentTurn = (TurnMessage) ois.readObject();
                uiGame.actionOpponentUI.onNext(opponentTurn);
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}