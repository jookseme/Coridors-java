package models;

import io.reactivex.subjects.PublishSubject;
import Serialize.TurnMessage;

import javax.swing.*;
import java.awt.*;

public class UIGame {

    private Field jPanel1;

    public PublishSubject<TurnMessage> actionPlayerUI = PublishSubject.create();
    public PublishSubject<TurnMessage> actionOpponentUI = PublishSubject.create();



    public UIGame(String player, int turn) {
         final int height = 300;
         final int width = 300;
         final int cells = 5;

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("KORIDORCHIKI. Player name: " + player);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jPanel1 = new Field(cells, width, height, player, turn);
        frame.getContentPane().add(jPanel1);


        frame.setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setVisible(true);

        jPanel1.actionPlayerMAP.subscribe(turnMessage -> {
            actionPlayerUI.onNext(turnMessage);
        });
        actionOpponentUI.subscribe(turnMessage -> {
            jPanel1.actionOpponentMAP.onNext(turnMessage);
        });
    }
}

