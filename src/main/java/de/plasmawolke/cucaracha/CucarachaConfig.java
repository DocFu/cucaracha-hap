package de.plasmawolke.cucaracha;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The config
 * 
 * @author Arne Schueler
 */
@XmlRootElement
public class CucarachaConfig {

	private final int bridgePort = 9123;
	private final String bridgePin = "000-00-000";
	private final String bridgeName = "CucarachaDev";
	private final String bridgeVendor = "DocFu Inc.";
	private final String bridgeVersion = "2017.1";
	private final String bridgeSerialNo = "0000";

	/**
	 * @return the bridgePort
	 */
	public final int getBridgePort() {
		return bridgePort;
	}

	/**
	 * @return the bridgePin
	 */
	public final String getBridgePin() {
		return bridgePin;
	}

	/**
	 * @return the bridgeName
	 */
	public final String getBridgeName() {
		return bridgeName;
	}

	/**
	 * @return the bridgeVendor
	 */
	public final String getBridgeVendor() {
		return bridgeVendor;
	}

	/**
	 * @return the bridgeVersion
	 */
	public final String getBridgeVersion() {
		return bridgeVersion;
	}

	/**
	 * @return the bridgeSerialNo
	 */
	public final String getBridgeSerialNo() {
		return bridgeSerialNo;
	}

}
