package pl.agh.fis.ships;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.Serializable;

public class Editor extends Grid implements Serializable {
    private Button[] sets;
    private int[] shipcounters;
    private int currentShipToAdd = 0;
    private boolean justAdding;
    private boolean direction = true; //true - poziom
    private int prevx, prevy;

    public Editor(View[] rids, boolean isAbstract, View[] sets) {
        super(rids, isAbstract);
        shipcounters = new int[4]; //0 - 1m, 1 - 2m, 2 - 3m, 3 - 4m
        currentShipToAdd = 0;
        this.sets = new Button[4];
        for(int i = 0 ; i < 4; i++) {
            final int temp = i + 1;
            this.sets[i] = (Button)sets[i];
            this.sets[i].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    currentShipToAdd = temp;
                    justAdding = true;
                }
            });
        }
    }


    public Editor(View[] rids, boolean isAbstract) {
        super(rids, isAbstract);
        shipcounters = new int[4];
    }

    public void init() {
        for(int i = 0; i < 100; i++) {
            Field temp = new Field(rids[i], i, Field.EMPTY);
            final int q = i;
            temp.getButton().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    int x = q/10;
                    int y = q%10;
                    if(currentShipToAdd>0) {
                        if((shipCounter <10)&&(justAdding)) {
                            prevx = 100;
                            prevy = 100;
                            if(shipcounters[currentShipToAdd-1] < (5 - currentShipToAdd)) {
                                if(!insertShip(x, y, currentShipToAdd, direction)) {
                                    shipCounter++;
                                    shipcounters[currentShipToAdd-1]++;
                                } else {
                                    prevx = x;
                                    prevy = y;
                                }
                                updateButtons();
                            }
                        } if(!justAdding) {
                            boolean tempclear = clearShip(prevx, prevy, currentShipToAdd, direction);
                            if((prevx == x)&&(prevy==y))
                                direction = !direction;
                            boolean tempins = insertShip(x, y, currentShipToAdd, direction);

                            if(!tempins) {
                                shipCounter++;
                                shipcounters[currentShipToAdd-1]++;
                            } else {
                                prevx = x;
                                prevy = y;
                            }
                        }
                        justAdding = false;
                    }
                }
            });

        }
    }

    public void updateButtons() {
        for(int i = 0; i < 4; i++)
            if(shipcounters[i] >= 4 - i) {
                sets[i].setVisibility(View.INVISIBLE);
            }
    }


    public boolean clearShip(int x, int y, int l, boolean direction) {
        shipCounter--;
        shipcounters[l-1]--;
        if(direction)
            if(y + l > 10)
                return false;
        if(!direction)
            if(x + l > 10)
                return false;
        for(int i = 0; i < l; i++) {
            if(direction) {
                if(this.matrix[x][y + i] != 0) {
                    this.matrix[x][y + i] = 0;
                    Button temp = (Button)(rids[x*10 + y + i]);
                    temp.setBackgroundResource(R.drawable.button_field);
                }
            } else {
                if(this.matrix[x + i][y] != 0) {
                    this.matrix[x + i][y] = 0;
                    Button temp = (Button)(rids[x*10 + i*10 + y]);
                    temp.setBackgroundResource(R.drawable.button_field);
                }
            }
        }
        return true;
    }

    @Override
    public void randomize() {
        clearGrid();
        for(int i = 4; i > 0; i--) {
            for(int j = i; j < 5; j++) {
                insertRandomShip(i);
                shipcounters[i-1] = 9;
            }
        }
        this.ready = true;
        updateButtons();
    }

    @Override
    public void clearGrid() {
        ready = false;
        for(int i = 0; i < 100; i++) {
            Button temp = (Button)rids[i];
            temp.setBackgroundResource(R.drawable.button_field);
            temp = null;
        }
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++)
                matrix[i][j] = Grid.FREE;
        }
        for(int i = 0; i < 4; i++) {
            shipcounters[i] = 0;
            sets[i].setVisibility(View.VISIBLE);
        }
        shipCounter = 0;
        currentShipToAdd = 0;
    }

    public boolean insertShip(int x, int y, int l, boolean direction) {
        if(l == 0) return false;
        if(direction)
            if(y + l > 10) return false;
        if(!direction)
            if(x + l > 10) return false;
        boolean inserted = true;
        for(int i = 0; i < l; i++) {
            if(direction) {
                if(isEmpty(x, y + i) == false) {
                    inserted = false;
                    break;
                }
            } else {
                if(isEmpty(x + i, y) == false) {
                    inserted = false;
                    break;
                }
            }
        }
        if(inserted) {
            View[] temp = new View[l];
            int[] temp2 = new int[l];
            for(int i = 0; i < l; i++) {
                if(direction) {
                    temp[i] = rids[x*10 + y + i];
                    temp2[i] = x*10 + y + i;
                    this.matrix[x][y + i] = 1;
                } else {
                    temp[i] = rids[x*10 + i*10 + y];
                    temp2[i] = x*10 + i*10 + y;
                    this.matrix[x + i][y] = 1;
                }
            }
            if(isAbstract)
                ships[shipCounter] = new Ship(l, temp, temp2, true);
            else
                ships[shipCounter] = new Ship(l, temp, temp2);
            shipCounter++;
            shipcounters[l-1]++;
            if(shipCounter == 10)
                ready = true;
        }
        return inserted;
    }

}
