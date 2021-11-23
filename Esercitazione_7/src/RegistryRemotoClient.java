import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoClient extends Remote {
    public Remote cerca(String nomeLogico) throws RemoteException;

    public Remote[] cercaTutti(String nomeLogico) throws RemoteException;
}
