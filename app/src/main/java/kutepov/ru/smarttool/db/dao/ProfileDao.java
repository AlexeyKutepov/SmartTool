package kutepov.ru.smarttool.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

import kutepov.ru.smarttool.db.entity.Profile;

/**
 * DAO для сущности Profile
 */

public class ProfileDao extends BaseDaoImpl<Profile, Integer> {

    public ProfileDao(Class<Profile> dataClass) throws SQLException {
        super(dataClass);
    }

    public ProfileDao(ConnectionSource connectionSource, Class<Profile> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public ProfileDao(ConnectionSource connectionSource, DatabaseTableConfig<Profile> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }
}
