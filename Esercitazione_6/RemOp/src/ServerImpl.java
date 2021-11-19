import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class ServerImpl extends UnicastRemoteObject implements RemOp {

	protected ServerImpl() throws RemoteException {
		super();
	}

	@Override
	public int conta_righe(String fileName, int max) throws RemoteException{
		try {
		int res=0;
		String linea;
		File f=new File(fileName);
		BufferedReader br=new BufferedReader(new FileReader(f));
			while ((linea=br.readLine())!=null) {
				if(linea.split("[ \t]+").length>max) res++;
				}
			return res;
		} catch (Exception e) {
			throw new RemoteException("Rilancio eccezione: " + e.getMessage(), e);
		}
	}

	@Override
	public Risposta elimina_riga(String fileName, int line) throws RemoteException {
		// TODO Auto-generated method stub
		return new Risposta(fileName,0);
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
