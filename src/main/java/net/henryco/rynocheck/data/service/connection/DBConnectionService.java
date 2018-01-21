package net.henryco.rynocheck.data.service.connection;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.extern.java.Log;
import lombok.val;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Henry on 13/01/18.
 */ @Log
public class DBConnectionService implements IDBConnectionService {

	private ConnectionSource connectionSource;
	private final String URL;

	DBConnectionService(String URL) {
		this.URL = URL;
	}

	@Override
	public ConnectionHandler connect() {

		log.info("connect();");
		final ConnectionHandler handler =  new ConnectionHandler() {

			private Consumer<Exception> exceptionConsumer = e -> {
				log.throwing("ConnectionHandler", "connect", e);
				e.printStackTrace();
			};

			@Override
			public ConnectionHandler then(Consumer<ConnectionSource> sourceConsumer) {

				try {
					sourceConsumer.accept((DBConnectionService.this.connectionSource));
				} catch (Exception e) {
					exceptionConsumer.accept(e);
				}

				return this;
			}

			@Override
			public ConnectionHandler exceptionally(Consumer<Exception> consumer) {
				this.exceptionConsumer = consumer;
				return this;
			}

			@Override
			public ConnectionSource getSource() {
				log.info("getSource: " + DBConnectionService.this.connectionSource);
				return DBConnectionService.this.connectionSource;
			}

			@Override
			public void close() {
				try {
					DBConnectionService.this.close();
				} catch (Exception e) {
					exceptionConsumer.accept(e);
				}
			}
		};


		if (connectionSource == null) {
			try (val connectionSource = new JdbcPooledConnectionSource(URL)) {
				this.connectionSource = connectionSource;
				log.info("connectionSource: " + connectionSource);
			} catch (Exception e) {
				e.printStackTrace();
				log.throwing(this.getClass().getName(), "connect", e);
			}
		}

		return handler;
	}


	@Override
	public void close() throws IOException {

		if (connectionSource == null) return;

		connectionSource.close();
		connectionSource = null;
	}

}