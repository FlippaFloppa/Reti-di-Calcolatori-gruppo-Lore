import java.io.File;

public class RowSwapServer extends Thread{

    private int port;
    private File file;

    public RowSwapServer(File file,int port){
        this.port=port;
        this.file=file;
    }

    public void run(){

    }

    public String getFileName(){
        return this.file.getName();
    }
    
    public int getPortNumber(){
        return this.port;
    }
}
