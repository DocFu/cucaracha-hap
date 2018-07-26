package de.plasmawolke.cucaracha.wsqlcplusdmx;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Lightbulb;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;
import de.plasmawolke.cucaracha.model.CucarachaConfig;

public class QlcButtonControl extends BaseControl implements Lightbulb {

	private final static Logger logger = LoggerFactory.getLogger(QlcButtonControl.class);

	private VirtualConsoleButton button = null;
	private CucarachaConfig cfg = null;

	public int getQlcId() {
		return button.getId();
	}

	public QlcButtonControl(CucarachaConfig cfg, CucarachaAccessory cucarachaAccessory, VirtualConsoleButton button) {
		super(cucarachaAccessory);
		this.button = button;
		this.cfg = cfg;
	}

	@Override
	public CompletableFuture<Boolean> getLightbulbPowerState() {
		return CompletableFuture.completedFuture(isInternalPowerState());
	}

	@Override
	public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {

		logger.info("Setting light state for QLC+ Button " + button.getName() + ". New powerState +" + powerState);

		String message1 = button.getId() + "|0";
		String message2 = button.getId() + "|255";

		/// ----

		String destUri = cfg.buildQlcPlusWsUrl();

		WebSocketClient client = new WebSocketClient();
		QlcWebSocket socket = new QlcWebSocket();
		socket.setControl(this);

		socket.setMessage1(message1);
		socket.setMessage2(message2);

		try {
			client.start();

			URI echoUri = new URI(destUri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			logger.info("Connecting to : %s%n", echoUri);

			// wait for closed socket connection.
			socket.awaitClose(1, TimeUnit.SECONDS);
		} catch (Throwable t) {
			logger.error("Could not speak with websocket:", t);
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				logger.error("Could not stop websocket client:", e);
			}
		}

		//// ---

		return null;
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
