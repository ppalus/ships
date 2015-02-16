package pl.agh.fis.ships;

import android.view.View;
import android.view.View.OnClickListener;

public class Ship {
    private Field[] fields;
    private int length;
    private boolean isSink;

    public Ship(int l, View[] v, int[] nr) {
        this.length= l;
        this.fields = new Field[this.length];
        for(int i = 0; i < this.length; i++) {
            fields[i] = new Field(v[i], nr[i], Field.SHIP);
        }
        this.isSink = false;
        for(int i = 0; i < length; i++) {
            final Field temp = fields[i];
            temp.getButton().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    temp.getButton().setClickable(false);
                    if(temp.getState() == Field.SHIP) {
                        temp.setState(Field.SHOT);
                        updateState();
                        if(isSink)
                            for(int j = 0; j < length; j++)
                                fields[j].setState(Field.SINK);
                    }
                }
            });
        }
    }

    public Ship(int l, View[] v, int[] nr, boolean fake) {
        this.length = l;
        this.fields = new Field[this.length];
        for(int i = 0; i < this.length; i++) {
            fields[i] = new Field(v[i], nr[i], true);
        }
        this.isSink = false;
    }

    public Ship(int l, int[] nr) {
        this.length = l;
        this.isSink = false;
        this.fields = new Field[this.length];
        for(int i = 0; i < this.length; i++)
            fields[i] = new Field(nr[i], Field.SHIP);
    }

    public Ship(int l, int xs, int xe) {
        this.length = l;
        this.isSink = false;
        this.fields = new Field[this.length];
        for(int i = 0; i < this.length; i++) {
            if(xe - xs == l - 1)
                fields[i] = new Field(xs + i, Field.SHIP);
            if(xe - xs == (l - 1)*10)
                fields[i] = new Field(xs + i*10, Field.SHIP);
        }
    }

    public Ship(int l, int xs, int xe, View[] v) {
        this.length = l;
        this.isSink = false;
        this.fields = new Field[this.length];
        for(int i = 0; i < this.length; i++) {
            if(xe - xs == l - 1)
                fields[i] = new Field(v[xs + i], xs + i, Field.SHIP);
            if(xe - xs == (l - 1)*10)
                fields[i] = new Field(v[xs + i*10], xs + i*10, Field.SHIP);
        }
    }

    public Ship(int l, int xs, int xe, View[] v, boolean fake) {
        this.length = l;
        this.isSink = false;
        this.fields = new Field[l];
        for(int i = 0; i < l; i++) {
            if(xe - xs == l - 1) {
                fields[i] = new Field(v[xs + i], xs + i, true);
            }
            if(xe - xs == (l - 1)*10) {
                fields[i] = new Field(v[xs + i*10], xs + i*10, true);
            }
        }
    }

    public int[] getParsedShip() {
        return new int[]{this.length, this.fields[0].getNr(), this.fields[this.length-1].getNr()};
    }

    public int getLength() {
        return this.length;
    }

    public void updateState() {
        for(int i = 0; i < this.length; i++) {
            if(fields[i].getState() != Field.SHOT) {
                this.isSink = false;
                return;
            }
        }
        this.isSink = true;
    }

    public boolean getSinkState() {
        return isSink;
    }

    public void setFakeShipState(int bg) {
        for(int i = 0; i < this.length; i++) {
            this.fields[i].getButton().setBackgroundResource(bg);
        }
    }

    public void setShipState() {
        for(int i = 0; i < this.length; i++)
            this.fields[i].setState(Field.SHIP);
    }

    public Field getField(int nr) {
        return this.fields[nr];
    }
}
