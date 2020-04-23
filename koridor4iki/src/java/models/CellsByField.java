package models;

import java.awt.*;
import java.util.ArrayList;

public class CellsByField {
    private int count = 0;
    private ArrayList<Cells> sticks ;

    public CellsByField() {
        this.sticks = new ArrayList<Cells>();
        this.count = 0;
    }

    public int addStick(int x, int y, int width, int height) {
        Cells newStick = new Cells(x, y, width, height, count);
        this.sticks.add(newStick);
        this.count++;
        return this.count-1;
    }

    public void addStick(Cells stick) {
        this.sticks.add(stick);
    }

    public void draw(Graphics g) {
        for (Cells stick : sticks) {
            g.setColor(stick.color);
            g.fillRect(stick.x, stick.y, stick.width,
                    stick.height);
        }
    }

    public ArrayList<Cells> getSticks() {
        return this.sticks;
    }

}
