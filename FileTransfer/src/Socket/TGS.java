package Socket;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//import DB.DB;
//import DES.DES;
import Kerberos.kerberos;
//import RSA.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;import java.util.Date;

public class TGS {
    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(7777);
        System.out.println("TGS server start at:" + new Date());
        TGSthread a = new TGSthread();
        while (true) {
            Socket socket = serverSocket.accept();
            a.setSocket(socket);
            new Thread(a).start();
        }
    }
}

class TGSthread implements Runnable{
    private static String USER_VERIFY_TGS = "1005";
    private static String K_TGS= "default1";
    private static String EKv= "default2";
    Socket socket;
    //private static final Logger log = LogManager.getLogger(TGS_TEST.class);
    public void setSocket(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        try {
        	kerberos kerberos_instance = new kerberos();
            InetAddress address = socket.getInetAddress();
            System.out.println("connected with address:"+address.getHostAddress());
            //log.info("connected with address:"+address.getHostAddress());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String receive = input.readUTF(); //接收数据

            System.out.println("TGS 接收到 Client的报文"+ receive);
            //log.info("TGS 接收到 Client的报文"+ receive);
            
            //下面对包的前四位进行判断
    		String packet_type=receive.substring(0,4);
    		System.out.print("packet_type:");
    		System.out.println(packet_type);
    		
            if(packet_type.equals(USER_VERIFY_TGS))
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("收到client请求验证TGS包，包的内容为：");
            	String []data=kerberos_instance.GetCtoTGS(receive,K_TGS);
            	//data为解析后的数据
        		for(int i=0;i<data.length;i++)
        		   System.out.println(data[i]);
        		
        		String IDv=data[0];
        		String Kc_tgs_Tickettgs=data[1];
        		String IDc_Tickettgs=data[2];
        		String ADc_Tickettgs=data[3];
        		//String IDtgs_Tickettgs=data[4];
        		//String TS2_Tickettgs=data[5];
        		//String lifetime2_Tickettgs=data[6];
        		String IDc_Authenticator=data[7];
        		String ADc_Authenticator=data[8];
        		//String TS2_Authenticator=data[9];
        		
        	    //验证信息是否一致
        		if(IDc_Tickettgs.equals(IDc_Authenticator)  &&  ADc_Tickettgs.equals(ADc_Authenticator)) {
        			System.out.println("TGS端验证client成功");
        			String k_c_v = kerberos_instance.create_sessionkey();//当生命周期过后要换密钥 K_c_v
        			
        			System.out.println("k_c_v:"+k_c_v);System.out.println("Kc_tgs_Tickettgs:"+Kc_tgs_Tickettgs);
            		System.out.println("EKv:"+EKv);System.out.println("IDv:"+IDv);
            		System.out.println("IDc_Tickettgs:"+IDc_Tickettgs);System.out.println("ADc_Tickettgs:"+ADc_Tickettgs);
        			
        			String result=kerberos_instance.TGStoC(k_c_v,Kc_tgs_Tickettgs,EKv,IDv,IDc_Tickettgs,ADc_Tickettgs);
        			
        			//下面发送注册结果包给client
                    output.writeUTF(result);//发送包
                    System.out.println("********************");
                    System.out.println("");
        		}
            }//while 结束
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}



