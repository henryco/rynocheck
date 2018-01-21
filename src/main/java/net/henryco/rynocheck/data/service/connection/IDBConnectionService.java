package net.henryco.rynocheck.data.service.connection;

import com.j256.ormlite.support.ConnectionSource;
import lombok.extern.java.Log;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Henry on 13/01/18.
 */
public interface IDBConnectionService extends Closeable {

	@Log
	final class Factory {

		public static IDBConnectionService sqlite(String dataBaseFile) {
			log.info("Creating sqlite IDBConnectionService: " + dataBaseFile);
			return new DBConnectionService("jdbc:sqlite:" + dataBaseFile);
		}

		// todo more databases
	}


	interface ConnectionHandler {

		default ConnectionHandler then(Consumer<ConnectionSource> sourceConsumer) {
			return this;
		}
		default ConnectionHandler exceptionally(Consumer<Exception> consumer) {
			return this;
		}

		ConnectionSource getSource();

		default void close() {

		}
	}

	ConnectionHandler connect();
	default ConnectionHandler forceConnect() throws IOException {
		this.close();
		return connect();
	}

}