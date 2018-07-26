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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic Echo Client Socket
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class QlcWebSocket {

	private final static Logger logger = LoggerFactory.getLogger(QlcWebSocket.class);

	private String message1 = null;
	private String message2 = null;

	private String responseMessage = null;

	private QlcButtonControl control = null;

	private final CountDownLatch closeLatch;
	@SuppressWarnings("unused")
	private Session session;

	public QlcWebSocket() {
		this.closeLatch = new CountDownLatch(1);
	}

	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		logger.info("Connection closed: %d - %s%n", statusCode, reason);

		this.session = null;
		this.closeLatch.countDown(); // trigger latch
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.info("Got connect: %s%n", session);

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

		logger.info("Got message '" + message + "'");

		int id = 38;
		String name = "BUTTON";
		int value = 255;

		String[] parts = StringUtils.split(message, "|");

		id = Integer.parseInt(parts[0]);
		name = parts[1];
		value = Integer.parseInt(parts[2]);

		logger.info("Parsed message;");
		logger.info("id=" + id);
		logger.info("name=" + name);
		logger.info("value=" + value);

		synchronized (control) {

			// 0|BUTTON|255

			if (id == control.getQlcId()) {

				if (value == 255) {
					control.setInternalPowerState(true);
					logger.info("Setting on " + id);
				} else {
					control.setInternalPowerState(false);
					logger.info("Setting off " + id);
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
	public final void setControl(QlcButtonControl control) {
		this.control = control;
	}

}
