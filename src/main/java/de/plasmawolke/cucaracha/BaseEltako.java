package de.plasmawolke.cucaracha;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitCharacteristicChangeCallback;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;

public abstract class BaseEltako extends CucarachaAccessory implements HomekitAccessory {

	// HAP 
	private HomekitCharacteristicChangeCallback powerStateChangeCallback = null;

	// Shared
	private boolean internalPowerState = false;

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public void identify() {
		System.out.println("identify: " + getId() + ":" + getLabel());
	}

	public final void powerStateChanged() {
		if (powerStateChangeCallback != null) {
			powerStateChangeCallback.changed();
		}
	}

	// Getters & Setters

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

}
