package kutepov.ru.smarttool;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import kutepov.ru.smarttool.common.Constants;
import kutepov.ru.smarttool.db.dao.ProfileDao;
import kutepov.ru.smarttool.db.entity.Profile;
import kutepov.ru.smarttool.db.helper.DatabaseHelper;

public class BluetoothService extends Service {

    private DatabaseHelper databaseHelper;

    private String address;

    private BluetoothAdapter bluetooth;
    private BluetoothSocket socket;
    private BluetoothDevice device;

    public BluetoothService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.address = intent.getStringExtra(Constants.MAC);

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
        databaseHelper.close();

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
            Profile profile;
            try {
                ProfileDao profileDao = databaseHelper.getProfileDao();
                profile = profileDao.queryForId(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            if (profile != null) {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(profile.getUuid().toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
