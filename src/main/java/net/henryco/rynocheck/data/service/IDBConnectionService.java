package net.henryco.rynocheck.data.service;

import com.j256.ormlite.support.ConnectionSource;

import java.io.Closeable;
import java.util.function.Consumer;

/**
 * @author Henry on 13/01/18.
 */
public interface IDBConnectionService extends Closeable {


	final class Factory {

		public static IDBConnectionService sqlite(String dataBaseFile) {
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
		default void close() {

		}
	}

	ConnectionHandler connect();

}