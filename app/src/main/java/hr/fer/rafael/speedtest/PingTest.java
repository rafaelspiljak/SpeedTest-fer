package hr.fer.rafael.speedtest;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class PingTest extends Thread{


    String server = "";

    double instantPing = 0;
    double averagePing = 0.0;
    boolean finished = false;
    boolean started = false;

    public PingTest(String serverIpAddress) {
        this.server = serverIpAddress;

    }

    public double getAveragePing() {
        return averagePing;
    }

    public double getInstantPing() {
        return instantPing;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        try {


            Socket s=new Socket("46.101.104.253",8080);
            String ipAddress="46.101.104.253";
            started=true;
            DataInputStream input=new DataInputStream(s.getInputStream());
            int num=100;
            PrintStream p=new PrintStream(s.getOutputStream());
            InetAddress inet= InetAddress.getByName(ipAddress);

            long a;
            long cost;
            float avgping=0;
            ArrayList<Long> times=new ArrayList<>();

            long d=System.currentTimeMillis();
            while(System.currentTimeMillis()-d<1000){
                a=System.currentTimeMillis();
                if(inet.isReachable(5000)) {
                    cost=System.currentTimeMillis()-a;
                    times.add(cost);
                    instantPing=cost;
                    avgping+=cost;
                }else {

                }
            }

            averagePing=avgping/times.size();


            s.close();
            finished=true;
        }catch(Exception e){

        }
    }

}
