import java.io.*;
import java.net.*;

public class RSClient {

    public static void main(String[] args){

        DatagramSocket socket=null;
        DatagramPacket packet=null;
        byte[] buf=null;
        InetAddress addr=null; 
        int port=-1;
        
        try{
            if (args.length == 3){
                addr = InetAddress.getByName(args[0]); 
                port = Integer.parseInt(args[1]);

                if(port<=1024 || port>=65536){
                    System.out.println("Numero di porta non valido!\nSelezionare una porta compresa fra 1025 e 65535");
                    System.exit(4);
                }
            }        
            else{ 
                System.out.println("Utilizzo: java RSClient ipDS portDS fileName");
                System.exit(1);
            } 
        } 
        catch(UnknownHostException e){
            e.printStackTrace();
            System.exit(3);
        }

        try{ 

            buf=new byte[8];
            socket = new DatagramSocket(); 
            packet = new DatagramPacket(buf, buf.length, addr, port);
        } 
        catch (SocketException e) { 
            e.printStackTrace();
            System.exit(2);
        } 

        //Inzio codice Datagram
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        try{
            
            // Inizializzazione Stream
            ByteArrayOutputStream boStream = new ByteArrayOutputStream(); 
            DataOutputStream doStream = new DataOutputStream(boStream);
            doStream.writeUTF(args[2]);
            
            // Invio del datagram al DiscoveryServer
            byte[] data = boStream.toByteArray(); 
            packet.setData(data);
            socket.send(packet);
            
            // Redirezione input
            packet.setData(buf);
            System.out.println("In attesa di risposta da DiscoveryServer ...");
            socket.receive(packet);
            
            ByteArrayInputStream biStream = new ByteArrayInputStream( packet.getData(),0,packet.getLength());
            DataInputStream diStream = new DataInputStream(biStream);

        
            // Reimpostazione packet
            if((port=diStream.readInt())<0){
                System.out.println("File non presente");
                System.exit(8);
            }

            packet.setPort(port);
            
            String[] strings;
            String line;
            int row1,row2;
            
            System.out.println("Inserire l'indice delle righe da scambiare, separate da spazio\n^D(Unix)/^Z(Win) invio per uscire");
            
            while((line=stdIn.readLine())!=null){


                strings=line.split(" ");
                System.out.println(strings[0]+"\n"+strings[1]);

                row1=Integer.parseInt(strings[0].trim());
                row2=Integer.parseInt(strings[1].trim());
                
                //Controllo righe
                if(row1<0 || row2<0){ 
                    System.out.println("Impossibile swappare righe negative!");
                    System.exit(7);
                }
                
                // Invio righe da swappare al DiscoveryServer
                boStream.close();
                doStream.close();
                
                boStream = new ByteArrayOutputStream(); 
                doStream = new DataOutputStream(boStream);

                doStream.writeUTF(row1+" "+row2);
                data=boStream.toByteArray();

                packet.setData(data);
                socket.send(packet);

                // Ricezione conferma dal Serever
                packet.setData(buf);
                socket.receive(packet);

                biStream = new ByteArrayInputStream( packet.getData(),0,packet.getLength());
                diStream = new DataInputStream(biStream);

                if(diStream.readInt()<0){
                    System.out.println("Operazione di swap fallita!");
                }
                else{
                    System.out.println("Swap avvenuta con successo!");
                }
            }

        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(5);
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(6);
        }

        socket.close();
        System.out.println("Terminata esecuzione Client");
    }
}
