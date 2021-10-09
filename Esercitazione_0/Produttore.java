import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

// Produttore ORA e' un filtro
public class Produttore {
	public static void main(String[] args) {		
		BufferedReader in = null;
		
		if (args.length != 1){
			System.out.println("Utilizzo: produttore <inputFilename>");
			System.exit(0);
		}
		
		in = new BufferedReader(new InputStreamReader(System.in));
			
		FileWriter fout;
		String inputl = null;
		boolean status=true;

		try {
			fout = new FileWriter(args[0]);

			while (status){
				System.out.println("Inserisci la nuova riga");
				inputl = in.readLine();
				if(inputl==null)status=false;
				else{
					fout.write(inputl+"\n", 0, inputl.length()+1);
				}
			}		
			fout.close();
		} 
		catch (NumberFormatException nfe) { 
			nfe.printStackTrace(); 
			System.exit(1); // uscita con errore, intero positivo a livello di sistema Unix
		}
	    catch (IOException e) { 
			e.printStackTrace();
			System.exit(2); // uscita con errore, intero positivo a livello di sistema Unix
		}
	}
}

