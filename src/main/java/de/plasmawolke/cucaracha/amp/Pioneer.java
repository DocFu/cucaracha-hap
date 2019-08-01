package de.plasmawolke.cucaracha.amp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pioneer {

	private final static Logger logger = LoggerFactory.getLogger(Pioneer.class);

	public static void main(String[] args) {
		kino();
	}

	public static void kino() {
		Pioneer p = new Pioneer();		
		//p.execute("SLI10");
		//p.execute("MVL77");
		p.execute("TSWB00");
		

	}

	private void execute(String command) {

		logger.info("Executing command %s ...", command);

		command = prepareCommand(command);

		String hostname = "10.15.1.64";
		int port = 60128;

		// declaration section:
		// clientSocket: our client socket
		// os: output stream
		// is: input stream

		Socket clientSocket = null;
		DataOutputStream os = null;
		BufferedReader is = null;

		// Initialization section:
		// Try to open a socket on the given port
		// Try to open input and output streams

		try {
			clientSocket = new Socket(hostname, port);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + hostname);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: " + hostname);
		}

		// If everything has been initialized then we want to write some data
		// to the socket we have opened a connection to on the given port

		if (clientSocket == null || os == null || is == null) {
			System.err.println("Something is wrong. One variable is null.");
			return;
		}

		try {

			//				System.out.print("Enter an integer (0 to stop connection, -1 to stop server): ");
			//				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			//				String keyboardInput = br.readLine();
			os.writeBytes(command);

			String responseLine = is.readLine();
			System.out.println("Pioneer:");
			System.out.println(responseLine);

			// clean up:
			// close the output stream
			// close the input stream
			// close the socket

			os.close();
			is.close();
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}

	}

	/**
	 * ISCP\x00\x00\x00\x10\x00\x00\x00\x08\x01\x00\x00\x00\x211SLI10\x0D\x0A
	 */
	private String prepareCommand(String command) {

		int commandLength = command.length();
		commandLength = commandLength + 3;

		byte lengthByte = (byte) commandLength;
		System.out.println(lengthByte);

		byte[] ba1 = new byte[] { 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, lengthByte, 0x01, 0x00, 0x00, 0x00, 0x21 };
		byte[] ba2 = new byte[] { 0x0D, 0x0A };

		String s = "ISCP" + new String(ba1) + "1" + command + new String(ba2);

		//String commandLengthHex = ("00" + commandLength).substring(commandLength+"".length());

		return s;
	}

}
