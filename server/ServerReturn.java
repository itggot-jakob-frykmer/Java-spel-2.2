/*
 För varje spelare som ansluter skapas en ny instans utav denna klassen, server är alltså multi-trådad.
 I run(), som körs hela tiden kollar servern om denna klienten som den här tråden hör ihop med har skickat
 ett meddelande till server och hanterar det. Alla meddelanden som börjar med '#' ska direkt skickas vidare
 till alla andra klienter då de också ska ta del utav denna informationen. 


 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerReturn implements Runnable {

	public Socket SOCK;
	private Scanner INPUT;
	private PrintWriter OUT;
	String MESSAGE = "";
	int playerNumber = 0;
	int delay = 0;

	public ServerReturn(Socket X, int playersOnline) {

		this.playerNumber = playersOnline; // globlat spelarnummer
		this.SOCK = X;

		try {
			INPUT = new Scanner(SOCK.getInputStream());
			OUT = new PrintWriter(SOCK.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// När klienten förlorar anslutning körs detta
	public void clientDisconnect() {
		Server.disconnectedPlayers.add(playerNumber); // lägger till denna spelaren i listan över spelare som förlorat sin anslutning

		// hittar vilket index denna spelaren har i listorna och tar bort spelaren
		int index = Server.SRS.indexOf(this);

		Server.SRS.remove(index);

		Server.updateActivePlayers(); // servern uppdaterar aktiva spelare
		Server.sendPlayerList(); // skickar den uppdaterade listan över spelare till alla klienter
		Server.updateTextAreas(); // uppdaterar serverFrames informationsfält

	}

	@Override
	public void run() {
		try {
			try {

				while (true) {

					if (!INPUT.hasNext()) {
						return;
					}
					MESSAGE = INPUT.nextLine();
					// alla meddelanden som börjar med '#' ska direkt skickas ut till alla klienter
					if (MESSAGE.startsWith("#")) {
						// loopar igenom alla anslutningar till alla andra klienter för att skicka meddelande till dem
						for (int i = 0; i < Server.SRS.size(); i++) {
							// kollar så listan har hunnits skapats
							if (Server.SRS.get(i).getSocket() != null) {
								// kollar så att den nuvarande anslutningen inte är till klienten som hör ihop med denna instansen
								// utav ServerReturn för det är onödigt att klienten som skickade meddelandet ska få tillbaka det igen
								if (Server.SRS.get(i).getSocket() != SOCK) {
									Socket TEMP_SOCK = Server.SRS.get(i).getSocket();
									PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());

									String temp = MESSAGE.substring(1); // tar bort #
									TEMP_OUT.println(temp);
									TEMP_OUT.flush();

								}
							}
						}

						if (MESSAGE.startsWith("#SNDTREECUTDOWN")) {
							ServerMap.cutTreeFromClient(MESSAGE);
						}
						
						if (MESSAGE.startsWith("#SNDCLIENTREMOVEWORLDOBJECT")) {
							ServerMap.removeObjectFromClient(MESSAGE);
						}

					}

					if (OUT.checkError()) {
						// clientDisconnect();
					}
				}
			} finally {
				clientDisconnect();
				// Server.clientDisconnectedInfo(playerNumber);
				SOCK.close();

			}
		} catch (Exception X) {
			// clientDisconnect();
			System.out.println("ERROR 1");
			System.out.print(X);
			X.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

	public Socket getSocket() {
		return SOCK;
	}

	public int getDelay() {
		return delay;
	}

}
