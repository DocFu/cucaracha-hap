package de.plasmawolke.cucaracha;

import java.util.concurrent.CompletableFuture;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Outlet;

/**
 * Some kind of latching switch relais
 * @author Arne Schueler
 */
public class OutletEltako extends BaseEltako implements Outlet {

	/**
	 * Event Handler Method for GPIO Listener on Pin
	 * getGpioPowerStateReaderPin();
	 * @throws Exception
	 */
	public void onGpioInputChange() throws Exception {
		setInternalPowerState(true); // or false
		powerStateChanged();
	}

	/**
	 * Called by HAP API implementation
	 */
	@Override
	public CompletableFuture<Void> setPowerState(boolean state) throws Exception {
		setInternalPowerState(state);
		// set internal shared value, which may not always reflect the reality

		int pinNumber = getGpioPowerStateWriterPin();

		if (state == true) {
			// set pinNumber to high
		} else {
			// set pinNumber to low
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
