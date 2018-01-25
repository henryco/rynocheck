package net.henryco.rynocheck.data.dao.session;

import java.util.UUID;

public interface SessionDao {


	void addSession(String username, UUID uuid);

	void removeSession(String username);

	void removeSession(UUID uuid);

	boolean isSessionExist(String username);

	boolean isSessionExist(UUID uuid);

	String getSessionName(UUID uuid);

	UUID getSessionId(String username);
}