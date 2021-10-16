import java.io.*;
import java.net.*;

public class RowSwapServer extends Thread{

    private int port;
    private File file;
    private DatagramSocket socketThread;
    private byte[] bufferThread;
    private DatagramPacket packetThread;
    private ByteArrayInputStream biStream;
    private DataInputStream diStream ;
    private ByteArrayOutputStream boStream ;
    private DataOutputStream doStream ;
    private byte[] data ;
    private int row1,row2;

    public RowSwapServer(File file,int port){
        this.port=port;
        this.file=file;

        bufferThread=new byte[8];
        data= new byte[4];

        try{
            packetThread=new DatagramPacket(bufferThread, bufferThread.length);
            socketThread=new DatagramSocket(port);

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

                packetThread.setData(bufferThread);
                socketThread.receive(packetThread);
                
                biStream=new ByteArrayInputStream(packetThread.getData(),0,packetThread.getLength());
                diStream=new DataInputStream(biStream);
                
                String[] tmpThread =diStream.readUTF().trim().split(" ");
                
                row1=Integer.parseInt(tmpThread[0].trim());
                row2=Integer.parseInt(tmpThread[1].trim());
                System.out.println(row1+"\t"+row2);
                
                if(swap()){
                    doStream.writeInt(1);
                }else{
                    doStream.writeInt(-1);
                }

                data=boStream.toByteArray();
                packetThread.setData(data);
                socketThread.send(packetThread);
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

    private boolean swap()throws FileNotFoundException, IOException{
        
    if(row1 == row2)
        return true;
    //
    BufferedReader in = null;
    BufferedWriter out = null;
    StringBuilder sb = new StringBuilder();

        in = new BufferedReader(new FileReader(file));
        in.mark(0);

    int i = 1;
    String l1 = null;
    String l2 = null;
    String l;
        while((l = in.readLine()) != null) {
            if(i == row1) {
                l1 = l;
            }else if(i == row2) {
                l2 = l;
            }
            i++;
        }
        in.close();

    if((l1 == null) || (l2 == null)) {
        return false;
    }else {
            in = new BufferedReader(new FileReader(file));
            i = 1;
            while((l = in.readLine()) != null) {
                if(i == row1) {
                    sb.append(l2);
                }else if(i == row2) {
                    sb.append(l1);
                }else {
                    sb.append(l);
                }
                sb.append("\n");
                i++;
            }
            in.close();
            out = new BufferedWriter(new FileWriter(file));
            out.write(sb.toString());
            out.flush();
            out.close();
        
            }
        return true;
    }
}
