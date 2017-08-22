package de.plasmawolke.cucaracha.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * The config
 * 
 * @author Arne Schueler
 */
@XmlRootElement(name = "cucaracha-config")
public class CucarachaConfig {

	private int bridgePort = 9123;
	private String bridgePin = "000-00-000";
	private String bridgeName = "CucarachaDev";
	private String bridgeVendor = "DocFu Inc.";
	private String bridgeVersion = "2017.1";
	private String bridgeSerialNo = "0000";
	private List<CucarachaAccessory> accessories = new ArrayList<>();

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

	/**
	 * @param bridgePort
	 *            the bridgePort to set
	 */
	public final void setBridgePort(int bridgePort) {
		this.bridgePort = bridgePort;
	}

	/**
	 * @param bridgePin
	 *            the bridgePin to set
	 */
	public final void setBridgePin(String bridgePin) {
		this.bridgePin = bridgePin;
	}

	/**
	 * @param bridgeName
	 *            the bridgeName to set
	 */
	public final void setBridgeName(String bridgeName) {
		this.bridgeName = bridgeName;
	}

	/**
	 * @param bridgeVendor
	 *            the bridgeVendor to set
	 */
	public final void setBridgeVendor(String bridgeVendor) {
		this.bridgeVendor = bridgeVendor;
	}

	/**
	 * @param bridgeVersion
	 *            the bridgeVersion to set
	 */
	public final void setBridgeVersion(String bridgeVersion) {
		this.bridgeVersion = bridgeVersion;
	}

	/**
	 * @param bridgeSerialNo
	 *            the bridgeSerialNo to set
	 */
	public final void setBridgeSerialNo(String bridgeSerialNo) {
		this.bridgeSerialNo = bridgeSerialNo;
	}

	/**
	 * @return the accessories
	 */
	public final List<CucarachaAccessory> getAccessories() {
		return accessories;
	}

	/**
	 * @param accessories
	 *            the accessories to set
	 */
	public final void setAccessories(List<CucarachaAccessory> accessories) {
		this.accessories = accessories;
	}

	public String print() {
		return ReflectionToStringBuilder.toString(this);
	}

}
