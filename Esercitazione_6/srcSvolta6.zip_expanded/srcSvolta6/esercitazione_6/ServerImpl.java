package esercitazione_6;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class ServerImpl extends UnicastRemoteObject implements RemOp {

	protected ServerImpl() throws RemoteException {
		super();
	}

	@Override
	public int conta_righe(String fileName, int max) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Optional<Risposta> elimina_riga(String fileName, int line) throws RemoteException {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	
	public static void main(String[] args) {
		final int REGISTRYPORT = 1099;
		String registryHost = "localhost";
		String serviceName = "RemOp"; // lookup name...

		// Registrazione del servizio RMI
		String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
		try {
			ServerImpl serverRMI = new ServerImpl();
			Naming.rebind(completeName, serverRMI);
			System.out.println("Server RMI: Servizio \"" + serviceName + "\" registrato");
		} catch (Exception e) {
			System.err.println("Server RMI \"" + serviceName + "\": " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
