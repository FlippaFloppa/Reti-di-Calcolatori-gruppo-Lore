
/**
 * Interfaccia remota di servizio
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerCongresso extends Remote {

	int registrazione(int giorno, String sessione, String speaker) throws RemoteException;

	Programma programma(int giorno) throws RemoteException;

}