package kutepov.ru.smarttool;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kutepov.ru.smarttool.common.Constants;


public class BluetoothService extends Service {


    private String address;
    private String uuid;

    private BluetoothAdapter bluetooth;
    private BluetoothSocket socket;
    private BluetoothDevice device;

    public BluetoothService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetooth = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.address = intent.getStringExtra(Constants.MAC);
        this.uuid = intent.getStringExtra(Constants.PROFILE_UUID);

        if (bluetooth != null && bluetooth.isEnabled()) {
            if (socket == null || !socket.isConnected()) {
                connect();
            }
        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Установить соеденение
     */
    private void connect() {
        device = bluetooth.getRemoteDevice(this.address);

        try {
            socket = device.createRfcommSocketToServiceRecord(Constants.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.connect();
            syncData();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e2) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Синхронизация данных по bluetooth
     */
    private void syncData() {
        if (socket.isConnected()) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(uuid.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получение данных от устройства
     */
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;

        public ConnectedThread() {
            InputStream tmpIn = null;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException ignored) {
            }
            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];// буферный массив
            int bytes;

            while (socket.isConnected()) {
                try {
                    bytes = mmInStream.read(buffer);
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
