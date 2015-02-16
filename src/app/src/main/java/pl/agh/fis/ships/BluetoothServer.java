package pl.agh.fis.ships;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class BluetoothServer extends Thread {
    private BluetoothServerSocket serverSocket;
    private ShipsActivity shipsActivity;

    public BluetoothServer(ShipsActivity shipsActivity) {
        this.shipsActivity = shipsActivity;
        BluetoothServerSocket bluetoothServerSocket = null;
        BluetoothAdapter bluetoothAdapter = shipsActivity.getBluetoothAdapter();
        try {
            bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(ShipsActivity.NAME, ShipsActivity.SHIPS_UUID);
        } catch (IOException ignore) { }
        serverSocket = bluetoothServerSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                BluetoothSocket bluetoothSocket = serverSocket.accept();
                shipsActivity.manageConnectedSocket(bluetoothSocket, MultiPlayerMode.SERVER);
            } catch (Exception e) {
                Log.e(BluetoothServer.class.getSimpleName(), "Exception while obtaining Bluetooth socket.", e);
                break;
            }
        }
    }

    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException ignore) { }
    }
}
