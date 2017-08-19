package de.plasmawolke.cucaracha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Manages the HAP Users / Pairings
 * 
 * @author Arne Schueler
 */
public class UserManager {

	private static final File PAIRINGS_FILE = new File(Cucaracha.BASE_DIR, "pairings.ser");

	private ConcurrentMap<String, byte[]> userKeyMap = null;

	private static UserManager instance = new UserManager();

	public UserManager() {
		if (!PAIRINGS_FILE.exists()) {
			try {
				PAIRINGS_FILE.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (PAIRINGS_FILE.length() > 0) {
			try {
				userKeyMap = SerializationUtils.deserialize(new FileInputStream(PAIRINGS_FILE));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				userKeyMap = new ConcurrentHashMap<>();
			}
		} else {
			userKeyMap = new ConcurrentHashMap<>();
		}
	}

	public static synchronized final UserManager get() {
		return instance;
	}

	public void createUser(String username, byte[] publicKey) {
		userKeyMap.putIfAbsent(username, publicKey);
		serialize();
	}

	public void removeUser(String username) {
		userKeyMap.remove(username);
		serialize();
	}

	public byte[] getUserPublicKey(String username) {
		return userKeyMap.get(username);
	}

	private synchronized void serialize() {
		try {
			SerializationUtils.serialize((Serializable) userKeyMap, new FileOutputStream(PAIRINGS_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
