package de.plasmawolke.cucaracha;

import java.util.concurrent.CompletableFuture;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Outlet;

/**
 * Some kind of latching switch relais
 * @author Arne Schueler
 */
public class OutletEltako implements Outlet {

	// HAP 
	private HomekitCharacteristicChangeCallback subscribeCallback = null;

	//HAP Identity
	private int hapId = 2;
	private String hapLabel = "My Outlet";
	private String hapSerialNo = "not applicable";
	private String hapModel = "not applicable";
	private String hapManufacturer = "not applicable";

	// Other
	private boolean powerState = false;

	private boolean stateless = false;

	@Override
	public void subscribePowerState(HomekitCharacteristicChangeCallback callback) {
		this.subscribeCallback = callback;

	}

	@Override
	public void unsubscribePowerState() {
		this.subscribeCallback = null;

	}

	@Override
	public CompletableFuture<Boolean> getPowerState() {
		return CompletableFuture.completedFuture(powerState);
	}

	public CompletableFuture<Void> setPowerState(boolean state) throws Exception {
		this.powerState = state;
		System.out.println("setPowerState " + hapId + ":" + hapLabel + ":" + (powerState ? "on" : "off"));
		return CompletableFuture.completedFuture(null);
	}

	// outlet in use

	@Override
	public CompletableFuture<Boolean> getOutletInUse() {
		return CompletableFuture.completedFuture(false);
	}

	@Override
	public void subscribeOutletInUse(HomekitCharacteristicChangeCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unsubscribeOutletInUse() {
		// TODO
	}

	// Getters & Setters

	@Override
	public int getId() {
		return hapId;
	}

	@Override
	public String getLabel() {
		return hapLabel;
	}

	@Override
	public void identify() {
		System.out.println("identify " + hapId + ":" + hapLabel);
	}

	@Override
	public String getSerialNumber() {
		return hapSerialNo;
	}

	@Override
	public String getModel() {
		return hapModel;
	}

	@Override
	public String getManufacturer() {
		return hapManufacturer;
	}

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
	 * @return the powerState
	 */
	public final boolean isPowerState() {
		return powerState;
	}

	/**
	 * @return the stateless
	 */
	public final boolean isStateless() {
		return stateless;
	}

	/**
	 * @param stateless
	 *            the stateless to set
	 */
	public final void setStateless(boolean stateless) {
		this.stateless = stateless;
	}

}
