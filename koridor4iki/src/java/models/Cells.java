package models;

import java.awt.*;

public class Cells extends Rectangle {
    public boolean borderU = false;
    public boolean borderD = false;
    public boolean borderL = false;
    public boolean borderR = false;
    public boolean vertical = false;
    public boolean horizontal = false;
    public boolean cell = false;
    int id;
    public Color color = new Color(10, 99, 99);
    public Cells(int x, int y, int w, int h, int id) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.id = id;
    }
    public Cells(int x, int y, int w, int h, int id, Color color) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.id = id;
        this.color = color;
    }
    public int getId() {
        return this.id;
    }
}