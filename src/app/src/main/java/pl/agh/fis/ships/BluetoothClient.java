package pl.agh.fis.ships;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothClient extends Thread {
    private BluetoothSocket bluetoothSocket;
    private ShipsActivity shipsActivity;

    public BluetoothClient(ShipsActivity shipsActivity) {
        BluetoothDevice bluetoothDevice = shipsActivity.getBluetoothDevice();
        this.shipsActivity = shipsActivity;
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(ShipsActivity.SHIPS_UUID);
        } catch (Exception e) {
            Log.e(BluetoothClient.class.getSimpleName(), "Error while creating socket.", e);
        }
    }

    @Override
    public void run() {
        shipsActivity.getBluetoothAdapter().cancelDiscovery();
        try {
            bluetoothSocket.connect();
            shipsActivity.manageConnectedSocket(bluetoothSocket, MultiPlayerMode.CLIENT);
        } catch (Exception e) {
            Log.e(BluetoothClient.class.getSimpleName(), "Trouble with connecting to socket.", e);
            try {
                bluetoothSocket.close();
            } catch (Exception ignore) { }
        }
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (Exception ignore) { }
    }
}
