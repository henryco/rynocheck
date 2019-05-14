package net.henryco.rynocheck.data.dao.session;



import com.github.henryco.injector.meta.annotations.Provide;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Provide
@Singleton
public class SessionDaoImp implements SessionDao {

	private final Map<UUID, String> sessionMap;

	public SessionDaoImp() {
		this.sessionMap = new HashMap<>();
	}

	@Override
	public void addSession(String username, UUID uuid) {
		if (username == null || username.trim().isEmpty() || uuid == null)
			return;
		sessionMap.put(uuid, username);
	}

	@Override
	public void removeSession(String username) {
		UUID key = null;
		for (Map.Entry<UUID, String> entry : sessionMap.entrySet()) {
			if (entry.getValue().equals(username)) {
				key = entry.getKey();
				break;
			}
		}
		if (key != null) sessionMap.remove(key);
	}

	@Override
	public String removeSession(UUID uuid) {
		return sessionMap.remove(uuid);
	}

	@Override
	public boolean isSessionExist(String username) {
		return sessionMap.containsValue(username);
	}

	@Override
	public boolean isSessionExist(UUID uuid) {
		return sessionMap.containsKey(uuid);
	}

	@Override
	public String getSessionName(UUID uuid) {
		return sessionMap.get(uuid);
	}

	@Override
	public UUID getSessionId(String username) {
		if (username == null || username.trim().isEmpty()) return null;
		for (Map.Entry<UUID, String> entry : sessionMap.entrySet())
			if (entry.getValue().equals(username)) return entry.getKey();
		return null;
	}
}