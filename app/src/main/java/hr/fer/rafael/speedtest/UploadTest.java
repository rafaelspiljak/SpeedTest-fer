package hr.fer.rafael.speedtest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;

public class UploadTest extends  Thread{

    int method=0;
    long uploadedByte=0;
    long startTime=0;
    long endTime=0;

    boolean finished=false;
    double instantUploadRate=0.0;
    double finalUploadRate=0.0;
    InputStream is=null;
    Socket clientSocket=null;
    Socket ss=null;

    LinkedList<Double> speeds=new LinkedList<>();
    UploadTest(InputStream is,int method){
        this.is=is;
        this.method=method;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public long getUploadedByte() {
        return uploadedByte;
    }

    public void setUploadedByte(long uploadedByte) {
        this.uploadedByte = uploadedByte;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public double getInstantUploadRate() {
        return instantUploadRate;
    }

    public void setInstantUploadRate(double instantUploadRate) {
        this.instantUploadRate = instantUploadRate;
    }

    public double getFinalUploadRate() {
        return finalUploadRate;
    }

    public void setFinalUploadRate(double finalUploadRate) {
        this.finalUploadRate = finalUploadRate;
    }
    public static LinkedList<Double> addToList(LinkedList<Double> speeds, Double value){
        speeds.add(value);
        return speeds;
    }

    @Override
    public void run() {
        try{
            int bufferSize=32000;
            byte[] buff=new byte[bufferSize];
            clientSocket=new Socket("46.101.104.253",8080);
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            out.println("1");
            is.read(buff,0,bufferSize);
            System.out.println("buffer: "+buff.toString());
            System.out.println("buffer: "+buff.toString()+" "+buff.toString().length());
            System.out.println("buffer: "+buff.toString()+" "+buff.toString().length());
            String s="pocni";
            a:
            while(!s.equals("stop")){
                s=in.readLine();
                if(!s.equals("NaN")&&!s.equals("stop")){
                    speeds.add(Double.parseDouble(s));
                    setInstantUploadRate(Double.parseDouble(s));
                }
                if(s.equals("stop"))break a;
                dos.write(buff,0,bufferSize);
                dos.flush();
                dos.flush();
            }
            Collections.sort(speeds);
            clientSocket.close();
            finished=true;
           /* ss=new Socket("46.101.104.253",443);
            BufferedReader in1=new BufferedReader(new InputStreamReader(ss.getInputStream()));
            try{
                setFinalUploadRate(Double.parseDouble(in1.readLine()));
            }catch(Exception e){
                setFinalUploadRate(speeds.getLast());
            }
            ss.close();
            finished=true;*/
        }catch(Exception e){

        }
    }
}
