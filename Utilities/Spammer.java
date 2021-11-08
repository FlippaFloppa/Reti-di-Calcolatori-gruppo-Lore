import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Spammer {
public static void main(String[] args) throws IOException {
    final String WRITE_PATH = "/home/lore09/Documenti/Reti-di-Calcolatori-gruppo-Lore/Esercitazione_4/100MbPalle";
    FileWriter fileWriter = new FileWriter(WRITE_PATH);
    PrintWriter printWriter = new PrintWriter(fileWriter);
    for(int i = 1; i <= 10000000  ; i++) {
    printWriter.println("palle ttt");
    }
    printWriter.close();
}
}
