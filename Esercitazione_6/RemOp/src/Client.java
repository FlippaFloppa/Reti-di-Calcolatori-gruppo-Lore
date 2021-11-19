import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Optional;

public class Client {
	public static void main(String[] args) {
		final int REGISTRYPORT = 1099;
		String registryHost = null; // host remoto con registry
		String serviceName = "";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if (args.length != 2) {
			System.out.println("Sintassi: RMI_Registry_IP ServiceName");
			System.exit(1);
		}
		registryHost = args[0];
		serviceName = args[1];

		System.out.println("Invio richieste a " + registryHost + " per il servizio di nome " + serviceName);

		// Connessione al servizio RMI remoto
		try {
			String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
			RemOp serverRMI = (RemOp) Naming.lookup(completeName);
			System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

			System.out.println("\nRichieste di servizio fino a fine file");

			String service;
			System.out.print("Servizio (Count=conta linee, Cancel=cancella linea): ");
			String fileName;
			/* ciclo accettazione richieste utente */
			while ((service = stdIn.readLine()) != null) {

				if (service.equals("Count")) {
					int max = 0;
					System.out.print("Nome file? ");
					fileName=stdIn.readLine();
					System.out.println("Numero massimo parole");
					try{
						max= Integer.parseInt(stdIn.readLine());
					}
					catch(NumberFormatException e) {
						System.out.println("Il numero di parole deve essere un intero");
						continue;
					}
					
					System.out.println("Righe con pi� di "+ max + "parole= " + serverRMI.conta_righe(fileName, max));
				} // Count=conta linee

				else if (service.equals("Cancel")) {
					int line= 0;
					Optional<Risposta> r;
					System.out.print("Nome file? ");
					fileName=stdIn.readLine();
					System.out.println("Numero massimo parole");
					try{
						line= Integer.parseInt(stdIn.readLine());
					}
					catch(NumberFormatException e) {
						System.out.println("Il numero di parole deve essere un intero");
						continue;
					}
					if (!(r=serverRMI.elimina_riga(fileName, line)).isEmpty())
						r.get().toString();
					else 
						System.out.println("Numero riga maggiore del massimo o file inesistente");

				} //

				else
					System.out.println("Servizio non disponibile");

				System.out.print("Servizio (R=Registrazione, P=Programma del congresso): ");
			} // while (!EOF), fine richieste utente

		} catch (NotBoundException nbe) {
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
