import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// Consumatore e' un filtro
public class Consumatore {
	public static void main(String[] args) {
		FileReader r = null;
		char ch;
		int x;
	  
		if(args.length==1) {	//codice replicato per evitare controlli multipli nel ciclo
			
			try {
				
				while ((x = System.in.read()) >= 0) { 
					ch = (char) x;
					if(args[0].indexOf(ch)==-1) System.out.print(ch);
				}
				
			} catch(IOException ex){
				
				System.out.println("Errore di input");
				System.exit(2);
			}
			
		}
		
		else if(args.length==2) {
		
			try {
				r = new FileReader(args[1]);
			} catch(FileNotFoundException e){
				
				System.out.println("File non trovato");
				System.exit(1);
			}
			
			try {
				
				while ((x = r.read()) >= 0) { 
					ch = (char) x;
					if(args[0].indexOf(ch)==-1) System.out.print(ch);
				}
				r.close();
			} catch(IOException ex){
				
				System.out.println("Errore di input");
				System.exit(2);
			}
		}
		
		else {
			System.out.println("Utilizzo: consumatore <filterprefix> <inputFilename> \n oppure \n consumatore <filterprefix> per stdin");
			System.exit(0);
		}
	
}}
