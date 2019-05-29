package hr.fer.rafael.speedtest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;

public class DownloadTest extends Thread{
    double instantDownloadRate=0.0;
    double finalDownloadRate=0.0;
    long startTime=0;
    long endTime=0;
    long downloadedByte=0;
    boolean wait=true;
    boolean finished=false;
    int timeOut=15;
    Socket clientSocket=null;
    int method=0;//ookla=0, SOM=1, moja=2;
    String hostName="46.101.104.253";
    int portNumber=8080;
    LinkedList<Double> speeds=new LinkedList<>();
    DownloadTest(int method){
        this.method=method;
    }

    public double getInstantDownloadRate() {
        return instantDownloadRate;
    }

    public void setInstantDownloadRate(double instantDownloadRate) {
        this.instantDownloadRate=getDownloadedByte()*0.008/(System.currentTimeMillis()-startTime);

    }

    public double getFinalDownloadRate() {
        return finalDownloadRate;
    }

    public void setFinalDownloadRate(double finalDownloadRate) {
        this.finalDownloadRate = 0.008*getDownloadedByte()/(endTime-startTime);
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

    public long getDownloadedByte() {
        return downloadedByte;
    }

    public void setDownloadedByte(long downloadedByte) {
        this.downloadedByte = downloadedByte;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    @Override
    public void run() {
        if(getMethod()==0){
            try{
                clientSocket=new Socket(hostName,portNumber);
                long currentByte=0;
                long innerStartTime=0;
                long innerEndTime=0;
                long downloadTime=0;
                downloadedByte=0;
                PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
                out.println("0");
                BufferedInputStream bis=new BufferedInputStream(clientSocket.getInputStream());
                BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                int bufferSize=128000;
                byte[] buff=new byte[bufferSize];
                startTime=System.currentTimeMillis();

                while(downloadTime<15000){
                    out.println(downloadedByte);
                    innerStartTime=System.currentTimeMillis();
                    System.out.println("skinuto "+getDownloadedByte());
                    currentByte=bis.read(buff,0,bufferSize);
                    innerEndTime=System.currentTimeMillis();
                    downloadedByte+=currentByte;
                    downloadTime+=(innerEndTime-innerStartTime);
                    instantDownloadRate=downloadedByte/downloadTime*0.008;
                    System.out.println("instant: "+instantDownloadRate);
                    speeds.add(instantDownloadRate);
                    //System.out.println("instant:"+instantDownloadRate);
                }
                out.println("stop");
                endTime=System.currentTimeMillis();
                finalDownloadRate=Ookla(speeds);
                finished=true;
                clientSocket.close();
                System.out.println("Gotovo");
                wait=false;
            }catch (Exception e){

            }

        }else if (getMethod()==1){
            try{
                int dataSize=128000;
                long downloadSize=0;
                long fullDownloadTime=0;
                double lastDownload;
                a:
                while(true) {
                    downloadedByte=0;
                    clientSocket = new Socket(hostName, portNumber);
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("0");
                    long outerStartTime=System.currentTimeMillis();
                    BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    int bufferSize = 128000;
                    byte[] buff = new byte[bufferSize];
                    startTime = System.currentTimeMillis();
                    long innerStartTime=0;
                    long innerEndTime=0;
                    long downloadTime=0;
                    long currentByte=0;
                    while (downloadedByte <dataSize) {

                        out.println(downloadedByte);
                        innerStartTime=System.currentTimeMillis();
                        System.out.println("skinuto "+getDownloadedByte());
                        currentByte=bis.read(buff,0,bufferSize);
                        innerEndTime=System.currentTimeMillis();
                        downloadedByte+=currentByte;
                        downloadTime+=(innerEndTime-innerStartTime);
                        instantDownloadRate=downloadedByte/downloadTime*0.008;
                        System.out.println("instant: "+instantDownloadRate);
                        speeds.add(instantDownloadRate);
                        System.out.println("instant:"+instantDownloadRate);

                    }
                    endTime = System.currentTimeMillis();
                    downloadSize=downloadedByte;
                    fullDownloadTime=downloadTime;
                    lastDownload=instantDownloadRate;
                    out.println("stop");
                    if(endTime-startTime<8000){
                        if(dataSize>128000000)break a;
                        dataSize*=2;


                    }else{
                        break a;
                    }

                    clientSocket.close();
                    Thread.sleep(500);
                }
                //finalDownloadRate=downloadSize/fullDownloadTime*0.008;
                finalDownloadRate=lastDownload;
                System.out.println("Gotovo");
                wait=false;
                finished=true;
            }catch (Exception e){

            }


        }else if(getMethod()==2){
            try{
                clientSocket=new Socket(hostName,portNumber);
                long currentByte=0;
                long innerStartTime=0;
                long innerEndTime=0;
                long downloadTime=0;
                downloadedByte=0;
                PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
                out.println("0");
                BufferedInputStream bis=new BufferedInputStream(clientSocket.getInputStream());
                BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                int bufferSize=128000;
                byte[] buff=new byte[bufferSize];
                startTime=System.currentTimeMillis();

                while(downloadedByte<25000000){
                    out.println(downloadedByte);
                    innerStartTime=System.currentTimeMillis();
                    System.out.println("skinuto "+getDownloadedByte());
                    currentByte=bis.read(buff,0,bufferSize);
                    innerEndTime=System.currentTimeMillis();
                    downloadedByte+=currentByte;
                    downloadTime+=(innerEndTime-innerStartTime);
                    instantDownloadRate=downloadedByte/downloadTime*0.008;
                    System.out.println("instant: "+instantDownloadRate);
                    speeds.add(instantDownloadRate);
                    System.out.println("instant:"+instantDownloadRate);
                }
                out.println("stop");
                endTime=System.currentTimeMillis();
                finalDownloadRate=FastCom(speeds);
                finished=true;
                clientSocket.close();
                System.out.println("Gotovo");
                wait=false;
            }catch (Exception e){

            }
        }
        finished=true;
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
    public static double SpeedOfMe(LinkedList<Double> speeds){
        double sum=0.0;
        Collections.sort(speeds);
        LinkedList<Double> newList=new LinkedList<>();


        for(double speed:speeds){
            sum+=speed;
        }

        System.out.println("Final: "+speeds.getLast());
        return sum/speeds.size();
    }
    public static double FastCom(LinkedList<Double> speeds){
        double sum=0.0;
        Collections.sort(speeds);
        LinkedList<Double> newList=new LinkedList<>();


        for(double speed:speeds){
            sum+=speed;
        }

        return sum/(speeds.size());
    }

}
