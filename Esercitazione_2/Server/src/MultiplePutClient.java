import java.net.*;
import java.io.*;

public class MultiplePutClient {

	public static void main(String[] args) throws IOException {

		InetAddress addr = null;
		int port = -1;

		try { // Controllo argomenti
			if (args.length == 2) {
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
			} else {
				System.out.println("Usage: java MultiplePutClient serverAddr serverPort");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out.println("Usage: java MultiplePutClient serverAddr serverPort");
			System.exit(2);
		}

		// Inizializzazione variabili
		Socket socket = null;
		FileInputStream inFile = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		String nomeDir = null;
		int minFileSize = 0;

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.print(
				"MultiplePutClient Started.\n\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");

		try {
			while ((nomeDir = stdIn.readLine()) != null) {

				// creazione socket
				try {
					socket = new Socket(addr, port);
					System.out.println("Creata la socket: " + socket);
					// socket.setSoTimeout(5000);
				} catch (Exception e) {
					System.out.println("Problemi nella creazione della socket: ");
					e.printStackTrace();
				}

				// creazione stream di input/output su socket
				try {
					inSock = new DataInputStream(socket.getInputStream());
					outSock = new DataOutputStream(socket.getOutputStream());
				} catch (IOException e) {
					System.out.println("Problemi nella creazione degli stream su socket: ");
					e.printStackTrace();
				}

				if (new File(nomeDir).isDirectory()) { // Il file passato è una directory

					System.out.print("\nImmetti dimensione minima file: ");
					try {
						minFileSize = Integer.parseInt(stdIn.readLine().trim());
					} catch (NumberFormatException e) {
						System.out.println("Dimensione file errata!");
						continue;
					}

				} else {
					// Directory non trovata o file
					System.out.println("Directory non identificata");
					System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
					continue;
				}

				// Inizio operazioni su files
				try {
					File[] filesArray = new File(nomeDir).listFiles();
					if (filesArray == null) {
						System.out.println("Nella directory non sono presenti files");
					}

					outSock.writeUTF(nomeDir);
					if (!inSock.readUTF().equals("conferma")) {
						System.out.println("Conferma directory non ricevuta");
						continue;
					}

					for (File f : filesArray) {
						if (f.isFile() && f.length() >= minFileSize) { // Check dimensioni del file

							outSock.writeUTF(f.getName());
							System.out.println("\n\nInviato il nome del file " + f.getName());

							if (inSock.readUTF().equals("attiva")) {

								System.out.println("Inizio la trasmissione di " + f.getName());
								inFile = new FileInputStream(f);
								FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock);
								inFile.close(); // chiusura file

								System.out.println("Trasmissione di " + f.getName() + " terminata ");
								System.out.println(
										"Esito trasmissione: " + inSock.readUTF() + "\n--------------------\n");

							} else {
								System.out.println(f.getName() + " non sarà inviato");
							}

						} else {
							if (f.isFile())
								System.out.println(
										"Il file " + f.getName() + " non raggiunge la dimensione minima selezionata");
						}
					}

				} catch (SocketTimeoutException ste) {
					System.out.println("Timeout scattato: ");
					ste.printStackTrace();
					socket.close();
					System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
					// il client continua l'esecuzione riprendendo dall'inizio del ciclo
					continue;
				} catch (Exception e) {
					System.out.println("Problemi nell'invio dei file in " + nomeDir + ": ");
					e.printStackTrace();
					socket.close();
					System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
					// il client continua l'esecuzione riprendendo dall'inizio del ciclo
					continue;
				}

				socket.shutdownOutput();
				socket.shutdownInput();
				socket.close();

				System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
			}

			System.out.println("PutFileClient: termino...");
		}

		catch (Exception e) {
			System.err.println("Errore irreversibile, il seguente: ");
			e.printStackTrace();
			System.err.println("Chiudo!");
			System.exit(3);
		}
	}
}
