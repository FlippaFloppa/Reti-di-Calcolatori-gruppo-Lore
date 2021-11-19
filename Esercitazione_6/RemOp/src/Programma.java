
/** 
 * Programma.java
 * 	Serializable -> deve essere restituita da un metodo
 * 		remoto.
 * 	Costruttore = inizializza (a "") tutto il programma.
 * 	Stampa = metodo della classe di appoggio per visualizzare il programma.
 * 	Registra = metodo per registrare un nome in una sessione. Ritorna 0 se OK.
 */

import java.io.Serializable;

public class Programma implements Serializable {

	public String speaker[][] = new String[12][5];

	public Programma() {
		for (int i = 0; i < 5; i++)
			for (int e = 0; e < 12; e++)
				speaker[e][i] = "";
	}

	public synchronized int registra(int sessione, String nome) {
		System.out.println("Programma: registrazione di " + nome + " per la sessione " + (sessione + 1));

		for (int k = 0; k < 5; k++) {
			if (speaker[sessione][k].equals("")) {
				speaker[sessione][k] = nome;
				return 0;
			}
		}
		return 1;
	}

	public void stampa() {
		System.out.println("Sessione\tIntervento1\tIntervento2\tIntervento3\tIntervento4\tIntervento5\n");
		for (int k = 0; k < 12; k++) {
			String line = new String("S" + (k + 1));
			for (int j = 0; j < 5; j++)
				line = line + "\t\t" + speaker[k][j];
			System.out.println(line);

		}
	}

}