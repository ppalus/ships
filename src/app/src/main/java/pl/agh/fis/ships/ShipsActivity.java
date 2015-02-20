package pl.agh.fis.ships;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ShipsActivity extends Activity implements ResultHandler {

    public final static String NAME = "SHIPS";
    public final static UUID SHIPS_UUID = UUID.nameUUIDFromBytes(NAME.getBytes());

    private final static int SCREEN_GAMER_CHOOSE = 0;
    private final static int SCREEN_BLUETOOTH_LIST = 1;
    private final static int SCREEN_DIFFICULTY_CHOOSE = 2;
    private final static int SCREEN_GRID_SHIP = 3;

    private final static int BLUETOOTH_INTENT_ENABLE = 1;
    private final static int BLUETOOTH_INTENT_DISCOVERABLE = 2;

    public final static int SINGLE_PLAYER_MODE = 1;
    public final static int MULTI_PLAYER_MODE = 2;

    static int previousView = 0;
    static int state = 0;
    static int mode = 0;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> BTArrayAdapter;
    private List<BluetoothDevice> foundDevices;
    private MultiPlayerMode multiPlayerMode;
    private BluetoothDevice bluetoothDevice;

    private BluetoothServer bluetoothServer;
    private BluetoothClient bluetoothClient;
    private Editor rGrid;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundDevices.add(device);
                BTArrayAdapter.add(device.getName());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void setContentViewBluetoothList() {
        previousView = SCREEN_BLUETOOTH_LIST;
        setContentView(R.layout.bluetooth_list);
        ListView listView = (ListView) findViewById(R.id.BluetoothDeviceList);
        listView.setAdapter(BTArrayAdapter);
        final ShipsActivity shipsActivity = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothDevice = getBluetoothDeviceByName((String) parent.getItemAtPosition(position));
                bluetoothClient = new BluetoothClient(shipsActivity);
                bluetoothClient.start();
            }
        });
        findViewById(R.id.searchDevices).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findBluetoothDevices();
            }
        });
        findViewById(R.id.serverCreate).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), BLUETOOTH_INTENT_DISCOVERABLE);
            }
        });
        turnOnBluetooth();
    }

    public void setContentViewGamerChoose() {
        previousView = SCREEN_GAMER_CHOOSE;
        setContentView(R.layout.gamer_choose);
        Button button_single = (Button) findViewById(R.id.singlePlayer);
        button_single.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                previousView = SCREEN_DIFFICULTY_CHOOSE;
                mode = SINGLE_PLAYER_MODE;
                setContentViewDifficultyChoose();
            }
        });
        Button multiPlayer = (Button) findViewById(R.id.multiPlayer);
        final Activity activity = this;
        multiPlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                previousView = SCREEN_BLUETOOTH_LIST;
                mode = MULTI_PLAYER_MODE;
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    Toast.makeText(activity, "Bluetooth nie wspierany.", Toast.LENGTH_LONG).show();
                    return;
                }
                setContentViewBluetoothList();
            }
        });
    }

    public void setContentViewDifficultyChoose() {
        previousView = SCREEN_DIFFICULTY_CHOOSE;
        setContentView(R.layout.difficulty_choose);
        findViewById(R.id.easy).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (state < 10)
                    state += 20;
                previousView = SCREEN_GRID_SHIP;
                setContentViewGridShip();
            }
        });
        findViewById(R.id.hard).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (state < 10)
                    state += 20;
                previousView = SCREEN_GRID_SHIP;
                setContentViewGridShip();
            }
        });
    }

    public void setContentViewGridShip() {
        previousView = SCREEN_GRID_SHIP;
        setContentView(R.layout.ship_grid_choose);
        DisplayMetrics scrSize = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(scrSize);

        int[] rids = {R.id.a0, R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7, R.id.a8, R.id.a9
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
        for (int i = 0; i < 100; i++) {
            views[i] = findViewById(rids[i]);
            views[i].getLayoutParams().height = scrSize.widthPixels / 10;
            views[i].getLayoutParams().width = scrSize.widthPixels / 10;
        }
        int[] rids2 = {R.id.ship1, R.id.ship2, R.id.ship3, R.id.ship4};
        final View[] sets = new View[4];
        for (int i = 0; i < 4; i++) {
            sets[i] = findViewById(rids2[i]);

        }
        Button buttonRandom = (Button) findViewById(R.id.random);
        Button buttonClear = (Button) findViewById(R.id.clear);
        rGrid = new Editor(views, true, sets);
        rGrid.init();
        buttonClear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rGrid.clearGrid();
            }
        });
        buttonRandom.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rGrid.randomize();
            }
        });
        Button buttonPlay = (Button) findViewById(R.id.next);
        final ShipsActivity shipsActivity = this;
        buttonPlay.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (rGrid.isReady()) {
                    switch(mode) {
                        case MULTI_PLAYER_MODE:
                            ConnectedThread connectedThread = new ConnectedThread(shipsActivity ,shipsActivity);
                            connectedThread.execute();
                            byte[] gridBytes = ConnectedThread.intArrayToByteArray(rGrid.getParsedGrid());
                            connectedThread.write(gridBytes);
                            break;
                        case SINGLE_PLAYER_MODE:
                            Intent myIntent = new Intent(ShipsActivity.this, BattleShipsGame.class);
                            myIntent.putExtra(BattleShipsGame.EXTRA_PARSED_GRID, rGrid.getParsedGrid());
                            myIntent.putExtra(BattleShipsGame.EXTRA_STATE, state);
                            myIntent.putExtra(BattleShipsGame.EXTRA_MODE, SINGLE_PLAYER_MODE);
                            ShipsActivity.this.startActivity(myIntent);
                            break;
                    }
                } else {
                    message();
                }
            }
        });
    }

    public void handleResult(int[] parsedGrid, Activity activity) {
        Intent myIntent = new Intent(ShipsActivity.this, BattleShipsGame.class);
        myIntent.putExtra(BattleShipsGame.EXTRA_PARSED_GRID, rGrid.getParsedGrid());
        myIntent.putExtra(BattleShipsGame.EXTRA_OPPONENTS_PARSED_GRID, parsedGrid);
        myIntent.putExtra(BattleShipsGame.EXTRA_MODE, MULTI_PLAYER_MODE);
        myIntent.putExtra(BattleShipsGame.EXTRA_SERVER, multiPlayerMode == MultiPlayerMode.SERVER);
        ShipsActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch(Exception ignore) { /* receiver not registered, no big deal, ur server it's normal */ }
    }


    public void message() {
        Toast.makeText(getApplicationContext(), "Aby rozpoczac gre, wylosuj ustawienie statkow na planszy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        foundDevices = new ArrayList<>();
        setContentViewGamerChoose();
    }

    public void endGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy chcesz opóścić grę?").setCancelable(false).setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                invalidateBluetooth();
                ShipsActivity.this.finish();
            }
        }).setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
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
        switch (Item.getItemId()) {
            case R.id.end:
                endGame();
                return true;
            default:
                return super.onOptionsItemSelected(Item);
        }
    }

    @Override
    public void onBackPressed() {
        switch (previousView) {
            case SCREEN_BLUETOOTH_LIST:
                setContentViewGamerChoose();
                invalidateBluetooth();
                break;
            case SCREEN_DIFFICULTY_CHOOSE:
                setContentViewGamerChoose();
                break;
            case SCREEN_GRID_SHIP:
                switch(mode) {
                    case SINGLE_PLAYER_MODE:
                        setContentViewDifficultyChoose();
                        break;
                    case MULTI_PLAYER_MODE:
                        setContentViewBluetoothList();
                        break;
                }
                break;
            case SCREEN_GAMER_CHOOSE:
                endGame();
                break;
            default:
                super.onBackPressed();
        }
    }

    public void turnOnBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    public void findBluetoothDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            ((Button) findViewById(R.id.searchDevices)).setText("FIND DEVICES");
        } else {
            ((Button) findViewById(R.id.searchDevices)).setText("STOP SEARCHING");
            BTArrayAdapter.clear();
            bluetoothAdapter.startDiscovery();
            registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void manageConnectedSocket(BluetoothSocket bluetoothSocket, MultiPlayerMode multiPlayerMode) {
        if(bluetoothSocket != null) {
            Globals.bluetoothSocket = bluetoothSocket;
            this.multiPlayerMode = multiPlayerMode;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentViewGridShip();
                }
            });

        }
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDeviceByName(String name) {
        for(BluetoothDevice device : foundDevices) {
            if(name.equals(device.getName())) {
                return device;
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case BLUETOOTH_INTENT_DISCOVERABLE:
                bluetoothServer = new BluetoothServer(this);
                bluetoothServer.start();
                return;
            case BLUETOOTH_INTENT_ENABLE:
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void invalidateBluetooth() {
        if(bluetoothServer != null) {
            bluetoothServer.cancel();
        }
        if(bluetoothClient != null) {
            bluetoothClient.cancel();
        }
        if(bluetoothAdapter != null) {
            bluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "Bluetooth wyłączony", Toast.LENGTH_LONG).show();
        }
    }
}