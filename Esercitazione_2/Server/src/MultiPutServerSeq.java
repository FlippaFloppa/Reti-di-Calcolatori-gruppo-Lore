
import java.io.*;
import java.net.*;

public class MultiPutServerSeq {
    public static final int PORT = 54321; // default port

    public static void main(String[] args) throws IOException {

        int port = -1;

        try { // Controllo argomenti
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
                if (port < 1024 || port > 65535) {
                    System.out.println("Usage: java LineServer [serverPort>1024]");
                    System.exit(1);
                }
            } else if (args.length == 0) {
                port = PORT;
            } else {
                System.out.println("Usage: java MultiPutServerThread or java MultiPutServerThread port");
                System.exit(1);
            }
        } // try
        catch (Exception e) {
            System.out.println("Problemi, i seguenti: ");
            e.printStackTrace();
            System.out.println("Usage: java MultiPutServerThread or java MultiPutServerThread port");
            System.exit(1);
        }

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            System.out.println("MultiPutServerSeq: avviato ");
            System.out.println("Server: creata la server socket: " + serverSocket);
        } catch (Exception e) {
            System.err.println("Server: problemi nella creazione della server socket: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {

            while (true) {
                System.out.println("Server: in attesa di richieste...\n");

                try {
                    // bloccante fino ad una pervenuta connessione
                    clientSocket = serverSocket.accept();
                    // clientSocket.setSoTimeout(30000);
                    System.out.println("Server: connessione accettata: " + clientSocket);
                } catch (Exception e) {
                    System.err.println("Server: problemi nella accettazione della connessione: " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }

                DataInputStream inSock;
                DataOutputStream outSock;

                try {

                    try {
                        // creazione stream di input e out da socket
                        inSock = new DataInputStream(clientSocket.getInputStream());
                        outSock = new DataOutputStream(clientSocket.getOutputStream());
        
                    } catch (SocketTimeoutException ste) {
                        System.out.println("Timeout scattato: ");
                        ste.printStackTrace();
                        clientSocket.close();
                        System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
                        return;
                    } catch (IOException ioe) {
                        System.out.println("Problemi nella creazione degli stream di input/output " + "su socket: ");
                        ioe.printStackTrace();
                        // il server continua l'esecuzione riprendendo dall'inizio del ciclo
                        return;
                    } catch (Exception e) {
                        System.out.println("Problemi nella creazione degli stream di input/output " + "su socket: ");
                        e.printStackTrace();
                        return;
                    }
        
                    String dir = inSock.readUTF();
        
                    outSock.writeUTF("conferma");
                    String nomeFile;
                    FileOutputStream outFile = null;
                    File curFile = null;
        
                    File directory = new File(dir);
                    directory.mkdir();
                    long flength=-1;
                    long i=0;
                    int buffer;
        
                    try {
                        while ((nomeFile = inSock.readUTF()) != null) {
        
                            curFile = new File(dir + "/" + nomeFile);
                            System.out.println(nomeFile);
        
                            if (curFile.exists())
                                outSock.writeUTF("salta");
                            else {
                                curFile.createNewFile();
                                outSock.writeUTF("attiva");
        
                                // ciclo di ricezione dal client, salvataggio file e stamapa a video
                                try {
                                    outFile = new FileOutputStream(curFile);
        
                                    System.out.println("Ricevo il file " + nomeFile + ": \n");
                                    flength=inSock.readLong();
                                    for(i=0; i<flength; i++){
                                        buffer=inSock.read();
                                        outFile.write(buffer);
                                    }
                                    System.out.println("\nRicezione del file " + nomeFile + " terminata\n");
        
                                    outFile.close();
                                    outSock.writeUTF("conferma");
                                    outSock.flush();
        
                                } catch (SocketTimeoutException ste) {
                                    System.out.println("Timeout scattato: ");
                                    ste.printStackTrace();
                                    clientSocket.close();
                                    System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
                                    return;
                                } catch (Exception e) {
                                    System.err
                                            .println("\nProblemi durante la ricezione e scrittura del file: " + e.getMessage());
                                    e.printStackTrace();
                                    clientSocket.close();
                                    System.out.println("Terminata connessione con " + clientSocket);
                                    return;
                                }
                            }
                        }
                    } catch (EOFException e) {
                        System.out.println("Operazione di trasferimento directory terminata!\n\n\n\n\n\n\n\n\n\n\n");
                    }
        
                    clientSocket.shutdownInput(); // chiusura socket (downstream)
                    clientSocket.shutdownOutput(); // chiusura socket (dupstream)
                    System.out.println("\nTerminata connessione con " + clientSocket);
                    clientSocket.close();
        
                }
                
                 catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Errore irreversibile, MultiPutServerThread: termino...");
                    System.exit(3);
                }

            } // while
        }
        // qui catturo le eccezioni non catturate all'interno del while
        // in seguito alle quali il server termina l'esecuzione
        catch (Exception e) {
            e.printStackTrace();
            // chiusura di stream e socket
            System.out.println("MultiPutServerCon: termino...");
            System.exit(2);
        }

    }
}
