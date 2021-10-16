import java.util.Collection;

public class DiscoveryServer {
    public static void main(String[] args){

        int[] portsArray = new int[args.length/2];
        String[] filesArray = new String[args.length/2];
        Thread[] threadArray = new Thread[args.length/2];

        try{

            int port=Integer.parseInt(args[0]),tmp;

            if(args.length%2==0){
                System.out.println("DiscoveryServer portaDiscoveryServer nomefile1 port1... \n(tutte coppie di argomenti file e porta) nomefileN portN");
                System.exit(1);
            }
    
            if(port<=1024 || port>=65536){
                System.out.println("Numero di porta non valido!\nSelezionare una porta compresa fra 1025 e 65535");
                System.exit(2);
            }

            // Controllo porte duplicate
            for(int i=0;i<args.length;i+=2){
                for(int j=0;j<i;j+=2){
                    if(args[i].equals(args[j])){
                        System.out.println("Porte duplicate!");
                        System.exit(3);
                    }
                }
            }

            
            for(int i=1;i<args.length;i+=2){

                tmp=Integer.parseInt(args[i+1]);

                if((tmp<=1024 || tmp>=65536)){
                    System.out.println("Numero di porta non valido!\nSelezionare una porta compresa fra 1025 e 65535");
                    System.exit(2);
                }

                threadArray[i/2]=new RowSwapServer(args[i],tmp);
                threadArray[i/2].start();

            }

        }catch(Exception e){
            e.printStackTrace();
            System.exit(3);
        }


    }
}
