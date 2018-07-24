package de.plasmawolke.cucaracha.wsqlcplusdmx;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.Lightbulb;

import de.plasmawolke.cucaracha.model.CucarachaAccessory;

public class Switch extends BaseControl implements Lightbulb {

	public Switch(CucarachaAccessory cucarachaAccessory) {
		super(cucarachaAccessory);
	}

	@Override
	public CompletableFuture<Boolean> getLightbulbPowerState() {
		return CompletableFuture.completedFuture(isInternalPowerState());
	}

	@Override
	public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {

		System.out.println("DMX: " + getQlcPlusControlId() + " new powerState +" + powerState);

		String message1 = getQlcPlusControlId() + "|0";
		String message2 = getQlcPlusControlId() + "|255";

		/// ----

		String destUri = "ws://schneestreamchen.local:9999/qlcplusWS";

		WebSocketClient client = new WebSocketClient();
		SimpleEchoSocket socket = new SimpleEchoSocket();
		socket.setControl(this);

		socket.setMessage1(message1);
		socket.setMessage2(message2);

		try {
			client.start();

			URI echoUri = new URI(destUri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			System.out.printf("Connecting to : %s%n", echoUri);

			// wait for closed socket connection.
			socket.awaitClose(1, TimeUnit.SECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
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
