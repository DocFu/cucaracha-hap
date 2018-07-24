package de.plasmawolke.cucaracha.wsqlcplusdmx;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitCharacteristicChangeCallback;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;

public abstract class BaseControl extends CucarachaAccessory implements HomekitAccessory {

	// HAP 
	private HomekitCharacteristicChangeCallback powerStateChangeCallback = null;

	// Shared
	private boolean internalPowerState = false;

	public BaseControl(CucarachaAccessory cucarachaAccessory) {

		setQlcPlusControlId(cucarachaAccessory.getQlcPlusControlId());

		setHapId(cucarachaAccessory.getHapId());
		setHapLabel(cucarachaAccessory.getHapLabel());
		setHapManufacturer(cucarachaAccessory.getHapManufacturer());
		setHapModel(cucarachaAccessory.getHapModel());
		setHapSerialNo(cucarachaAccessory.getHapSerialNo());
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public final int getId() {
		return getHapId();
	}

	/**
	 * @return the powerState
	 */
	public final boolean isInternalPowerState() {
		return internalPowerState;
	}

	/**
	 * @param powerState
	 *            the powerState to set
	 */
	public final void setInternalPowerState(boolean powerState) {
		this.internalPowerState = powerState;
	}

	/**
	 * @return the powerStateChangeCallback
	 */
	public final HomekitCharacteristicChangeCallback getPowerStateChangeCallback() {
		return powerStateChangeCallback;
	}

	/**
	 * @param powerStateChangeCallback
	 *            the powerStateChangeCallback to set
	 */
	public final void setPowerStateChangeCallback(HomekitCharacteristicChangeCallback powerStateChangeCallback) {
		this.powerStateChangeCallback = powerStateChangeCallback;
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public final String getLabel() {
		return getHapLabel();
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public final String getSerialNumber() {
		return getHapSerialNo();
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public final String getModel() {
		return getHapModel();
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public final String getManufacturer() {
		return getHapManufacturer();
	}

	@Override
	public void identify() {
		// TODO Auto-generated method stub

	}

}
