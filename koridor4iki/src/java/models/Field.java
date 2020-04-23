package models;

import Serialize.TurnMessage;
import io.reactivex.subjects.PublishSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class Field extends JPanel {

    public PublishSubject<TurnMessage> actionPlayerMAP = PublishSubject.create();
    public PublishSubject<TurnMessage> actionOpponentMAP = PublishSubject.create();

    private int linesCount = 0;

    public CellsByField stickStore;
    private Cells startStick;
    private ArrayList<Cells> sticks = new ArrayList<>();

    private int width = 0;
    private int height = 0;
    private Color playerColor;
    private Color alliesColor;
    private Color purple = new Color(150, 130, 250);
    private Color aqua = new Color(10, 250, 250);
    public int turn = 0;
    private int opponentScore = 0;
    private int playerScore = 0;

    public void repaintMap() {
        this.revalidate();
         this.repaint();
    }

    public Field(final int linesCount, int width, int height, String player, int turn) {
        this.linesCount = linesCount;
        this.stickStore = new CellsByField();
        this.height = height;
        this.width = width;
        this.turn = turn;
        switch (player) {
            case  ("aqua"):
                this.playerColor = this.aqua;
                this.alliesColor = this.purple;
                break;
            case ("purple"):
                this.playerColor = this.purple;
                this.alliesColor = this.aqua;
                break;

        }
        this.init();

        JLabel label = new JLabel("allies: " + this.opponentScore+ "You: " + this.playerScore );
        setLayout(new FlowLayout());
        add(label);


        actionOpponentMAP.subscribe(turnMessage -> {
            stickStore.getSticks().get(Integer.parseInt(turnMessage.idStick)).color = alliesColor;
            System.out.println("clicked correctly, turn to  " + player);
            if(Integer.parseInt(turnMessage.resultTurn) > 0) {
                this.opponentScore++;
                label.setText("allies: " + this.opponentScore+ "You: " + this.playerScore);
                calculateResult(stickStore.getSticks().get(Integer.parseInt(turnMessage.idStick)), alliesColor);
                System.out.println("allies score: " + this.opponentScore);
                this.turn = 0;
            } else {
                this.turn = 1;
            }
            System.out.println("allies " + this.turn +" and stick " + turnMessage.idStick);
            repaintMap();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(getTurn() == 1) {
                    ArrayList<Cells> sticks = stickStore.getSticks();
                    for (int i = 0; i < sticks.size(); i++) {
                        Cells stick = sticks.get(i);
                        if (stick.contains(e.getPoint()) && stick.color!= playerColor && stick.color!= alliesColor) {
                            int result = calculateResult(stick, playerColor);
                            setTurn(result);
                            if(result>0) {
                                playerScore++;
                                label.setText("allies: " + opponentScore+ "You: " + playerScore);
                                System.out.println("player:  " + playerScore);
                            }
                            TurnMessage newTurnMessage = new TurnMessage(stick.getId(), result);
                            actionPlayerMAP.onNext(newTurnMessage);
                            stick.color = playerColor;
                            repaintMap();
                            break;
                        }
                    }
                } else {
                    System.out.println("not your turn ! "+ getTurn());
                }
            }
        });

    }

    private void setTurn(int i) { this.turn = i; }

    public int getTurn() {
        return this.turn;
    }

    public void init() {
        int cellWidth = this.height / linesCount;
        int cellHeight = this.width / linesCount;

        int stickWidth = 6;
        int margin = 50;
        for (int i = 0; i < linesCount; i++) {
            for(int j = 0; j < linesCount; j++) {
                int index;
                if(j!=linesCount-1) {
                    index = this.stickStore.addStick(cellWidth * j, i * cellHeight + margin, cellHeight, stickWidth);
                    if (i == 0) {
                        this.stickStore.getSticks().get(index).borderU = true;
                    }
                    if (i == linesCount - 1) {
                        this.stickStore.getSticks().get(index).borderD = true;
                    }
                    this.stickStore.getSticks().get(index).horizontal = true;
                }
            }
            for(int j = 0; j < linesCount; j++) {
                int index;
                if(i!=linesCount-1) {
                    index = this.stickStore.addStick(cellWidth * j, i * cellHeight + margin, stickWidth, cellHeight);
                    if (j == 0) {
                        this.stickStore.getSticks().get(index).borderL = true;
                    }
                    if (j == linesCount - 1) {
                        this.stickStore.getSticks().get(index).borderR = true;
                    }
                    if (index > -1) this.stickStore.getSticks().get(index).vertical = true;
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setBackground(Color.white);
        g.setColor(Color.BLACK);
        this.stickStore.draw(g);
    }

    public int calculateResult(Cells startStick, Color pColor) {
        this.sticks = this.stickStore.getSticks();
        this.startStick = startStick;
        boolean r = this.rightSearch(startStick.getId(),pColor);
        boolean l = this.leftSearch(startStick.getId(), pColor);
        boolean u = this.upSearch(startStick.getId(), pColor);
        boolean d = this.downSearch(startStick.getId(), pColor);
        if(r || l || u || d ) { return 1; }
        else { return 0; }
    }

    public boolean rightSearch(int id, Color pColor) {
        if(sticks.get(id).borderR || sticks.get(id).horizontal) {
            return false;
        }
        if(
                        (this.sticks.get(id+1).color == alliesColor || this.sticks.get(id+1).color == playerColor) &&
                        (this.sticks.get(id+linesCount).color == alliesColor || this.sticks.get(id+linesCount).color == playerColor) &&
                        (this.sticks.get(id-linesCount+1).color == alliesColor || this.sticks.get(id-linesCount+1).color == playerColor)
        ) {

            int index = this.stickStore.addStick(this.sticks.get(id).x, this.sticks.get(id).y, this.width / linesCount, this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }
        return false;
    }
    public boolean leftSearch(int id, Color pColor) {
        if(sticks.get(id).borderL || sticks.get(id).horizontal) {
            return false;
        }
        if(
                (this.sticks.get(id-1).color == alliesColor || this.sticks.get(id-1).color == playerColor) &&
                        (this.sticks.get(id-linesCount).color == alliesColor || this.sticks.get(id-linesCount).color == playerColor) &&
                        (this.sticks.get(id+linesCount-1).color == alliesColor || this.sticks.get(id+linesCount-1).color == playerColor)
        ) {
            int index = this.stickStore.addStick(this.sticks.get(id-1).x, this.sticks.get(id-1).y, this.width / linesCount, this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }
        return false;
    }
    public boolean upSearch(int id, Color pColor) {
        if(sticks.get(id).borderU || sticks.get(id).vertical ) {
            return false;
        }
        if(
                (this.sticks.get(id-linesCount*2+1).color == alliesColor || this.sticks.get(id-linesCount*2+1).color == playerColor) &&
                        (this.sticks.get(id-linesCount).color == alliesColor || this.sticks.get(id-linesCount).color == playerColor) &&
                        (this.sticks.get(id-linesCount+1).color == alliesColor || this.sticks.get(id-linesCount+1).color == playerColor)
        ) {
            int index = this.stickStore.addStick(this.sticks.get(id-linesCount).x, this.sticks.get(id-linesCount).y, this.width / linesCount, this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }
        return false;
    }
    public boolean downSearch(int id, Color pColor) {
        if(sticks.get(id).borderD || sticks.get(id).vertical) {
            return false;
        }
        if(
                (this.sticks.get(id+linesCount*2-1).color == alliesColor || this.sticks.get(id+linesCount*2-1).color == playerColor) &&
                        (this.sticks.get(id+linesCount).color == alliesColor || this.sticks.get(id+linesCount).color == playerColor) &&
                        (this.sticks.get(id+linesCount-1).color == alliesColor || this.sticks.get(id+linesCount-1).color == playerColor)
        ) {
            int index = this.stickStore.addStick(this.sticks.get(id+linesCount-1).x, this.sticks.get(id+linesCount-1).y, this.width / linesCount, this.width / linesCount);
            this.stickStore.getSticks().get(index).color = pColor;
            this.stickStore.getSticks().get(index).cell = true;
            return true;
        }
        return false;
    }

}
