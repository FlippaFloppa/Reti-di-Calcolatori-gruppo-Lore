public class RowSwapServer extends Thread{

    private String file;
    private int port;

    public RowSwapServer(String nomeFile,int port){
        file=nomeFile;
        this.port=port;
    }
    
}
