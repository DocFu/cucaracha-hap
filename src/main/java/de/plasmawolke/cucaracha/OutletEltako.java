package de.plasmawolke.cucaracha;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Outlet;
import com.pi4j.io.gpio.PinState;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;

/**
 * Some kind of latching switch relais
 * @author Arne Schueler
 */
public class OutletEltako extends BaseEltako implements Outlet {

	private final static Logger logger = LoggerFactory.getLogger(OutletEltako.class);

	public OutletEltako(CucarachaAccessory cucarachaAccessory) {
		super(cucarachaAccessory);
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public CompletableFuture<Void> setPowerState(boolean state) throws Exception {

		// set internal shared value, which may not always reflect the reality
		setInternalPowerState(state);

		try {
			logger.info("Pulsing PIN " + getOutputPin() + " Output: " + getEltakoInput());
			getEltakoOutput().pulse(200, PinState.LOW);
		} catch (Exception e) {
			System.out.println("Could not pulse pin: " + e);
		}

		powerStateChanged();

		System.out.println("setPowerState " + getPowerState() + ":" + getHapLabel() + ":"
				+ (isInternalPowerState() ? "on" : "off"));

		return CompletableFuture.completedFuture(null);
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public CompletableFuture<Boolean> getPowerState() {
		return CompletableFuture.completedFuture(isInternalPowerState());
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public void subscribePowerState(HomekitCharacteristicChangeCallback callback) {
		setPowerStateChangeCallback(callback);

	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public void unsubscribePowerState() {
		setPowerStateChangeCallback(null);
	}

	/**
	 * Called by HAP API implementation
	 * Always true, as hardware behind this does not support this feature
	 */
	@Override
	public CompletableFuture<Boolean> getOutletInUse() {
		return CompletableFuture.completedFuture(true);
	}

	/**
	 * Called by HAP API implementation
	 * Nothing to do, as hardware behind this does not support this feature
	 */
	@Override
	public void subscribeOutletInUse(HomekitCharacteristicChangeCallback callback) {
		// noop
	}

	/**
	 * Called by HAP API implementation
	 * Nothing to do, as hardware behind does this not support this feature
	 */
	@Override
	public void unsubscribeOutletInUse() {
		// noop
	}

}
