package pl.agh.fis.ships;

import android.view.View;

public class Player extends Grid {

    public Player(int[] parsedGrid, View[] rids, boolean fake) {
        super(parsedGrid, rids, fake);
    }

    public Player(int[] parsedGrid, View[] rids) {
        super(parsedGrid, rids);
    }

    public Player(View[] rids) {
        super(rids);
    }
}
