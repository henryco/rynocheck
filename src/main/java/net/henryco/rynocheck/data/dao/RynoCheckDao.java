package net.henryco.rynocheck.data.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public abstract class RynoCheckDao<T, ID> extends BaseDaoImpl<T, ID> {


	public RynoCheckDao(ConnectionSource connectionSource,
						Class<T> dataClass) throws SQLException {
		super(connectionSource, dataClass);
		TableUtils.createTableIfNotExists(connectionSource, dataClass);
	}


	protected static boolean assertString(String string) {
		return (string != null && !string.trim().isEmpty());
	}
}