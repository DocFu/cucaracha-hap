package de.plasmawolke.cucaracha;

import java.io.Serializable;
import java.math.BigInteger;

import com.beowulfe.hap.HomekitAuthInfo;

/**
 * HAP AuthInfo
 * @author Arne Schueler
 */
public class HomekitAuthInfoImpl implements HomekitAuthInfo, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8686844907158780299L;

	private String pin;
	private String mac;
	private BigInteger salt;
	private byte[] privateKey;

	@Override
	public void createUser(String username, byte[] publicKey) {
		System.out.println("Creating user " + username);
		UserManager.get().createUser(username, publicKey);

	}

	@Override
	public void removeUser(String username) {
		System.out.println("Removing user " + username);
		UserManager.get().removeUser(username);
	}

	@Override
	public byte[] getUserPublicKey(String username) {
		System.out.println("Requesting user " + username);
		return UserManager.get().getUserPublicKey(username);
	}

	// getters setters

	@Override
	public String getPin() {
		return pin;
	}

	@Override
	public String getMac() {
		return mac;
	}

	@Override
	public BigInteger getSalt() {
		return salt;
	}

	@Override
	public byte[] getPrivateKey() {
		return privateKey;
	}

	/**
	 * @param pin
	 *            the pin to set
	 */
	public final void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * @param mac
	 *            the mac to set
	 */
	public final void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * @param salt
	 *            the salt to set
	 */
	public final void setSalt(BigInteger salt) {
		this.salt = salt;
	}

	/**
	 * @param privateKey
	 *            the privateKey to set
	 */
	public final void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

}
