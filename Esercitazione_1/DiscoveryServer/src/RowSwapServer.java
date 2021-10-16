import java.io.*;
import java.net.*;

public class RowSwapServer extends Thread{

    private int port;
    private File file;
    private DatagramSocket socketPalle;
    private byte[] buffer;
    private DatagramPacket packetPalle;
    private ByteArrayInputStream biStream;
    private DataInputStream diStream ;
    private ByteArrayOutputStream boStream ;
    private DataOutputStream doStream ;
    private byte[] data ;
    private int row1,row2;

    public RowSwapServer(File file,int port){
        this.port=port;
        this.file=file;

        buffer=new byte[8];
        data= new byte[4];

        try{
            packetPalle=new DatagramPacket(buffer, buffer.length);
            socketPalle=new DatagramSocket(port);

            boStream=new ByteArrayOutputStream();
            doStream=new DataOutputStream(boStream);

        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(666);
        }

    }

    public void run(){

        try{
            while(true){

                packetPalle.setData(buffer);
                socketPalle.receive(packetPalle);

                System.out.println(getFileName());

                biStream=new ByteArrayInputStream(buffer);
                diStream=new DataInputStream(biStream);
    
                row1=diStream.readInt();
                row2=diStream.readInt();
                
                if(swap()){
                    doStream.writeInt(1);
                }else{
                    doStream.writeInt(-1);
                }

                data=boStream.toByteArray();
                packetPalle.setData(data);
                socketPalle.send(packetPalle);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String getFileName(){
        return this.file.getName();
    }
    
    public int getPortNumber(){
        return this.port;
    }

    private boolean swap(){
        return true;
    }
}
