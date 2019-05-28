package hr.fer.rafael.speedtest;

import android.content.SyncStatusObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    FileInputStream fis;
    InputStream is;
    Button startButton;
    TextView pingView;
    TextView downloadView;
    TextView uploadView;
    TextView centralView;
    Spinner spinner;
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
        spinner=(Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerList=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_expandable_list_item_1,getResources().getStringArray(R.array.methods));
        spinnerList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerList);

    }

    public void SpeedTest(View view) throws IOException {

        startButton.setEnabled(false);
        startButton.setText("Measuring");
        pingView.setText("0 ms");
        downloadView.setText("0 Mbps");
        uploadView.setText("0 Mbps");
        centralView.setText(" ");
        String number,temp;
        spinner.getSelectedItem().toString();

        try{
            is=getResources().openRawResource(R.raw.file);
        }catch(Exception e){
            e.printStackTrace();
        }

        final String ipAddress="46.101.104.253";
        final int port=8080;
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

                    int method=0;
                    String methodChosen=spinner.getSelectedItem().toString();
                    if(methodChosen.equals("Ookla")){
                        method=0;
                    }else if(methodChosen.equals("SpeedOfMe")){
                        method=1;
                    }else if(methodChosen.equals("SaveDataMethod")){
                        method=2;
                    }
                    final DownloadTest download=new DownloadTest(method);
                    final PingTest ping=new PingTest(ipAddress);
                    final UploadTest upload=new UploadTest(is,method);
                    boolean pingStarted=false;
                    boolean downloadStarted=false;
                    boolean uploadStarted=false;
                    boolean pingFinished=false;
                    boolean downloadFinished=false;
                    boolean uploadFinished=false;

                    while(true){
                        if(!pingStarted&&!downloadStarted&&!uploadStarted){
                            ping.start();
                            pingStarted=true;
                        }
                        if(pingFinished&&!downloadStarted&!uploadStarted){
                            download.start();
                            downloadStarted=true;
                        }
                        if(pingFinished&&downloadFinished&&!uploadStarted){
                            //upload.start();
                            uploadStarted=true;
                        }

                        /*pingtest*/
                        if (pingStarted) {
                            if(ping.averagePing==0.0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(getApplicationContext(),"Server is not available",Toast.LENGTH_SHORT);

                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pingView.setText(String.format("%.0f",ping.getAveragePing())+" ms");
                                    }
                                });
                            }
                        }else{
                            centralView.setText(String.format("%.0f",ping.getInstantPing())+" ms");
                        }
                        /*downloadtest*/
                        if(pingFinished&&!uploadFinished){
                            System.out.println(download.finalDownloadRate);
                            if (downloadFinished&&!uploadFinished){
                                if (download.getFinalDownloadRate()==0.0){
                                    System.out.println("Error while downloading");
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadView.setText(String.format("%.2f",download.getFinalDownloadRate())+" Mbps");
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        centralView.setText(String.format("%.2f",download.getInstantDownloadRate())+" Mbps");
                                    }
                                });
                            }

                        }

                        if(pingFinished&&downloadFinished){
                            if(uploadFinished){
                                if(upload.getFinalUploadRate()==0){
                                    System.out.println("Upload error");
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            uploadView.setText(String.format("%.2f",upload.getFinalUploadRate())+" Mbps");
                                        }
                                    });
                                }
                                }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        centralView.setText(String.format("%.2f",upload.getInstantUploadRate())+" Mbps");
                                    }
                                });
                            }
                            }

                        if(pingFinished&&downloadFinished){
                            break;
                        }
                        if(ping.isFinished()&&!downloadFinished&&!uploadFinished){



                                pingFinished=true;

                        }
                        if(download.isFinished()&&pingFinished&&!uploadFinished){

                            try{
                                Thread.sleep(2000);
                                downloadFinished=true;
                            }catch (Exception e){

                            }
                        }
                        /*if(upload.isFinished()&&pingFinished&&downloadFinished){

                            try{
                                Thread.sleep(4000);
                                uploadFinished=true;
                            }catch(Exception e){

                            }
                        }*/
                    }


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


}


