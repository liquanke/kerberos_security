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
                String receive = input.readUTF(); //��������

                
                System.out.println("AS ���յ� Client�ı���"+ receive);
                //log.info("AS ���յ� Client�ı���"+ receive);
                
                //����԰���ǰ��λ�����ж�
        		String packet_type=receive.substring(0,4);
        		System.out.print("packet_type:");
        		System.out.println(packet_type);
        		
        		
                if(packet_type.equals(USER_REGISTE))
                {
                	System.out.println("");
                	System.out.println("********************");
                	String status;
                	System.out.println("AS�յ�client����ע�������������Ϊ��");
                	String []data=kerberos_instance.GetCtoAS(receive);
                	//dataΪ����������ݣ�data[0]ΪIDc��data[1]ΪKc
            		for(int i=0;i<data.length;i++)
            		   System.out.println(data[i]);
            		
            		//���û��������HASH����
            		Hash hash=new Hash();
            		String Kc=hash.generateHash(data[1]);
            		String idc=data[0];
            		
            		//���������ݿ�Խ�
            		DB db = new DB();
            		db.getConnection();
                    
                    if(db.FindID(idc)==null) {
                    	db.Insert(idc,Kc);
                    	System.out.println("ע��ɹ�");
                    	status="11";//��ʾע��ɹ�
                    }
                    else {
                    	System.out.println("���û�id�Ѿ����ڣ�ע��ʧ��");
                    	status="00";//��ʾע��ʧ��
                    }
                    //���淢��ע��������client
                    String result2=kerberos_instance.AStoC(idc,status);
                    output.writeUTF(result2);//���Ͱ� 
                    System.out.println("********************");
                    System.out.println("");
                }
                
                else if(packet_type.equals(USER_VERIFY))
                {
                	System.out.println("");
                	System.out.println("********************");
                	System.out.println("AS�յ�client������֤������������Ϊ��");
                	String []data=kerberos_instance.AS_parseClient(receive);
                	//dataΪ����������ݣ�data[0]ΪIDc��data[1]ΪIDtgs,data[2]ΪTS1
            		for(int i=0;i<data.length;i++)
            		   System.out.println(data[i]);
     
            		//��֤������IDtgs��AS��֪��IDtgs�Ƿ�һ��
            		if(data[1].equals(ID_TGS))
            		{
            			System.out.println("AS����֤client�ɹ�");
            			String idc=data[0];
                		String k_c_tgs = kerberos_instance.create_sessionkey();//���������ڹ���Ҫ����Կ K_c_tgs
                		DB db = new DB();
                		db.getConnection();
                		String kc=db.FindID(idc);//�����ݿ��л�ȡ����
                		if(kc==null) {
                			System.out.println("�޴��û�");
                		}
                		
                		//��ȡclient��ip
                		InetAddress address_ip = socket.getInetAddress();
                		String ip_client=address_ip.getHostAddress();
                		
                		System.out.println("kc:"+kc);System.out.println("K_TGS:"+K_TGS);
                		System.out.println("k_c_tgs:"+k_c_tgs);System.out.println("idc:"+idc);
                		System.out.println("ip_client:"+ip_client);System.out.println("ID_TGS:"+ID_TGS);
                		
                		//���淢��ע��������client
                        String result3=kerberos_instance.asToClient(kc,K_TGS,k_c_tgs,idc,ip_client,ID_TGS);
                        output.writeUTF(result3);//���Ͱ�
            		}
            		System.out.println("********************");
            		System.out.println("");
                }
                
            }//while ����
            catch (IOException e) {
                e.printStackTrace();
            }
    }
}
