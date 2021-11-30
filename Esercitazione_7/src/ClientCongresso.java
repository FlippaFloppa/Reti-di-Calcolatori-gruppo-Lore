
/**
 * ClientCongresso.java
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;

class ClientCongresso {

	public static void main(String[] args) {
		final int REGISTRYPORT = 1099;
		String registryHost = null; // host remoto con registry
		String registryRemotoName = "";
		String[] serviceList = null;
		int serviceNum;
		String serviceTag = null;
		String service;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		ServerCongresso serverRMI = null;

		// Controllo dei parametri della riga di comando
		if (args.length != 3) {
			System.out.println("Sintassi: RMI_Registry_IP RegistryRemotoName ServiceTag");
			System.exit(1);
		}
		registryHost = args[0];
		registryRemotoName = args[1];
		serviceTag = args[2];

		System.out.println(
				"Invio richieste a " + registryHost + " per il servizio di tipo " + serviceTag + "\t" + serviceTag);

		// Connessione al servizio RMI remoto
		try {
			String completeRemoteRegistryName = "//" + registryHost + ":" + REGISTRYPORT + "/" + registryRemotoName;
			RegistryRemotoTagClient registryRemoto = (RegistryRemotoTagClient) Naming.lookup(completeRemoteRegistryName);
			serviceList = registryRemoto.cercaTag(serviceTag);

			// Scelta ServiceTag
			do {
				System.out.println("Seleziona il numero del servitore:");
				for (int i = 1; i <= serviceList.length; i++) {
					System.out.println(i + ")\t" + serviceList[i - 1]);
				}

				serviceNum = Integer.parseInt(stdIn.readLine());
				// Controllo servitore
				if (serviceNum > 0 && serviceNum <= serviceList.length)
					serverRMI = (ServerCongresso) registryRemoto.cerca(serviceList[serviceNum]);
				else
					System.out.println("Numero servitore errato!");

			} while (serviceNum <= 0 || serviceNum > serviceList.length);

			/* ciclo accettazione richieste utente */
			while ((service = stdIn.readLine()) != null) {

				if (service.equals("R")) {
					boolean ok = false; // stato [VALID|INVALID] della richiesta
					int g = 0;
					System.out.print("Giornata (1-3)? ");
					while (ok != true) {
						g = Integer.parseInt(stdIn.readLine());
						if (g < 1 || g > 3) {
							System.out.println("Giornata non valida");
							System.out.print("Giornata (1-3)? ");
							continue;
						} else
							ok = true; // gioranta inserita valida
					}
					ok = false;
					String sess = null;
					System.out.print("Sessione (S1 - S12)? ");

					while (ok != true) {
						sess = stdIn.readLine();
						if (!sess.equals("S1") && !sess.equals("S2") && !sess.equals("S3") && !sess.equals("S4")
								&& !sess.equals("S5") && !sess.equals("S6") && !sess.equals("S7") && !sess.equals("S8")
								&& !sess.equals("S9") && !sess.equals("S10") && !sess.equals("S11")
								&& !sess.equals("S12")) {
							System.out.println("Sessione non valida");
							System.out.print("Sessione (S1 - S12)? ");
							continue;
						} else
							ok = true; // Sessione inserita valida
					}

					System.out.print("Speaker? ");
					String speak = stdIn.readLine(); // qualsiasi nome accettato, nessun check

					if (serverRMI.registrazione(g, sess, speak) == 0)
						System.out.println(
								"Registrazione di " + speak + " effettuata per giornata " + g + " sessione " + sess);
					else
						System.out.println("Sessione piena: giornata" + g + " sessione " + sess);
				} // R=Registrazione

				else if (service.equals("P")) {
					int g = 0;
					boolean ok = false;
					System.out.print("Programma giornata (1-3)? ");

					while (ok != true) {
						// TODO: check NumberFormatException
						g = Integer.parseInt(stdIn.readLine());
						if (g < 1 || g > 3) {
							System.out.println("Giornata non valida");
							System.out.print("Programma giornata (1-3)? ");
							continue;
						} else
							ok = true;
					}
					System.out.println("Ecco il programma: ");
					serverRMI.programma(g).stampa();
				} // P=Programma

				else
					System.out.println("Servizio non disponibile");

				System.out.print("Servizio (R=Registrazione, P=Programma del congresso): ");
			} // while (!EOF), fine richieste utente

		} catch (

		NotBoundException nbe) {
			System.err.println("ClientRMI: il nome fornito non risulta registrato; " + nbe.getMessage());
			nbe.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}