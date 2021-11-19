package esercitazione_6;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Optional;

public interface RemOp extends Remote {

	public int conta_righe(String fileName, int max) throws RemoteException;
	public Optional<Risposta> elimina_riga(String fileName, int line) throws RemoteException;
}
