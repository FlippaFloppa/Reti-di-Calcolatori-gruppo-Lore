
/**
 * ServerCongressoImpl.java
 * 		Implementazione del server
 * */

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerCongressoImpl extends UnicastRemoteObject implements ServerCongresso {
	static Programma prog[];

	// Costruttore
	public ServerCongressoImpl() throws RemoteException {
		super();
	}

	// Richiede una prenotazione
	public int registrazione(int giorno, String sessione, String speaker) throws RemoteException {
		int numSess = -1;
		System.out.println("Server RMI: richiesta registrazione con parametri");
		System.out.println("giorno   = " + giorno);
		System.out.println("sessione = " + sessione);
		System.out.println("speaker  = " + speaker);

		if (sessione.equals("S1"))
			numSess = 0;
		else if (sessione.equals("S2"))
			numSess = 1;
		else if (sessione.equals("S3"))
			numSess = 2;
		else if (sessione.equals("S4"))
			numSess = 3;
		else if (sessione.equals("S5"))
			numSess = 4;
		else if (sessione.equals("S6"))
			numSess = 5;
		else if (sessione.equals("S7"))
			numSess = 6;
		else if (sessione.equals("S8"))
			numSess = 7;
		else if (sessione.equals("S9"))
			numSess = 8;
		else if (sessione.equals("S10"))
			numSess = 9;
		else if (sessione.equals("S11"))
			numSess = 10;
		else if (sessione.equals("S12"))
			numSess = 11;

		// Se i dati sono sbagliati significa che sono stati trasmessi male e quindi
		// solleva una eccezione
		if (numSess == -1)
			throw new RemoteException();
		if (giorno < 1 || giorno > 3)
			throw new RemoteException();

		return prog[giorno - 1].registra(numSess, speaker);
	}

	// Ritorno il campo
	public Programma programma(int giorno) throws RemoteException {
		System.out.println("Server RMI: richiesto programma del giorno " + giorno);
		return prog[giorno - 1];
	}

	// Avvio del Server RMI
	public static void main(String[] args) {
		prog = new Programma[3]; // creazione programma
		for (int i = 0; i < 3; i++)
			prog[i] = new Programma();
		int registryRemotoPort = 1099; // default
		String registryRemotoName = "RegistryRemoto";
		String serviceName = "ServerCongresso";
		if (args.length != 1 && args.length != 2) {
		} // Controllo argomenti
		String registryRemotoHost = args[0];
		if (args.length == 2) {
			try {
				registryRemotoPort = Integer.parseInt(args[0]);
			} catch (Exception e) {
			}
		} // if
		// Registrazione servizio presso RegistryRemoto
		String completeRemoteRegistryName = "//" + registryRemotoHost +
				":" + registryRemotoPort + "/" + registryRemotoName;

		System.out.println("Sto per registrarmi");

		try {
			RegistryRemotoTagServer registryRemoto = (RegistryRemotoTagServer) Naming.lookup(completeRemoteRegistryName);
			System.out.println("Ho trovato il registytdgdgs");
			ServerCongressoImpl serverRMI = new ServerCongressoImpl();
			registryRemoto.aggiungi(serviceName, serverRMI);
			System.out.println("AAAAAAAAAAAAAAAAAAAaa");
			System.out.println("DAniele culo :\t"+registryRemoto.associaTag(serviceName, "Congresso"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
