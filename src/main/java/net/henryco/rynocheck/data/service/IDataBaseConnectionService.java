package net.henryco.rynocheck.data.service;

import com.j256.ormlite.support.ConnectionSource;

import java.util.function.Consumer;

/**
 * @author Henry on 13/01/18.
 */
public interface IDataBaseConnectionService {


	final class Factory {

		public static IDataBaseConnectionService sqliteDataBase(String dataBaseFile) {
			return new DataBaseConnectionService("jdbc:sqlite:" + dataBaseFile);
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
	void close();

}