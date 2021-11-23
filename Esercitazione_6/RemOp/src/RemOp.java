

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote {

	public int conta_righe(String fileName, int max) throws RemoteException;
	public Risposta elimina_riga(String fileName, int line) throws RemoteException;
	
}
