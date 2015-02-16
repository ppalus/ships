package pl.agh.fis.ships;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Field implements Serializable {
    private int nr;
    private int state;
    private Button button = null;

    public static final int EMPTY = 0;
    public static final int SHIP = 1;
    public static final int MISSED = 2;
    public static final int SHOT = 3;
    public static final int SINK = 4;
    public static final int MY_SHIP = 5;

    public Field(View v, int nr, int state) {
        this.nr = nr;
        setButton(v);
        setState(state);
    }

    public Field(View v, int nr, boolean fake) {
        this.button = (Button)v;
        this.nr = nr;
        setState(Field.MY_SHIP);
    }

    public Field(int nr, int state) {
        this.nr = nr;
        setState(state);
    }

    public void setButton(View v) {
        this.button = (Button)v;
        this.button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(getState()==Field.EMPTY) {
                    setState(Field.MISSED);
                    button.setClickable(false);
                }
                if(getState()==Field.SHIP) {
                    setState(Field.SHOT);
                    button.setClickable(false);
                }
            }
        });
    }

    public Button getButton() {
        return this.button;
    }

    public int getState() {
        return state;
    }

    public int getNr() {
        return this.nr;
    }

    public void setState(int s) {
        int t = R.drawable.regular;
        this.state = s;
        switch(s) {
            case Field.EMPTY:
                t = R.drawable.button_field;
                break;
            case Field.MISSED:
                t = R.drawable.pudlo;
                break;
            case Field.SHOT:
                t = R.drawable.trafiony;
                break;
            case Field.SINK:
                t = R.drawable.zatopiony;
                break;
            case Field.MY_SHIP:
                t = R.drawable.ship;
                break;
        }
        if(this.button != null)
            this.button.setBackgroundResource(t);
    }
}
