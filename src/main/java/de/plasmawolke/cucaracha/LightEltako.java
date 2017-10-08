package de.plasmawolke.cucaracha;

import java.util.concurrent.CompletableFuture;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Lightbulb;
import com.pi4j.io.gpio.PinState;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;

/**
 * Some kind of latching switch relais
 * @author Arne Schueler
 */
public class LightEltako extends BaseEltako implements Lightbulb {

	public LightEltako(CucarachaAccessory cucarachaAccessory) {
		super(cucarachaAccessory);
	}

	@Override
	public CompletableFuture<Boolean> getLightbulbPowerState() {
		return CompletableFuture.completedFuture(isInternalPowerState());
	}

	@Override
	public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {

		// set internal shared value, which may not always reflect the reality
		setInternalPowerState(powerState);

		try {

			getEltakoOutput().pulse(200, PinState.LOW);
		} catch (Exception e) {
			System.out.println("Could not pulse pin: " + e);
		}

		powerStateChanged();

		System.out.println("setLightbulbPowerState " + getId() + ":" + getLabel() + ":" + (powerState ? "on" : "off"));
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {
		setPowerStateChangeCallback(callback);
	}

	@Override
	public void unsubscribeLightbulbPowerState() {
		setPowerStateChangeCallback(null);
	}

}
