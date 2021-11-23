import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements RemOp {

	protected ServerImpl() throws RemoteException {
		super();
	}

	@Override
	public synchronized int conta_righe(String fileName, int min) throws RemoteException {
		try {
			int res = 0;
			String linea;
			File f = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(f));
			while ((linea = br.readLine()) != null) {
				if (linea.split("[ \t]+").length > min)
					res++;
			}
			br.close();
			return res;
		} catch (Exception e) {
			throw new RemoteException("Rilancio eccezione: " + e.getMessage(), e);
		}
	}

	@Override
	public synchronized Risposta elimina_riga(String fileName, int line) throws RemoteException {
		try {
			int curLine = 1;
			String linea;
			File f = new File(fileName), tmp = new File("temp");
			BufferedReader br = new BufferedReader(new FileReader(f));
			PrintWriter pw = new PrintWriter(tmp);
			while ((linea = br.readLine()) != null) {
				if (curLine !=line) {
					pw.println(linea);
				}
				curLine++;
			}
			br.close();
			pw.close();
			Files.move(tmp.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
			if(line>=curLine) throw new Exception("Numero linea maggiore dimensione file");
			return new Risposta(fileName, curLine - 2);
		} catch (Exception e) {
			throw new RemoteException("Rilancio eccezione: " + e.getMessage(), e);
		}
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
