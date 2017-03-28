package kutepov.ru.smarttool;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kutepov.ru.smarttool.common.Constants;
import kutepov.ru.smarttool.db.dao.ProfileDao;
import kutepov.ru.smarttool.db.entity.Profile;
import kutepov.ru.smarttool.db.helper.DatabaseHelper;

public class SearchDeviceActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetooth;
    private Intent bluetoothService;

    private DatabaseHelper databaseHelper;
    private ProfileDao profileDao;

    private ListView listViewDevices;
    private ArrayAdapter<String> arrayAdapter;
    private ProgressDialog progressDialog;
    private WaitingSearchResultTask searchResultTask;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        /*
         * База данных
         */
        databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {
            profileDao = databaseHelper.getProfileDao();
        } catch (SQLException e) {
            e.printStackTrace();
            finish();
        }

        /*
         * Интерфейс
         */
        listViewDevices = (ListView) findViewById(R.id.listViewDevices);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewDevices.setAdapter(arrayAdapter);
        listViewDevices.setOnItemClickListener(onItemClickListener);

        /*
         * Диалоговые окна
         */
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.search_devices_dialog_title);
        progressDialog.setMessage(getResources().getString(R.string.search_devices_dialog_message));

        builder = new AlertDialog.Builder(this);

        /*
         * Задачи
         */
        searchResultTask = new WaitingSearchResultTask();

        /*
         * bluetooth
         */
        bluetoothService = new Intent(this, BluetoothService.class);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) {
            if (bluetooth.isEnabled()) {
                IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
                bluetooth.startDiscovery();
                searchResultTask.execute();
            } else {
                // Bluetooth выключен. Предложим пользователю включить его.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            builder.setMessage(R.string.bluetooth_is_not_supported_message)
                    .setTitle(R.string.bluetooth_is_not_supported_title);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
            bluetooth.startDiscovery();
            searchResultTask.execute();
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    /**
     * Выбор устройства для подключения и соединение с ним
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            stopService(bluetoothService);
            Object item = parent.getItemAtPosition(position);
            String address = item.toString().split("\n")[1];
            bluetooth.cancelDiscovery();
            bluetoothService.putExtra(Constants.MAC, address);
            Profile profile;
            try {
                profile = profileDao.queryForId(1);
                if (profile == null) {
                    profile = new Profile(
                            1,
                            UUID.randomUUID()
                    );
                } else if (profile.getUuid() == null) {
                    profile.setUuid(UUID.randomUUID());
                }
                profileDao.createOrUpdate(profile);
            } catch (SQLException e) {
                return;
            }
            bluetoothService.putExtra(Constants.PROFILE_UUID, profile.getUuid().toString());

            startService(bluetoothService);
        }
    };


    /**
     * Поиск bluetooth-устройств
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Когда найдено новое устройство
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Получаем объект BluetoothDevice из интента
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Добавляем имя и адрес в array adapter, чтобы показвать в ListView
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                runOnUiThread(updateListView);
            }
        }
    };

    Runnable updateListView = new Runnable(){
        public void run(){
            arrayAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Ожидание завершения поиска устройств
     */
    private class WaitingSearchResultTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (bluetooth.isDiscovering()) {
                continue;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (arrayAdapter.isEmpty()) {
                builder.setMessage(R.string.devices_not_found_dialog_message)
                       .setTitle(R.string.devices_not_found_dialog_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
}
