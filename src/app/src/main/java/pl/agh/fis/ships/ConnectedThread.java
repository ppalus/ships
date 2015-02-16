package pl.agh.fis.ships;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ConnectedThread extends AsyncTask<Void, Void, Void> {

    private static final int BUFFER_SIZE = 1024;

    private ResultHandler resultHandler;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] buffer;

    public ConnectedThread(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
        buffer = new byte[BUFFER_SIZE];
        try {
            inputStream = Globals.bluetoothSocket.getInputStream();
            outputStream = Globals.bluetoothSocket.getOutputStream();
        } catch (Exception e) {
            Log.e(ConnectedThread.class.getSimpleName(), e.toString(), e);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (inputStream.read(buffer) == -1) {
                throw new Exception("Error reading data.");
            }
        } catch (Exception e) {
            Log.e(ConnectedThread.class.getSimpleName(), e.toString(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        resultHandler.handleResult(byteArrayToIntArray(buffer));
    }

    public static byte[] intArrayToByteArray(int[] ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; ++i) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

    public static int[] byteArrayToIntArray(byte[] bytes) {
        int[] ints = new int[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            ints[i] = (int) bytes[i];
        }
        return ints;
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (Exception e) {
            Log.e(ConnectedThread.class.getSimpleName(), e.toString(), e);
        }
    }

    public void cancel() {
        try {
            Globals.bluetoothSocket.close();
        } catch (Exception e) {
            Log.e(ConnectedThread.class.getSimpleName(), e.toString(), e);
        }
    }
}


