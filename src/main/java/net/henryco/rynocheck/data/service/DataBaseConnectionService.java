package net.henryco.rynocheck.data.service;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Henry on 13/01/18.
 */
public class DataBaseConnectionService implements IDataBaseConnectionService {

	private ConnectionSource connectionSource;
	private final String URL;

	DataBaseConnectionService(String URL) {
		this.URL = URL;
	}

	@Override
	public ConnectionHandler connect() {

		try (JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource(URL)) {
			this.connectionSource = connectionSource;
			return new ConnectionHandler() {
				@Override
				public ConnectionHandler then(Consumer<ConnectionSource> sourceConsumer) {
					sourceConsumer.accept((DataBaseConnectionService.this.connectionSource));
					return this;
				}
				@Override
				public void close() {
					DataBaseConnectionService.this.close();
				}
			};
		} catch (Exception e) {
			return new ConnectionHandler() {
				@Override
				public ConnectionHandler exceptionally(Consumer<Exception> consumer) {
					consumer.accept(e);
					return this;
				}
			};
		}
	}

	@Override
	public void close() {
		try {
			connectionSource.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}