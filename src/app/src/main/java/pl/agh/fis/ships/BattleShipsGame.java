package pl.agh.fis.ships;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class BattleShipsGame extends Activity {

    public final static String EXTRA_PARSED_GRID = "PARSED_GRID";
    public final static String EXTRA_OPPONENTS_PARSED_GRID = "OPPONENTS_PARSED_GRID";
    public final static String EXTRA_STATE = "STATE";
    public final static String EXTRA_MODE = "MODE";
    public final static String EXTRA_SERVER = "SERVER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        int[] parsedGrid = extras.getIntArray(EXTRA_PARSED_GRID);
        int[] opponentsParsedGrid = extras.getIntArray(EXTRA_OPPONENTS_PARSED_GRID);
        int state = extras.getInt(EXTRA_STATE);
        int mode = extras.getInt(EXTRA_MODE);
        boolean isServer = extras.getBoolean(EXTRA_SERVER);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.game_grid);

        DisplayMetrics scrSize = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(scrSize);


        // podglad
        int[] rids = {R.id.b0 , R.id.b1 , R.id.b2 , R.id.b3 , R.id.b4 , R.id.b5 , R.id.b6 , R.id.b7 , R.id.b8 , R.id.b9
                , R.id.b10, R.id.b11, R.id.b12, R.id.b13, R.id.b14, R.id.b15, R.id.b16, R.id.b17, R.id.b18, R.id.b19
                , R.id.b20, R.id.b21, R.id.b22, R.id.b23, R.id.b24, R.id.b25, R.id.b26, R.id.b27, R.id.b28, R.id.b29
                , R.id.b30, R.id.b31, R.id.b32, R.id.b33, R.id.b34, R.id.b35, R.id.b36, R.id.b37, R.id.b38, R.id.b39
                , R.id.b40, R.id.b41, R.id.b42, R.id.b43, R.id.b44, R.id.b45, R.id.b46, R.id.b47, R.id.b48, R.id.b49
                , R.id.b50, R.id.b51, R.id.b52, R.id.b53, R.id.b54, R.id.b55, R.id.b56, R.id.b57, R.id.b58, R.id.b59
                , R.id.b60, R.id.b61, R.id.b62, R.id.b63, R.id.b64, R.id.b65, R.id.b66, R.id.b67, R.id.b68, R.id.b69
                , R.id.b70, R.id.b71, R.id.b72, R.id.b73, R.id.b74, R.id.b75, R.id.b76, R.id.b77, R.id.b78, R.id.b79
                , R.id.b80, R.id.b81, R.id.b82, R.id.b83, R.id.b84, R.id.b85, R.id.b86, R.id.b87, R.id.b88, R.id.b89
                , R.id.b90, R.id.b91, R.id.b92, R.id.b93, R.id.b94, R.id.b95, R.id.b96, R.id.b97, R.id.b98, R.id.b99};

        // plansza
        int[] rids2 = {R.id.a0 , R.id.a1 , R.id.a2 , R.id.a3 , R.id.a4 , R.id.a5 , R.id.a6 , R.id.a7 , R.id.a8 , R.id.a9
                , R.id.a10, R.id.a11, R.id.a12, R.id.a13, R.id.a14, R.id.a15, R.id.a16, R.id.a17, R.id.a18, R.id.a19
                , R.id.a20, R.id.a21, R.id.a22, R.id.a23, R.id.a24, R.id.a25, R.id.a26, R.id.a27, R.id.a28, R.id.a29
                , R.id.a30, R.id.a31, R.id.a32, R.id.a33, R.id.a34, R.id.a35, R.id.a36, R.id.a37, R.id.a38, R.id.a39
                , R.id.a40, R.id.a41, R.id.a42, R.id.a43, R.id.a44, R.id.a45, R.id.a46, R.id.a47, R.id.a48, R.id.a49
                , R.id.a50, R.id.a51, R.id.a52, R.id.a53, R.id.a54, R.id.a55, R.id.a56, R.id.a57, R.id.a58, R.id.a59
                , R.id.a60, R.id.a61, R.id.a62, R.id.a63, R.id.a64, R.id.a65, R.id.a66, R.id.a67, R.id.a68, R.id.a69
                , R.id.a70, R.id.a71, R.id.a72, R.id.a73, R.id.a74, R.id.a75, R.id.a76, R.id.a77, R.id.a78, R.id.a79
                , R.id.a80, R.id.a81, R.id.a82, R.id.a83, R.id.a84, R.id.a85, R.id.a86, R.id.a87, R.id.a88, R.id.a89
                , R.id.a90, R.id.a91, R.id.a92, R.id.a93, R.id.a94, R.id.a95, R.id.a96, R.id.a97, R.id.a98, R.id.a99};

        final View[] views = new View[100];
        final View[] views2 = new View[100];
        for(int i = 0; i < 100; i++) {
            views[i] = findViewById(rids[i]);
            views[i].getLayoutParams().height = scrSize.widthPixels/20;
            views[i].getLayoutParams().width = scrSize.widthPixels/20;

            views2[i] = findViewById(rids2[i]);
            views2[i].getLayoutParams().height = scrSize.widthPixels/10;
            views2[i].getLayoutParams().width = scrSize.widthPixels/10;
        }
        Game theGame;
        if(mode == ShipsActivity.SINGLE_PLAYER_MODE) {
            Player rGrid = new Player(parsedGrid, views, true);
            AIPlayer rGrid2 = new AIPlayer(views2);
            theGame = new Game(rGrid, rGrid2, state == 21 ? AIPlayer.EASY : AIPlayer.HARD);
        }
        else {
            Player rGrid = new Player(parsedGrid, views, true);
            Player rGrid2 = new Player(opponentsParsedGrid, views2);
            theGame = new Game(rGrid, rGrid2, findViewById(R.id.gameShield), views2, isServer);
        }
        theGame.startGame(this);
    }




    public void endGame() {
        AlertDialog.Builder bilder = new AlertDialog.Builder(this);
        bilder.setMessage("Czy chcesz opóścić grę?").setCancelable(false).setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BattleShipsGame.this.finish();
            }
        }).setNegativeButton("Nie", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = bilder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {
        switch(Item.getItemId()) {
            case R.id.end:
                endGame();
                return true;
            default:
                return super.onOptionsItemSelected(Item);
        }
    }

    @Override
    public void onBackPressed() {
        endGame();
    }

}
