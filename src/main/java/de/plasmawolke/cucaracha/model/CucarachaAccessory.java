package de.plasmawolke.cucaracha.model;

public class CucarachaAccessory {

	private AccessoryType type = AccessoryType.OUTLET;

	//HAP identity attributes
	private int hapId = -1;
	private String hapLabel = "undefined";
	private String hapSerialNo = "undefined";
	private String hapModel = "undefined";
	private String hapManufacturer = "undefined";

	// GPIO wiring attributes - http://pi4j.com/pin-numbering-scheme.html
	private int gpioPowerStateWriterPin = -1;
	private int gpioPowerStateReaderPin = -1; // kind of optional

	// ws-qlcplus-dmx control id
	private int qlcPlusControlId = -1;

	/**
	 * @return the hapId
	 */
	public final int getHapId() {
		return hapId;
	}

	/**
	 * @param hapId
	 *            the hapId to set
	 */
	public final void setHapId(int hapId) {
		this.hapId = hapId;
	}

	/**
	 * @return the hapLabel
	 */
	public final String getHapLabel() {
		return hapLabel;
	}

	/**
	 * @param hapLabel
	 *            the hapLabel to set
	 */
	public final void setHapLabel(String hapLabel) {
		this.hapLabel = hapLabel;
	}

	/**
	 * @return the hapSerialNo
	 */
	public final String getHapSerialNo() {
		return hapSerialNo;
	}

	/**
	 * @param hapSerialNo
	 *            the hapSerialNo to set
	 */
	public final void setHapSerialNo(String hapSerialNo) {
		this.hapSerialNo = hapSerialNo;
	}

	/**
	 * @return the hapModel
	 */
	public final String getHapModel() {
		return hapModel;
	}

	/**
	 * @param hapModel
	 *            the hapModel to set
	 */
	public final void setHapModel(String hapModel) {
		this.hapModel = hapModel;
	}

	/**
	 * @return the hapManufacturer
	 */
	public final String getHapManufacturer() {
		return hapManufacturer;
	}

	/**
	 * @param hapManufacturer
	 *            the hapManufacturer to set
	 */
	public final void setHapManufacturer(String hapManufacturer) {
		this.hapManufacturer = hapManufacturer;
	}

	/**
	 * @return the type
	 */
	public final AccessoryType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public final void setType(AccessoryType type) {
		this.type = type;
	}

	/**
	 * @return the gpioPowerStateWriterPin
	 */
	public final int getGpioPowerStateWriterPin() {
		return gpioPowerStateWriterPin;
	}

	/**
	 * @param gpioPowerStateWriterPin
	 *            the gpioPowerStateWriterPin to set
	 */
	public final void setGpioPowerStateWriterPin(int gpioPowerStateWriterPin) {
		this.gpioPowerStateWriterPin = gpioPowerStateWriterPin;
	}

	/**
	 * @return the gpioPowerStateReaderPin
	 */
	public final int getGpioPowerStateReaderPin() {
		return gpioPowerStateReaderPin;
	}

	/**
	 * @param gpioPowerStateReaderPin
	 *            the gpioPowerStateReaderPin to set
	 */
	public final void setGpioPowerStateReaderPin(int gpioPowerStateReaderPin) {
		this.gpioPowerStateReaderPin = gpioPowerStateReaderPin;
	}

	/**
	 * @return the qlcPlusControlId
	 */
	public final int getQlcPlusControlId() {
		return qlcPlusControlId;
	}

	/**
	 * @param qlcPlusControlId
	 *            the qlcPlusControlId to set
	 */
	public final void setQlcPlusControlId(int qlcPlusControlId) {
		this.qlcPlusControlId = qlcPlusControlId;
	}

}
