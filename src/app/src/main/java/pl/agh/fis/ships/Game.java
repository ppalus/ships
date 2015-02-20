package pl.agh.fis.ships;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.Random;

public class Game implements ResultHandler {
    private Player player;
    private Player opponent;
    private AIPlayer pc;
    private int mode;
    View gameShield;
    View[] rids;

    public Game(Player player, AIPlayer pc, int mode) {
        this.player = player;
        this.pc = pc;
        this.mode = mode;
    }

    public Game(Player player, Player opponent, View gameShield, View[] rids, boolean isStarting) {
        this.player = player;
        this.opponent = opponent;
        this.gameShield = gameShield;
        this.rids = rids;
        setLock(!isStarting);
    }

    private void initSinglePlayer(final Activity a) {
        pc.randomize();
        Ship[] ships = pc.getShips();
        for (final Ship ship : ships) {
            for (int j = 0; j < ship.getLength(); j++) {
                final Field pole = ship.getField(j);
                pole.getButton().setOnClickListener(new OnClickListener() {
                    // player hit ship
                    public void onClick(View v) {
                        end:
                        {
                            pole.getButton().setClickable(false);
                            if (pole.getState() == Field.SHIP) {
                                pole.setState(Field.SHOT);
                                ship.updateState();
                                if (ship.getSinkState()) {
                                    for (int j = 0; j < ship.getLength(); j++)
                                        ship.getField(j).setState(Field.SINK);
                                    pc.shipCounter--;
                                    if (pc.shipCounter == 0) {
                                        a.setContentView(R.layout.win);
                                        break end;
                                    }
                                }
                            }
                            pc.makeMove(player, mode, a);
                        }
                    }
                });
            }
        }
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(pc.getMatrix()[i][j] == 0) {
                    final Field test = new Field(pc.getRids()[i*10 + j], i*10 + j, Field.EMPTY);
                    test.getButton().setOnClickListener(new OnClickListener() {
                        // player missed
                        public void onClick(View v) {
                            if(test.getState()==Field.EMPTY) {
                                test.setState(Field.MISSED);
                                test.getButton().setClickable(false);
                            }
                            if(test.getState()==Field.SHIP) {
                                test.setState(Field.SHOT);
                                test.getButton().setClickable(false);
                            }
                            pc.makeMove(player, mode, a);
                        }
                    });
                }
            }
        }
    }

    public void initMultiPlayerMode(final Activity a) {
        final ResultHandler resultHandler = this;
        final ConnectedThread connectedThread = new ConnectedThread(resultHandler, a);
        connectedThread.execute();
        Ship[] ships = opponent.getShips();
        for (final Ship ship : ships) {
            for (int j = 0; j < ship.getLength(); j++) {
                final Field pole = ship.getField(j);
                final int fX = pole.getNr()/10;
                final int fY = pole.getNr()%10;
                pole.getButton().setOnClickListener(new OnClickListener() {
                    private int x = fX;
                    private int y = fY;
                    // player hit ship
                    public void onClick(View v) {
                        end:
                        {
                            pole.getButton().setClickable(false);
                            if (pole.getState() == Field.SHIP) {
                                pole.setState(Field.SHOT);
                                ship.updateState();
                                if (ship.getSinkState()) {
                                    for (int j = 0; j < ship.getLength(); j++)
                                        ship.getField(j).setState(Field.SINK);
                                    opponent.shipCounter--;
                                    if (opponent.shipCounter == 0) {
                                        a.setContentView(R.layout.win);
                                        connectedThread.write(ConnectedThread.intArrayToByteArray(new int[]{ -1, -1}));
                                        break end;
                                    }
                                }
                            }
                            connectedThread.write(ConnectedThread.intArrayToByteArray(new int[]{ x, y }));
                            setLock(true);
                        }
                    }
                });
            }
        }
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(opponent.getMatrix()[i][j] == 0) {
                    final Field test = new Field(opponent.getRids()[i*10 + j], i*10 + j, Field.EMPTY);
                    final int fX = i;
                    final int fY = j;
                    test.getButton().setOnClickListener(new OnClickListener() {
                        private int x = fX;
                        private int y = fY;
                        // player missed
                        public void onClick(View v) {
                            if(test.getState()==Field.EMPTY) {
                                test.setState(Field.MISSED);
                                test.getButton().setClickable(false);
                            }
                            if(test.getState()==Field.SHIP) {
                                test.setState(Field.SHOT);
                                test.getButton().setClickable(false);
                            }
                            connectedThread.write(ConnectedThread.intArrayToByteArray(new int[]{ x, y }));
                            setLock(true);
                        }
                    });
                }
            }
        }
    }

    public void startGame(Activity a) {
        if(mode == ShipsActivity.SINGLE_PLAYER_MODE) {
            initSinglePlayer(a);
        }
        else {
            initMultiPlayerMode(a);
        }
    }

    @Override
    public void handleResult(int[] ints, Activity activity) {
        int x = ints[0];
        int y = ints[1];
        if(x == -1 && y == -1) {
            activity.setContentView(R.layout.lost);
            return;
        }
        if (player.getMatrix()[x][y] == 0) {
            new Field(player.getRids()[x * 10 + y], x * 10 + y, Field.MISSED);
        }
        if (player.getMatrix()[x][y] == 1) {
            for (int i = 0; i < 10; i++) {
                Ship ship = player.getShips()[i];
                for (int j = 0; j < ship.getLength(); j++) {
                    if (ship.getField(j).getNr() == x * 10 + y) {
                        ship.getField(j).setState(Field.SHOT);
                        ship.updateState();
                        if (ship.getSinkState()) {
                            for (int k = 0; k < ship.getLength(); k++)
                                ship.getField(k).setState(Field.SINK);
                            player.shipCounter--;
                        }
                    }
                }
            }
        }
        setLock(false);
    }

    private void setLock(boolean lock) {
        gameShield.setVisibility(lock ? View.VISIBLE : View.GONE);
        for(View rid : rids) {
            rid.setClickable(!lock);
        }
    }
}
