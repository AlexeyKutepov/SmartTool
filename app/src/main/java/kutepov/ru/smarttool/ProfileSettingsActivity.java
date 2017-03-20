package kutepov.ru.smarttool;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.sql.SQLException;

import kutepov.ru.smarttool.db.dao.ProfileDao;
import kutepov.ru.smarttool.db.entity.Profile;
import kutepov.ru.smarttool.db.helper.DatabaseHelper;

public class ProfileSettingsActivity extends OrmLiteBaseActivity<DatabaseHelper> {

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

        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextMiddleName = (EditText) findViewById(R.id.editTextMiddleName);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);

        try {
            profileDao = getHelper().getProfileDao();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
