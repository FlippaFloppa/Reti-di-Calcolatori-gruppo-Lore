import java.io.*;
import java.net.*;

// Thread lanciato per ogni richiesta accettata
// versione per il trasferimento di file binari
class MultiPutServerThread extends Thread {

    private Socket clientSocket = null;

    /**
     * Constructor
     * 
     * @param clientSocket
     */
    public MultiPutServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {

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

            String nomeFile;
            FileOutputStream outFile = null;
            File curFile = null;
            long flength=-1;
            long i=0;
            int buffer;
            try {
                while ((nomeFile = inSock.readUTF()) != null) {

                    curFile = new File(nomeFile);
                    System.out.println(nomeFile);

                    if (curFile.exists())
                        outSock.writeUTF("salta");
                    else {
                        curFile.createNewFile();
                        outSock.writeUTF("attiva");

                        // ciclo di ricezione dal client, salvataggio file e stamapa a video
                        try {

                            flength=inSock.readLong();
                            System.out.println(flength);

                            outFile = new FileOutputStream(curFile);

                            System.out.println("Ricevo il file " + nomeFile + ": \n");

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

    }

}