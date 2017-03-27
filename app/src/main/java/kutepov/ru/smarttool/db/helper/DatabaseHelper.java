package kutepov.ru.smarttool.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.UUID;

import kutepov.ru.smarttool.R;
import kutepov.ru.smarttool.db.dao.ProfileDao;
import kutepov.ru.smarttool.db.entity.Profile;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "app.db";

    private static final int DATABASE_VERSION = 1;

    /**
     * DAO
     */
    private ProfileDao profileDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Profile.class);

            populateProfile();
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Profile.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        profileDao = null;
    }

    /**
     * Получить DAO для сущности Profile
     * @return {@link Dao}
     * @throws SQLException ошибка
     */
    public ProfileDao getProfileDao() throws SQLException {
        if (profileDao == null) {
            profileDao = new ProfileDao(connectionSource, Profile.class);
        }
        return profileDao;
    }

    /**
     * Создаём запись с UUID в таблице Profile
     * @throws SQLException
     */
    private void populateProfile() throws SQLException {
        Profile profile = new Profile(
                1,
                UUID.randomUUID()
        );
        profileDao = getProfileDao();
        profileDao.createOrUpdate(profile);
    }
}
