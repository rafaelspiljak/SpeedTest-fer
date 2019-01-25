import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.xml.bind.DataBindingException;

public class Server {

        public static void main(String[] args) throws IOException {
                ServerSocket ss=new ServerSocket(5533);
                ServerSocket ss1=new ServerSocket(5534);
                ServerSocket ss2=new ServerSocket(5535);
				Socket sr=null;
				Socket sr1=null;
				Socket sr2=null;
				File f=new File("/home/file.txt");
                byte[] buff=new byte[10240];
                int n=10240;
                while(true){
					FileInputStream fis=new FileInputStream(f);
					sr=ss.accept();
					System.out.println("connected");
					DataOutputStream dos= new DataOutputStream(new BufferedOutputStream(sr.getOutputStream()));
					download(fis, dos);
					sr.close();
					sr1=ss1.accept();
					System.out.println("conn2");
					Scanner sc=new Scanner(sr1.getInputStream());
					DataInputStream dis=new DataInputStream(new BufferedInputStream(sr1.getInputStream()));
					int i=0;
					double upload=upload(dis,false);
					System.out.println(upload);
					sr1.close();
					sr2=ss2.accept();
					PrintStream print=new PrintStream(sr2.getOutputStream());
					print.println(String.format("%.1f",upload));
					print.flush();
					print.flush();
					sr2.close();
				}
        }

        static void download(FileInputStream fis,DataOutputStream dos) throws IOException {
                int i=0,n=10240;
                byte[] buff=new byte[n];
                n=fis.read(buff,0,n);
                long start=System.currentTimeMillis();
                while(System.currentTimeMillis()-start<5000) {
                        try{
                                dos.write(buff, 0, n);
                                dos.flush();
                        }catch(Exception e) {

                        }

                }
                dos.flush();
                fis.close();
                dos.close();
        }
        static double upload(DataInputStream dis, boolean method) throws IOException {
                int i=0;
                int n=10240,h;
                byte[] buff=new byte[n];
                long start,cost,abc;
                ArrayList<Double> download=new ArrayList<>();
                start=System.currentTimeMillis();
                while(System.currentTimeMillis()-start<5000) {
                        h=dis.read(buff, 0, n);
						i+=h;
                        if(i==-1&&i>=10000000)break;
                        cost=System.currentTimeMillis()-start;
                                if(cost!=0) {

                                        download.add(0.008*i/cost);
                                }
                }
                if(method==true) {
                        download=speedMethod(download);
                }
                double avg=0;
                for(double d:download) {
                        avg+=d;
                }
                avg=avg/download.size();
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
}
