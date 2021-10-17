import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
    private long initMillis,finalMillis;

    public RowSwapServer(File file,int port){
        this.port=port;
        this.file=file;

        bufferThread=new byte[256];
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

                initMillis=System.currentTimeMillis();
                
                biStream=new ByteArrayInputStream(packetThread.getData(),0,packetThread.getLength());
                diStream=new DataInputStream(biStream);
                
                String[] tmpThread =diStream.readUTF().trim().split(" ");
                
                row1=Integer.parseInt(tmpThread[0].trim());
                row2=Integer.parseInt(tmpThread[1].trim());
                System.out.println(row1+"\t"+row2);

                boStream=new ByteArrayOutputStream();
                doStream=new DataOutputStream(boStream);
                
                if(swap()){
                    doStream.writeInt(1);
                }else{
                    doStream.writeInt(-1);
                }

                data=boStream.toByteArray();
                packetThread.setData(data);
                socketThread.send(packetThread);

                finalMillis=System.currentTimeMillis();

                System.out.println("Tempo impiegato: "+(finalMillis-initMillis));
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

    public boolean swap() throws EOFException,FileNotFoundException,IOException{

        if(row1==row2) return true;

        if(row1>row2){
            int tmp2=row1;
            row1=row2;
            row2=tmp2;
        }

        File fout=new File("out");
        BufferedReader br=new BufferedReader(new FileReader(file));
        String line;
        String swapLine=null;
        int i=0;
        PrintWriter pw=new PrintWriter(fout);
        
        while((line=br.readLine())!=null){
            i++;
            if(i==row1){
                swapLine=line;
                BufferedReader br2 = new BufferedReader(new FileReader(file));
                int j=0;
                
                while(j != row2){
                    j++;
                    if((line = br2.readLine())==null){
                        br2.close();
                        fout.delete();
                        return false;
                    }
                }
                br2.close();
                
            }
            else if (i==row2){
                line=swapLine;
            }
            pw.println(line);
        }
        
        //fout.renameTo(file);

        pw.close();
        br.close();

        // Riga scelta non esistente
        if(i<row1){
            fout.delete();
            return false;
        }

        Files.move(fout.toPath(),file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    
        return true;
    }
}