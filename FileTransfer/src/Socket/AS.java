package Socket;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;

import DB.DB;
//import RSA.RSA;
import Kerberos.kerberos;
import Kerberos.Hash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;import java.util.Date;

public class AS{
    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("AS server start at:" + new Date());
        Mythread a = new Mythread();
        while (true) {
            Socket socket = serverSocket.accept();
            a.setSocket(socket);
            new Thread(a).start();
        }
    }
}

class Mythread  implements Runnable{
    private static String USER_REGISTE = "1001";
    private static String USER_VERIFY = "1003";
    private static String ID_TGS= "192.168.43.244";
    private static String K_TGS= "default1";
    Socket socket;
    //private static final Logger log = LogManager.getLogger(AS.class);
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

                
                System.out.println("AS 接收到 Client的报文"+ receive);
                //log.info("AS 接收到 Client的报文"+ receive);
                
                //下面对包的前四位进行判断
        		String packet_type=receive.substring(0,4);
        		System.out.print("packet_type:");
        		System.out.println(packet_type);
        		
        		
                if(packet_type.equals(USER_REGISTE))
                {
                	System.out.println("");
                	System.out.println("********************");
                	String status;
                	System.out.println("AS收到client请求注册包，包的内容为：");
                	String []data=kerberos_instance.GetCtoAS(receive);
                	//data为解析后的数据，data[0]为IDc，data[1]为Kc
            		for(int i=0;i<data.length;i++)
            		   System.out.println(data[i]);
            		
            		//将用户口令进行HASH处理
            		Hash hash=new Hash();
            		String Kc=hash.generateHash(data[1]);
            		String idc=data[0];
            		
            		//下面与数据库对接
            		DB db = new DB();
            		db.getConnection();
                    
                    if(db.FindID(idc)==null) {
                    	db.Insert(idc,Kc);
                    	System.out.println("注册成功");
                    	status="11";//表示注册成功
                    }
                    else {
                    	System.out.println("该用户id已经存在，注册失败");
                    	status="00";//表示注册失败
                    }
                    //下面发送注册结果包给client
                    String result2=kerberos_instance.AStoC(idc,status);
                    output.writeUTF(result2);//发送包 
                    System.out.println("********************");
                    System.out.println("");
                }
                
                else if(packet_type.equals(USER_VERIFY))
                {
                	System.out.println("");
                	System.out.println("********************");
                	System.out.println("AS收到client请求认证包，包的内容为：");
                	String []data=kerberos_instance.AS_parseClient(receive);
                	//data为解析后的数据，data[0]为IDc，data[1]为IDtgs,data[2]为TS1
            		for(int i=0;i<data.length;i++)
            		   System.out.println(data[i]);
     
            		//验证解析的IDtgs与AS已知的IDtgs是否一致
            		if(data[1].equals(ID_TGS))
            		{
            			System.out.println("AS端验证client成功");
            			String idc=data[0];
                		String k_c_tgs = kerberos_instance.create_sessionkey();//当生命周期过后要换密钥 K_c_tgs
                		DB db = new DB();
                		db.getConnection();
                		String kc=db.FindID(idc);//从数据库中获取密码
                		if(kc==null) {
                			System.out.println("无此用户");
                		}
                		
                		//获取client的ip
                		InetAddress address_ip = socket.getInetAddress();
                		String ip_client=address_ip.getHostAddress();
                		
                		System.out.println("kc:"+kc);System.out.println("K_TGS:"+K_TGS);
                		System.out.println("k_c_tgs:"+k_c_tgs);System.out.println("idc:"+idc);
                		System.out.println("ip_client:"+ip_client);System.out.println("ID_TGS:"+ID_TGS);
                		
                		//下面发送注册结果包给client
                        String result3=kerberos_instance.asToClient(kc,K_TGS,k_c_tgs,idc,ip_client,ID_TGS);
                        output.writeUTF(result3);//发送包
            		}
            		System.out.println("********************");
            		System.out.println("");
                }
                
            }//while 结束
            catch (IOException e) {
                e.printStackTrace();
            }
    }
}
