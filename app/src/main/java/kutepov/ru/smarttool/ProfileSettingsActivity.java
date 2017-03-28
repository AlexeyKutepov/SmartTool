package kutepov.ru.smarttool;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.sql.SQLException;

import kutepov.ru.smarttool.db.dao.ProfileDao;
import kutepov.ru.smarttool.db.entity.Profile;
import kutepov.ru.smarttool.db.helper.DatabaseHelper;

public class ProfileSettingsActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private AlertDialog.Builder builder;

    private EditText editTextLastName;
    private EditText editTextFirstName;
    private EditText editTextMiddleName;
    private EditText editTextPhone;
    private EditText editTextEmailAddress;

    private ProfileDao profileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        /*
         * Диалоговые окна
         */
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_profile_dialog_title)
                .setMessage(R.string.save_profile_dialog_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        /*
         * Интерфейс
         */
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextMiddleName = (EditText) findViewById(R.id.editTextMiddleName);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);

        /*
         * База данных
         */
        try {
            profileDao = getHelper().getProfileDao();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Сохранение данных
     * @param view view
     */
    public void onClickButtonSave(View view) {
        Profile profile = new Profile(
                1,
                editTextLastName.getText().toString(),
                editTextFirstName.getText().toString(),
                editTextMiddleName.getText().toString(),
                editTextPhone.getText().toString(),
                editTextEmailAddress.getText().toString()
        );
        try {
            profileDao.createOrUpdate(profile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

    /**
     * Загрузка данных из БД на форму
     */
    private void loadData() throws SQLException {
        Profile profile = profileDao.queryForId(1);
        editTextLastName.setText(profile.getLastName());
        editTextFirstName.setText(profile.getFirstName());
        editTextMiddleName.setText(profile.getMiddleName());
        editTextPhone.setText(profile.getPhone());
        editTextEmailAddress.setText(profile.getEmail());
    }


}
