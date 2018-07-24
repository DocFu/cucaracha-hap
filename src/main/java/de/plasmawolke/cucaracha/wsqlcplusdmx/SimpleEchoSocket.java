package de.plasmawolke.cucaracha.wsqlcplusdmx;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Basic Echo Client Socket
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleEchoSocket {

	private String message1 = null;
	private String message2 = null;

	private String responseMessage = null;

	private BaseControl control;

	private final CountDownLatch closeLatch;
	@SuppressWarnings("unused")
	private Session session;

	public SimpleEchoSocket() {
		this.closeLatch = new CountDownLatch(1);
	}

	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
		this.session = null;
		this.closeLatch.countDown(); // trigger latch
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Got connect: %s%n", session);
		this.session = session;
		try {
			Future<Void> fut;
			fut = session.getRemote().sendStringByFuture(message1);
			fut.get(1, TimeUnit.SECONDS); // wait for send to complete.

			fut = session.getRemote().sendStringByFuture(message2);
			fut.get(1, TimeUnit.SECONDS); // wait for send to complete.

			//session.close(StatusCode.NORMAL, "I'm done");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * @return the message1
	 */
	public final String getMessage1() {
		return message1;
	}

	/**
	 * @param message1
	 *            the message1 to set
	 */
	public final void setMessage1(String message1) {
		this.message1 = message1;
	}

	/**
	 * @return the message2
	 */
	public final String getMessage2() {
		return message2;
	}

	/**
	 * @param message2
	 *            the message2 to set
	 */
	public final void setMessage2(String message2) {
		this.message2 = message2;
	}

	@OnWebSocketMessage
	public void onMessage(String message) {

		System.out.println("Got message '" + message + "'");

		int id = 38;
		String name = "BUTTON";
		int value = 255;

		String[] parts = StringUtils.split(message, "|");

		id = Integer.parseInt(parts[0]);
		name = parts[1];
		value = Integer.parseInt(parts[2]);

		System.out.println("Parsed message;");
		System.out.println("id=" + id);
		System.out.println("name=" + name);
		System.out.println("value=" + value);

		//		List<String> parts = new ArrayList<>();
		//
		//		int old_i = 0;
		//
		//		for (int i = 0; i < messageB.length; i++) {
		//			System.out.println(messageB[i]);
		//			if (124 == messageB[i]) {
		//
		//				id = message.substring(0, i);
		//				old_i = i + 1;
		//
		//				break;
		//			}
		//
		//		}
		//
		//		System.out.println("old_i=" + old_i);
		//
		//		for (int i = old_i; i < messageB.length; i++) {
		//			System.out.println(messageB[i]);
		//
		//			if (124 == messageB[i]) {
		//
		//				value = message.substring(i + 1, messageB.length);
		//
		//				System.out.println(id + "=" + value);
		//
		//				break;
		//			}
		//
		//		}
		//
		//		System.out.println(parts);
		//
		// ---------------

		synchronized (control) {

			// 0|BUTTON|255

			if (id == control.getQlcPlusControlId()) {

				if (value == 255) {
					control.setInternalPowerState(true);
					System.out.println("Setting on " + id);
				} else {
					control.setInternalPowerState(false);
					System.out.println("Setting off " + id);
				}

				control.getPowerStateChangeCallback().changed();

			}

		}

	}

	/**
	 * @return the responseMessage
	 */
	public final String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * @param responseMessage
	 *            the responseMessage to set
	 */
	public final void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	/**
	 * @return the control
	 */
	public final BaseControl getControl() {
		return control;
	}

	/**
	 * @param control
	 *            the control to set
	 */
	public final void setControl(BaseControl control) {
		this.control = control;
	}

}
