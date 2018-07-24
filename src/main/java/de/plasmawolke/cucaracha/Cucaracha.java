package de.plasmawolke.cucaracha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitAuthInfo;
import com.beowulfe.hap.HomekitRoot;
import com.beowulfe.hap.HomekitServer;
import com.beowulfe.hap.impl.HomekitBridge;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import de.plasmawolke.cucaracha.gpio.LightEltako;
import de.plasmawolke.cucaracha.gpio.MockGpioController;
import de.plasmawolke.cucaracha.gpio.OutletEltako;
import de.plasmawolke.cucaracha.model.AccessoryType;
import de.plasmawolke.cucaracha.model.CucarachaAccessory;
import de.plasmawolke.cucaracha.model.CucarachaConfig;
import de.plasmawolke.cucaracha.wsqlcplusdmx.ButtonCollector;
import de.plasmawolke.cucaracha.wsqlcplusdmx.VirtualConsoleButton;

/**
 * Cucaracha is a {@link HomekitBridge} running on a Raspberry PI wired with
 * PI4J to control GPIOs.
 * 
 * @author Arne Schueler
 */
public class Cucaracha {

	private final static Logger logger = LoggerFactory.getLogger(Cucaracha.class);

	public static final File BASE_DIR = new File(System.getProperty("user.home") + "/.kw30-hap-server/");
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

			wireDmx();

			// Start HAP stuff
			startHomekitBridge();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void wireDmx() {

		ButtonCollector bc = new ButtonCollector();
		try {
			bc.populate();
		} catch (Exception e) {
			logger.error("Could not populate buttons: ", e);
			return;
		}

		List<VirtualConsoleButton> buttons = bc.getButtons();

		if (buttons.isEmpty()) {
			logger.info("No QLC+ buttons found!");
		}

		int hapId = 100;
		for (VirtualConsoleButton button : buttons) {

			CucarachaAccessory cucarachaAccessory = new CucarachaAccessory();
			cucarachaAccessory.setHapId(hapId);
			cucarachaAccessory.setQlcPlusControlId(button.getId());
			cucarachaAccessory.setHapLabel(button.getName());
			cucarachaAccessory.setHapModel("HomeKit-QLC+ Bridge Accessory");

			de.plasmawolke.cucaracha.wsqlcplusdmx.Switch dmxSwitch = new de.plasmawolke.cucaracha.wsqlcplusdmx.Switch(
					cucarachaAccessory);

			dmxSwitch.setInternalPowerState(button.isEnabled());

			accessories.add(dmxSwitch);

			hapId++;

		}

	}

	/**
	 * Sets up configuration
	 * @throws IOException
	 *             if files could not be created
	 * @throws JAXBException
	 */
	private void init() throws IOException, JAXBException {

		logger.info("InetAdress: " + InetAddress.getLocalHost());

		logger.info("Intializing configuration...");

		if (!BASE_DIR.exists()) {
			BASE_DIR.mkdirs();
			logger.debug("Creating base directory " + BASE_DIR);
		}

		// try to get config from file
		// if config file exists, try to read and set config
		// else create a new file from a new CucarachaConfig instance
		if (CONFIG_FILE.exists()) {
			logger.info("Loading existing config file " + CONFIG_FILE);
			Unmarshaller unmarshaller = JAXBContext.newInstance(new Class[] { CucarachaConfig.class })
					.createUnmarshaller();
			cfg = (CucarachaConfig) unmarshaller.unmarshal(CONFIG_FILE);

			logger.info("Loaded config file succesfully.");
		} else {
			logger.info("Creating new a config file " + CONFIG_FILE);
			cfg = new CucarachaConfig();

			CucarachaAccessory sampleAccessory1 = new CucarachaAccessory();
			sampleAccessory1.setType(AccessoryType.LIGHT);
			sampleAccessory1.setHapId(2);
			sampleAccessory1.setHapLabel("Baulicht");
			sampleAccessory1.setQlcPlusControlId(0);
			//sampleAccessory1.setGpioPowerStateWriterPin(0);
			//sampleAccessory1.setGpioPowerStateReaderPin(1);
			cfg.getAccessories().add(sampleAccessory1);

			CucarachaAccessory sampleAccessory2 = new CucarachaAccessory();
			sampleAccessory2.setType(AccessoryType.LIGHT);
			sampleAccessory2.setHapId(3);
			sampleAccessory2.setHapLabel("Rote Stimmung");
			//sampleAccessory2.setGpioPowerStateWriterPin(2);
			//sampleAccessory2.setGpioPowerStateReaderPin(3);
			sampleAccessory2.setQlcPlusControlId(0);
			cfg.getAccessories().add(sampleAccessory2);

			CucarachaAccessory sampleAccessory3 = new CucarachaAccessory();
			sampleAccessory3.setType(AccessoryType.LIGHT);
			sampleAccessory3.setHapId(4);
			sampleAccessory3.setHapLabel("Grüne Stimmung");
			//sampleAccessory2.setGpioPowerStateWriterPin(2);
			//sampleAccessory2.setGpioPowerStateReaderPin(3);
			sampleAccessory3.setQlcPlusControlId(0);
			cfg.getAccessories().add(sampleAccessory3);

			Marshaller marshaller = JAXBContext.newInstance(new Class[] { CucarachaConfig.class }).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(cfg, CONFIG_FILE);

			logger.warn("Created new configuration [" + CONFIG_FILE
					+ "] with example values. Please edit the file according to your needs and restart!");
			System.exit(0);
		}

		logger.info("====== CONFIG BEGIN======");
		logger.info(cfg.print());
		logger.info("====== CONFIG END======");

		logger.info("Intializing configuration done!");
	}

	/**
	 * Wire GPIO to HAP an vice versa by information from config
	 */
	private void wire() {
		logger.info("Wiring things...");

		boolean runningOnPi = System.getProperty("os.arch").startsWith("arm");

		if (runningOnPi) {
			gpio = GpioFactory.getInstance();
		} else {
			logger.warn("Wrong platform detected! Using GPIO Mock. Expect some Errors (NPEs)...");
			gpio = new MockGpioController();
		}

		List<CucarachaAccessory> configAccessories = cfg.getAccessories();
		logger.info(configAccessories.size() + " accesories will be wired...");

		for (CucarachaAccessory cucarachaAccessory : configAccessories) {
			logger.debug("Preparing accessory " + cucarachaAccessory.getHapId() + "...");

			if (cucarachaAccessory.getGpioPowerStateReaderPin() == -1
					&& cucarachaAccessory.getGpioPowerStateWriterPin() == -1) {
				continue;
			}

			switch (cucarachaAccessory.getType()) {
				case OUTLET:

					OutletEltako outletEltako = new OutletEltako(cucarachaAccessory);
					outletEltako.wire(gpio);
					accessories.add(outletEltako);

					break;

				case LIGHT:

					LightEltako lightEltako = new LightEltako(cucarachaAccessory);
					lightEltako.wire(gpio);
					accessories.add(lightEltako);

					break;

				default:
					break;
			}

			logger.debug("Ok");

		}

		logger.info("Wiring things done!");

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
		logger.info("Creating HomekitAuthInfo...");
		HomekitAuthInfoImpl authInfo = null;

		if (!AUTH_INFO_FILE.exists()) {
			AUTH_INFO_FILE.createNewFile();
			logger.info("Created new HomekitAuthInfo File " + AUTH_INFO_FILE);
		}

		if (AUTH_INFO_FILE.length() > 0) {
			authInfo = SerializationUtils.deserialize(new FileInputStream(AUTH_INFO_FILE));
			logger.info("Restored HomekitAuthInfo from File " + AUTH_INFO_FILE);
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
			logger.info("Created an persisted new HomekitAuthInfo.");

		}
		logger.info("Mac: " + authInfo.getMac());
		logger.info("Salt: " + authInfo.getSalt());
		logger.info("PIN: " + authInfo.getPin());
		logger.info("PK: " + authInfo.getPrivateKey());
		return authInfo;

	}

	/**
	 * Adds all accessories and starts the bridge (HAP Server)
	 * @throws IOException
	 * @throws InvalidAlgorithmParameterException
	 */
	private void startHomekitBridge() throws IOException, InvalidAlgorithmParameterException {
		logger.info("Starting Homekit Bridge...");
		HomekitServer homekit = new HomekitServer(InetAddress.getByName(cfg.getBridgeHost()), cfg.getBridgePort());
		HomekitAuthInfo authInfo = createHomekitAuthInfo();
		HomekitRoot bridge = homekit.createBridge(authInfo, cfg.getBridgeName(), cfg.getBridgeVendor(),
				cfg.getBridgeVersion(), cfg.getBridgeSerialNo());

		for (HomekitAccessory homekitAccessory : accessories) {
			bridge.addAccessory(homekitAccessory);
		}
		bridge.start();
		logger.info("Starting Homekit Bridge done!");

		printPinBox();
	}

	/**
	 * Nice try ;-)
	 */
	private void printPinBox() {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("                ┌────────────┐");
		System.out.println("                │ " + cfg.getBridgePin() + " │");
		System.out.println("                └────────────┘");
		System.out.println();
		System.out.println();
		System.out.println();
	}

	/**
	 * Runs {@link Cucaracha}
	 * @param args
	 */
	public static void main(String[] args) {
		new Cucaracha();
	}

}
