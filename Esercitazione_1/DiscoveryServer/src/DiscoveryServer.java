import java.io.*;
import java.net.*;

public class DiscoveryServer {
    public static void main(String[] args){

        RowSwapServer[] threadArray = new RowSwapServer[args.length/2];
        int port=-1,tmp;

        try{

            if(args.length%2==0){
                System.out.println("DiscoveryServer portaDiscoveryServer nomefile1 port1... \n(tutte coppie di argomenti file e porta) nomefileN portN");
                System.exit(1);
            }

            port=Integer.parseInt(args[0]);

    
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

            // Creazione thread
            File tmpFile=null;
            for(int i=1;i<args.length;i+=2){

                tmp=Integer.parseInt(args[i+1]);

                if((tmp<=1024 || tmp>=65536)){
                    System.out.println("Numero di porta non valido!\nSelezionare una porta compresa fra 1025 e 65535");
                    System.exit(2);
                }

                tmpFile=new File(args[i]);

                threadArray[i/2]=new RowSwapServer(tmpFile,tmp);
            }

            for(RowSwapServer r:threadArray)    r.start();


            // Impostazione connessionein datagram
            DatagramSocket socket = new DatagramSocket(port); 
            byte[] buffer=new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            String nomeFile = null;
            ByteArrayInputStream biStream = null;
            DataInputStream diStream = null;
            ByteArrayOutputStream boStream = null;
            DataOutputStream doStream = null;
            byte[] data = null;
            int i;
            

            while(true){
                
                packet.setData(buffer);
                socket.receive(packet);
                i=0;

                biStream= new ByteArrayInputStream(packet.getData(),0,packet.getLength());
                diStream= new DataInputStream(biStream);

                nomeFile=diStream.readUTF();

                data=new byte[4];
                boStream=new ByteArrayOutputStream();
                doStream=new DataOutputStream(boStream);

                // Identifico il thread con il nome del file
                while(i<threadArray.length && !threadArray[i].getFileName().equals(nomeFile)){
                    i++;
                }

                if(i == threadArray.length){
                    doStream.writeInt(-1);  //file non trovato
                }
                else{
                    doStream.writeInt(threadArray[i].getPortNumber()); //File identificato. redirezione su Thread
                }

                data=boStream.toByteArray();
                packet.setData(data);
                socket.send(packet);
            }


        }catch(Exception e){
            e.printStackTrace();
            System.exit(3);
        }


    }
}