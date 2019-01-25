package hr.fer.rafael.speedtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    TextView pingView;
    TextView downloadView;
    TextView uploadView;
    TextView centralView;
    int i=0,n=10240,h=0,countzeros=0;
    byte[] buff=new byte[n];
    byte[] abuf=new byte[n];
    long start,cost,d;
    double speed=0,avg=0;
    ArrayList<Double> download=new ArrayList<>();

    FileOutputStream fos=null;
    String upload=null;
    String fileInputString="input123";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton=(Button) findViewById(R.id.button);
        pingView=(TextView) findViewById(R.id.ping);
        downloadView=(TextView) findViewById(R.id.download);
        uploadView=(TextView) findViewById(R.id.upload);
        centralView=(TextView) findViewById(R.id.centralView);

    }

    public void SpeedTest(View view) throws IOException {

        startButton.setEnabled(false);
        startButton.setText("Measuring");
        pingView.setText("0 ms");
        downloadView.setText("0 Mb/s");
        uploadView.setText("0 Mb/s");
        centralView.setText(" ");
        String number,temp;
        final String ipAddress="46.101.104.253";
        final int port=2222;
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        centralView.setText("0 ");
                    }
                });
                try{
                    Socket s=new Socket("46.101.104.253",5533);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Server connection established",Toast.LENGTH_SHORT).show();
                        }
                    });
                    DataInputStream input=new DataInputStream(s.getInputStream());
                    int num=100;
                    PrintStream p=new PrintStream(s.getOutputStream());
                    InetAddress inet=InetAddress.getByName(ipAddress);
                    final float pingavg=ping(inet,num);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pingView.setText(String.format("%.1f",pingavg)+" ms");
                        }
                    });
                    h=input.read(abuf,0,n);
                    start=System.currentTimeMillis();
                    while(System.currentTimeMillis()-start<5000){
                        d=System.currentTimeMillis();
                        h=input.read(buff,0,n);
                        cost=System.currentTimeMillis()-d;
                        i+=h;
                        final double momentSpeed=0.008*h/cost;
                        if(i==-1&&i>=20480000) break;
                        if(cost!=0){
                            if(Double.isInfinite(momentSpeed)){
                            }else if(Double.isNaN(momentSpeed)&&(momentSpeed)<=10){
                                continue;
                            }else {
                                download.add(momentSpeed);
                                if((System.currentTimeMillis()-start)%100==0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(cost!=0&&(momentSpeed)!=0&&!Double.isInfinite(momentSpeed)) {
                                                centralView.setText(String.format("%.1f", momentSpeed));
                                            }
                                        }
                                    });
                                }
                            }
                        }

                    }
                    download=speedMethod(download);

                    for(double d:download){
                        if(d==0)countzeros++;
                        avg+=d;
                    }
                    avg=avg/(download.size()-countzeros);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            centralView.setText(" ");
                            downloadView.setText(String.format("%.1f Mb/s",avg));
                        }
                    });
                    s.close();
                    Socket s1=new Socket("46.101.104.253",5534);
                    DataOutputStream dis=new DataOutputStream(new BufferedOutputStream(s1.getOutputStream()));
                    upload(dis,abuf);
                    dis.flush();
                    Socket s2=new Socket("46.101.104.253",5535);
                    Scanner sc=new Scanner(s2.getInputStream());
                    final String uploadSpeed=sc.nextLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadView.setText(uploadSpeed+" Mb/s");
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        centralView.setText("Press restart");
                        startButton.setText("Restart");
                        startButton.setEnabled(true);
                    }
                });
            }
        }).start();


    }


    static float ping(InetAddress inet,int number) throws IOException {
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
                avgping+=cost;
            }else {

            }
        }

        float pingavg=avgping/times.size();
        return pingavg;
    }

    static double download(DataInputStream dis,boolean method) throws IOException {
        int i=0;
        int n=10240;
        byte[] buff=new byte[n];
        long start,cost;
        ArrayList<Double> download=new ArrayList<>();
        while(n!=-1) {
            start=System.currentTimeMillis();
            if((n=dis.read(buff, 0, n))==-1) {
                break;
            }else {

                cost=System.currentTimeMillis()-start;
                if(cost!=0) {
                    download.add(0.008*n/cost);
                }
                i+=n;

            }
        }
        Log.i("  ",download.toString());
        if(method==true) {
            download=speedMethod(download);
        }
        double avg=0;
        for(double d:download) {
            avg+=d;
        }
        avg=avg/download.size();
        System.out.println(i);
        return avg;

    }

    static ArrayList<Double> speedMethod(ArrayList<Double> download){
        Collections.sort(download);
        int no=download.size();
        int i;
        for( i=0;i<no*0.3;i++) {
            download.remove(i);
        }
        no=(int)(no-no*0.3)-1;
        for(i=0;i<no*0.1;i++) {
            download.remove(no-i);
        }
        return download;

    }
    static void upload(DataOutputStream out, byte[] abuf) throws IOException {
        long start=System.currentTimeMillis();
        while(System.currentTimeMillis()-start<5000) {
            try{
                out.write(abuf,0,10240);
                out.flush();
                out.flush();
            }catch(Exception e){

            }

        }
    }
}
