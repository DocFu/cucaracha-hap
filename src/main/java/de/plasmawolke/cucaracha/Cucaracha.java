package de.plasmawolke.cucaracha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

import com.beowulfe.hap.HomekitAuthInfo;
import com.beowulfe.hap.HomekitRoot;
import com.beowulfe.hap.HomekitServer;
import com.beowulfe.hap.impl.HomekitBridge;

/**
 * Cucaracha is a {@link HomekitBridge} running on a Raspberry PI wired with
 * PI4J to control GPIOs.
 * 
 * @author Arne Schueler
 */
public class Cucaracha {

	public static final File BASE_DIR = new File(System.getProperty("user.home") + "/.cucaracha/");
	private static final File AUTH_INFO_FILE = new File(BASE_DIR, "auth-info.ser");
	private static final File CONFIG_FILE = new File(BASE_DIR, "config.xml");

	private CucarachaConfig cfg = null;

	private List<LightEltako> lightEltakos = new ArrayList<>();

	/**
	 * Constructs the Application
	 */
	public Cucaracha() {

		try {

			// Initialize config (bridge config, switches,...)
			init();

			// Start HAP stuff
			startHomekitBridge();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets up configuration
	 * @throws IOException
	 *             if files could not be created
	 */
	private void init() throws IOException {

		if (!BASE_DIR.exists()) {
			BASE_DIR.mkdirs();
		}

		// try to get config from file
		// if config file exists, try to read and set config
		// else create a new file from a new CucarachaConfig instance
		if (CONFIG_FILE.exists()) {
			// TODO
		} else {
			cfg = new CucarachaConfig();
			// TODO unmarshal to file
			System.out.println("Created new config file [" + CONFIG_FILE
					+ "] with default configuration. Please edit according to your needs. Note: Your changes will take effect with next restart.");
		}

		// TODO Populate devices from config

		// some mock devices...

		LightEltako lightEltako1 = new LightEltako();
		lightEltako1.setHapId(2);
		lightEltako1.setHapLabel("Esstisch");
		lightEltakos.add(lightEltako1);

		LightEltako lightEltako2 = new LightEltako();
		lightEltako2.setHapId(3);
		lightEltako2.setHapLabel("Unterschrank");
		lightEltakos.add(lightEltako2);

		LightEltako lightEltako3 = new LightEltako();
		lightEltako3.setHapId(4);
		lightEltako3.setHapLabel("Theke");
		lightEltakos.add(lightEltako3);

		LightEltako lightEltako4 = new LightEltako();
		lightEltako4.setHapId(5);
		lightEltako4.setHapLabel("Sofa");
		lightEltakos.add(lightEltako4);

	}

	/**
	 * Provides the persisted {@link HomekitAuthInfo}. If there was no auth info
	 * before, a new one is initialized.
	 * 
	 * @return the {@link HomekitAuthInfo}
	 * @throws InvalidAlgorithmParameterException
	 *             if the JVM does not contain the necessary encryption
	 *             algorithms, which is needed to generate the private key
	 * @throws IOException
	 *             if file for auth info persistence could not be created.
	 */
	private HomekitAuthInfo createHomekitAuthInfo() throws InvalidAlgorithmParameterException, IOException {
		HomekitAuthInfoImpl authInfo = null;

		if (!AUTH_INFO_FILE.exists()) {
			AUTH_INFO_FILE.createNewFile();
		}

		if (AUTH_INFO_FILE.length() > 0) {
			authInfo = SerializationUtils.deserialize(new FileInputStream(AUTH_INFO_FILE));
		}

		if (authInfo == null) {
			// Create a new one
			String mac = HomekitServer.generateMac();
			BigInteger salt = HomekitServer.generateSalt();
			byte[] privateKey = HomekitServer.generateKey();

			authInfo = new HomekitAuthInfoImpl();
			authInfo.setPin(cfg.getBridgePin());
			authInfo.setMac(mac);
			authInfo.setSalt(salt);
			authInfo.setPrivateKey(privateKey);

			// and serialize it
			SerializationUtils.serialize(authInfo, new FileOutputStream(AUTH_INFO_FILE));

		}

		return authInfo;

	}

	/**
	 * Adds all accessories and starts the bridge (HAP Server)
	 * @throws IOException
	 * @throws InvalidAlgorithmParameterException
	 */
	private void startHomekitBridge() throws IOException, InvalidAlgorithmParameterException {
		HomekitServer homekit = new HomekitServer(cfg.getBridgePort());
		HomekitAuthInfo authInfo = createHomekitAuthInfo();
		HomekitRoot bridge = homekit.createBridge(authInfo, cfg.getBridgeName(), cfg.getBridgeVendor(),
				cfg.getBridgeVersion(), cfg.getBridgeSerialNo());

		for (LightEltako lightEltako : lightEltakos) {
			bridge.addAccessory(lightEltako);
		}

		bridge.start();
	}

	/**
	 * Runs {@link Cucaracha}
	 * @param args
	 */
	public static void main(String[] args) {
		new Cucaracha();
	}

}
