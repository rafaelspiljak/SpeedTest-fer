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


                if(method==0) {
                    clientSocket=new Socket("46.101.104.253",8080);
                    PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
                    BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                    out.println("1");
                    is.read(buff,0,bufferSize);

                    String s = "pocni";
                    long startTime = System.currentTimeMillis();
                    int i = 0;

                    long sizeOfReadData = 0;
                    long endTime = 0;
                    s = "pocni";
                    System.out.println(s);
                    try {
                         is.read(buff, 0, bufferSize);
                        a:
                        while (!s.equals("stop")) {

                            s = in.readLine();

                            if (s.equals("stop")) break a;
                            else {
                                speeds.add(Double.parseDouble(s));
                                instantUploadRate = Double.parseDouble(s);
                            }
                            System.out.println(s);

                            dos.write(buff, 0, bufferSize);
                            dos.flush();
                        }
                        System.out.println("sendToSocket");

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    System.out.println("izasao");

                    finalUploadRate = Ookla(speeds);
                    clientSocket.close();
                    System.out.println(
                        "Odspojen"
                    );
                    finished = true;
            }else if(method==1){
                    int startSize=64000;
                    c:
                    while(true) {
                        clientSocket = new Socket("46.101.104.253", 8080);
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                        out.println("3");


                        out.println(startSize);
                        String s = "pocni";
                        long startTime = System.currentTimeMillis();
                        int i = 0;

                        long sizeOfReadData = 0;
                        long endTime = 0;
                        s = "pocni";
                        System.out.println(s);
                        try {
                            is.read(buff, 0, bufferSize);
                            b:
                            while (!s.equals("stop")) {

                                s = in.readLine();

                                if (s.equals("stop")) break b;
                                else {
                                    if(!s.equals("NaN")) {
                                        speeds.add(Double.parseDouble(s));
                                        instantUploadRate = Double.parseDouble(s);
                                    }
                                }
                                System.out.println(s);

                                dos.write(buff, 0, bufferSize);
                                dos.flush();
                            }
                            endTime=System.currentTimeMillis();
                            System.out.println("sendToSocket");

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                        System.out.println("izasao");


                        clientSocket.close();
                        System.out.println(
                                "Odspojen"
                        );
                        if(System.currentTimeMillis()-startTime<8000){
                            Thread.sleep(500);
                            startSize*=2;
                        }else{
                            break c;
                        }

                    }
                    finalUploadRate = speeds.getLast();
                    System.out.println(finalUploadRate);
                    System.out.println(startSize+" : start size");
                    System.out.println("time : "+(endTime-startTime));
                    finished = true;

            }else if(method==2){
                    clientSocket=new Socket("46.101.104.253",8080);
                    PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
                    BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                    out.println("3");
                    is.read(buff,0,bufferSize);

                    String s = "pocni";

                    int i = 0;

                    long sizeOfReadData = 0;
                    long endTime = 0;
                    s = "pocni";
                    out.println("12000000");
                    System.out.println(s);
                    try {
                        is.read(buff, 0, bufferSize);
                        a:
                        while (!s.equals("stop")) {

                            s = in.readLine();

                            if (s.equals("stop")) break a;
                            else {
                                if(!s.equals("NaN")) {
                                    speeds.add(Double.parseDouble(s));
                                    instantUploadRate = Double.parseDouble(s);
                                }
                            }
                            System.out.println(s);

                            dos.write(buff, 0, bufferSize);
                            dos.flush();
                        }
                        System.out.println("sendToSocket");

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    System.out.println("izasao");

                    finalUploadRate = FastCom(speeds);
                    clientSocket.close();
                    System.out.println(
                            "Odspojen"
                    );
                    finished = true;
                }
        }catch(Exception e){

        }

    }
    public static double Ookla(LinkedList<Double> speeds){
        double sum=0.0;
        Collections.sort(speeds);
        LinkedList<Double> newList=new LinkedList<>();
        for(int i=(int)(speeds.size()*0.3);i<(int)(speeds.size()*0.9);i++){
            newList.add(speeds.get(i));
        }

        for(double speed:newList){
            sum+=speed;
        }

        return sum/(newList.size());
    }
    public static double FastCom(LinkedList<Double> speeds){
        double sum=0.0;
        Collections.sort(speeds);
        LinkedList<Double> newList=new LinkedList<>();

        int i=0;
        for(double speed:speeds){
            if(sum!=Double.NaN){
                sum+=speed;
            }else{
                i++;
            }

        }

        return sum/(speeds.size()-i);
    }
}
