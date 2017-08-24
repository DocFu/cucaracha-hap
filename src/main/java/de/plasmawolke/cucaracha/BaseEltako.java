package de.plasmawolke.cucaracha;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;

public abstract class BaseEltako extends CucarachaAccessory implements HomekitAccessory {

	// HAP 
	private HomekitCharacteristicChangeCallback powerStateChangeCallback = null;

	// GPIO
	private GpioController gpio;
	private GpioPinDigitalOutput eltakoOutput;
	private GpioPinDigitalInput eltakoInput;

	// Shared
	private boolean internalPowerState = false;

	/**
	 * @param gpio2
	 * 
	 */
	public void wire(final GpioController gpio) {
		this.gpio = gpio;

		if (getGpioPowerStateWriterPin() != -1) {
			eltakoOutput = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(getGpioPowerStateWriterPin()),
					getHapLabel(), PinState.LOW);

		}

		if (getGpioPowerStateReaderPin() != -1) {
			eltakoInput = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(getGpioPowerStateReaderPin()),
					getHapLabel() + "State", PinPullResistance.PULL_DOWN);

			eltakoInput.addListener(new GpioPinListenerDigital() {

				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

					// display pin state on console
					System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());

					if (event.getState().isHigh()) {
						internalPowerState = true;
					} else {
						internalPowerState = false;
					}

					powerStateChanged();

				}
			});
		}

	}

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

	/**
	 * @return the eltakoOutput
	 */
	public final GpioPinDigitalOutput getEltakoOutput() {
		return eltakoOutput;
	}

	/**
	 * @param eltakoOutput
	 *            the eltakoOutput to set
	 */
	public final void setEltakoOutput(GpioPinDigitalOutput eltakoOutput) {
		this.eltakoOutput = eltakoOutput;
	}

	/**
	 * @return the eltakoInput
	 */
	public final GpioPinDigitalInput getEltakoInput() {
		return eltakoInput;
	}

	/**
	 * @param eltakoInput
	 *            the eltakoInput to set
	 */
	public final void setEltakoInput(GpioPinDigitalInput eltakoInput) {
		this.eltakoInput = eltakoInput;
	}

}
