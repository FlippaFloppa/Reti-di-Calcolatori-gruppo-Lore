import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoServer extends RegistryRemotoClient {
    // Tabella: la prima colonna i nomi, la seconda i riferimenti remoti
    public Object[][] restituisciTutti() throws RemoteException;

    public boolean aggiungi(String nomeLogico, Remote riferimento) throws RemoteException;

    public boolean eliminaPrimo(String nomeLogico) throws RemoteException;

    public boolean eliminaTutti(String nomeLogico) throws RemoteException;
}
