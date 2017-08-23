package de.plasmawolke.cucaracha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.SerializationUtils;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitAuthInfo;
import com.beowulfe.hap.HomekitRoot;
import com.beowulfe.hap.HomekitServer;
import com.beowulfe.hap.impl.HomekitBridge;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import de.plasmawolke.cucaracha.model.AccessoryType;
import de.plasmawolke.cucaracha.model.CucarachaAccessory;
import de.plasmawolke.cucaracha.model.CucarachaConfig;

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

	private GpioController gpio = null;

	private CucarachaConfig cfg = null;

	private List<HomekitAccessory> accessories = new ArrayList<>();

	/**
	 * Constructs the Application
	 */
	public Cucaracha() {

		try {

			// Initialize config (bridge config, switches,...)
			init();

			// Wire GPIO
			wire();

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
	 * @throws JAXBException
	 */
	private void init() throws IOException, JAXBException {

		if (!BASE_DIR.exists()) {
			BASE_DIR.mkdirs();
		}

		// try to get config from file
		// if config file exists, try to read and set config
		// else create a new file from a new CucarachaConfig instance
		if (CONFIG_FILE.exists()) {

			Unmarshaller unmarshaller = JAXBContext.newInstance(new Class[] { CucarachaConfig.class })
					.createUnmarshaller();
			cfg = (CucarachaConfig) unmarshaller.unmarshal(CONFIG_FILE);

			System.out.println("Using configiguration [" + CONFIG_FILE + "].");

		} else {
			cfg = new CucarachaConfig();

			CucarachaAccessory sampleAccessory1 = new CucarachaAccessory();
			sampleAccessory1.setType(AccessoryType.OUTLET);
			sampleAccessory1.setHapId(2);
			sampleAccessory1.setHapLabel("My Outlet");
			sampleAccessory1.setGpioPowerStateWriterPin(0);
			sampleAccessory1.setGpioPowerStateReaderPin(1);

			cfg.getAccessories().add(sampleAccessory1);

			CucarachaAccessory sampleAccessory2 = new CucarachaAccessory();
			sampleAccessory2.setType(AccessoryType.LIGHT);
			sampleAccessory2.setHapId(3);
			sampleAccessory2.setHapLabel("My Light");
			sampleAccessory2.setGpioPowerStateWriterPin(2);
			sampleAccessory2.setGpioPowerStateReaderPin(3);

			cfg.getAccessories().add(sampleAccessory2);

			Marshaller marshaller = JAXBContext.newInstance(new Class[] { CucarachaConfig.class }).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(cfg, CONFIG_FILE);

			System.out.println("Created new configuration [" + CONFIG_FILE
					+ "] with example values. Please edit the file according to your needs and restart.");
			System.exit(0);
		}

		System.out.println(cfg.print());
	}

	/**
	 * Wire GPIO to HAP an vice versa by information from config
	 */
	private void wire() {

		boolean runningOnPi = false;

		if (runningOnPi) {
			gpio = GpioFactory.getInstance();
		} else {
			System.out.println("Wrong platform - using GPIO Mock!");
			gpio = new MockGpioController();
		}

		List<CucarachaAccessory> configAccessories = cfg.getAccessories();

		for (CucarachaAccessory cucarachaAccessory : configAccessories) {

			switch (cucarachaAccessory.getType()) {
				case OUTLET:

					OutletEltako outletEltako = new OutletEltako();
					outletEltako.setGpio(gpio);
					outletEltako.setHapId(cucarachaAccessory.getHapId());
					outletEltako.setHapLabel(cucarachaAccessory.getHapLabel());
					outletEltako.setHapManufacturer(cucarachaAccessory.getHapManufacturer());
					outletEltako.setHapModel(cucarachaAccessory.getHapModel());
					outletEltako.setHapSerialNo(cucarachaAccessory.getHapSerialNo());
					outletEltako.wire();
					accessories.add(outletEltako);

					break;

				case LIGHT:

					LightEltako lightEltako = new LightEltako();
					lightEltako.setGpio(gpio);
					lightEltako.setHapId(cucarachaAccessory.getHapId());
					lightEltako.setHapLabel(cucarachaAccessory.getHapLabel());
					lightEltako.setHapManufacturer(cucarachaAccessory.getHapManufacturer());
					lightEltako.setHapModel(cucarachaAccessory.getHapModel());
					lightEltako.setHapSerialNo(cucarachaAccessory.getHapSerialNo());
					lightEltako.wire();
					accessories.add(lightEltako);

					break;

				default:
					break;
			}

		}

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

		for (HomekitAccessory homekitAccessory : accessories) {
			bridge.addAccessory(homekitAccessory);
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
