package Socket;

import Kerberos.kerberos;
import FILE_Function.FILE_delete;
import FILE_Function.FILE_download;
import FILE_Function.FILE_download;
import FILE_Function.FILE_refresh;
import FILE_Function.FILE_upload;
import FILE_Function.FILE_upload;
import FILE_Function.File_tool;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;import java.util.Date;

public class V {
    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("V server start at:" + new Date());
       // V_thread a = new V_thread();
        while (true) {
        	V_thread a = new V_thread();
            Socket socket = serverSocket.accept();
            a.setSocket(socket);
            new Thread(a).start();
        }
    }
}

class V_thread implements Runnable{
	private String FILE_PATH = "D:\\test\\file_disk\\";//���ش���·��
	String IF_VERIFY = "0000";//�������֤������Ϊ1111
	String Kcv = "default0";//�������֤�������Ϊ��ȷ��kcv
	String Status = "default0";//����ϴ��н���������Ϊ��ȷ��status
	//����ɹ�11  ����ʧ��10  �������00
    private static String USER_VERIFY_V = "1007";//�ͻ�������֤
    private static String UPFILE_CLIENT = "1009";//�ͻ������ϴ��ļ�Ŀ¼
    private static String DOWNFILE_CLIENT = "1011";//�ͻ����������ļ�Ŀ¼
    private static String REFRESH_CLIENT = "1015";//�ͻ�����ˢ���ļ�Ŀ¼
    private static String DELETE_CLIENT = "1012";//�ͻ�����ɾ���ļ�Ŀ¼
    private static String K_TGS= "default1";
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
            while(true) {
            String receive = input.readUTF(); //��������
            
            System.out.println("V ���յ� Client�ı���"+ receive);
            //log.info("TGS ���յ� Client�ı���"+ receive);
            
            //����԰���ǰ��λ�����ж�
    		String packet_type=receive.substring(0,4);
    		System.out.print("packet_type:");
    		System.out.println(packet_type);
    		
    		System.out.println("packet_type:"+packet_type);
            System.out.println("IF_VERIFY:"+IF_VERIFY);
    		
    		//�����client������֤��
            if(packet_type.equals(USER_VERIFY_V))
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("********************");
            	IF_VERIFY="0000";//�޸�ΪĬ��ֵ
            	System.out.println("�յ��û�������֤V�İ�����������Ϊ��");
            	String []data=kerberos_instance.GetCtoV(receive,K_TGS);
            	//dataΪ�����������
        		for(int i=0;i<data.length;i++)
        		   System.out.println(data[i]);
        		
        		String k_c_v_Tickettgs=data[0];
        		String IDc_Tickettgs=data[1];
        		String ADc_Tickettgs=data[2];
        		//String IDv_Tickettgs=data[3];
        		//String TS4_Tickettgs=data[4];
        		//String Lifetime4_Tickettgs=data[5];
        		String IDc_Authenticator=data[6];
        		String ADc_Authenticator=data[7];
        		//String TS5_Authenticator=data[8];
        		
        	    //��֤��Ϣ�Ƿ�һ��
        		if(IDc_Tickettgs.equals(IDc_Authenticator)  &&  ADc_Tickettgs.equals(ADc_Authenticator)) {
        			System.out.println("V����Ϣ��֤�ɹ�");
        			System.out.println("k_c_v_Tickettgs:"+k_c_v_Tickettgs);
        			String result=kerberos_instance.VtoC(k_c_v_Tickettgs);
        			//���淢��ע��������client
                    output.writeUTF(result);//���Ͱ�
                    IF_VERIFY="1111";//��֤������Ϊ1111
                    Kcv=k_c_v_Tickettgs;
        		}
        		System.out.println("********************");
        		System.out.println("");
            }
            
            //�����client�����ļ��ϴ������  ���û��Ѿ�����֤
            else if(packet_type.equals(UPFILE_CLIENT)   &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("�յ�client�����ϴ��ļ���1�����ݰ����ð���ʵ�����ݣ���������������");
            	
            	//������հ����ļ����ݵİ�
            	String receive_data2 = input.readUTF(); //�������ݰ�2,�����ļ������ļ����ţ��ܿ���
            	FILE_upload file_upload=new FILE_upload();
            	String []message = file_upload.Sever_parseFileUpload_Package2(receive_data2, Kcv);
            	System.out.println("�յ�client�����ϴ��ļ���2�����ݰ�����������Ϊ��");
            	String file_name=message[0];
            	int file_block_num=Integer.parseInt(message[1]);
            	System.out.println("�ļ�����"+message[0]);
            	System.out.println("�ļ��ܿ�����"+message[1]);
            	//message[0]Ϊ�ļ�����message[1]Ϊ���ļ��ܿ��� 
            	
            	//����ѭ�������ļ����ݰ�
            	byte[] receive_data3=new byte[1000];
            	int receive3= input.read(receive_data3); //��������
            	byte[]temp = new byte[receive3];
            	System.arraycopy(receive_data3, 0, temp, 0, receive3);
            	byte[][] file_data=new byte[file_block_num][receive3];
            	file_data[0]=file_upload.Sever_parseFileUpload_Package3(temp, Kcv);//�洢�ļ�����
            	
            	for(int i=1;i<file_block_num;i++) {
            		System.out.println(i);
            		receive3= input.read(receive_data3); //��������
                	System.arraycopy(receive_data3, 0, temp, 0, receive3);
            		file_data[i]=file_upload.Sever_parseFileUpload_Package3(temp, Kcv);//�洢�ļ�����
            	}
            	
            	//����ϲ��ļ����ݰ��������ļ�
            	File_tool file_tool=new File_tool();
        		String file_name_1=FILE_PATH+message[0];
        		file_tool.filemerge_byte(file_data, file_name_1);
        		Status="11";
            	
            	//file_upload.Sever_parseFileUpload_Package2
        		//�����client�����ļ��ϴ������
        		String upFileResult=file_upload.V_generateFileUpload_Result_Package(Kcv,Status);
        		//���淢���ϴ��������client
                output.writeUTF(upFileResult);//���Ͱ�
                System.out.println("�û���"+address.getHostAddress()+"�Ѿ��ϴ��ļ�"+file_name+"��������");
                System.out.println("********************");
                System.out.println("");
            }
            
            //�����client�����ļ����������  ���û��Ѿ�����֤
            else if(packet_type.equals(DOWNFILE_CLIENT)  &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	
            	FILE_download file_download=new FILE_download();
            	String file_Name=file_download.Server_GetFiledownload(receive,Kcv);
            	System.out.println("�յ�client�û����������ļ��İ�����������Ϊ��");
            	System.out.println("file_name:"+file_Name);
        		   
        		//���淢�Ͱ����ļ����ݵİ�
            	file_Name=FILE_PATH+file_Name;
            	String message=file_download.Server_filemore(file_Name,Kcv);
            	output.writeUTF(message);//�����ļ����ݰ�
            	
            	//����ѭ�������ļ����ݰ�
            	byte[][] send_data3=file_download.Server_filebyte(file_Name,Kcv);
            	int file_block_num=send_data3.length;
            	for(int i=0;i<file_block_num;i++) {
            		output.write(send_data3[i]);//�����ļ����ݰ�
            		Thread.sleep(300);
            	}
            	
            	System.out.println("�ļ�"+file_Name+"�Ѿ����͸��û���"+address.getHostAddress());
            	System.out.println("********************");
            	System.out.println("");
            }
            
          //�����client�����ļ�ˢ���������  ���û��Ѿ�����֤
            else if(packet_type.equals(REFRESH_CLIENT)  &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("�յ�client�û�����ˢ���ļ��İ�");
            	
            	InetAddress now = socket.getInetAddress();
            	
            	String IP = now.getHostAddress()+"";
            	
            	
            	FILE_refresh file_refresh=new FILE_refresh();
            	/*if(IP.equals("192.168.43.208")) {
            		String result=file_refresh.Server_toClient(Kcv,FILE_PATH);
            		output.writeUTF(result);//�����ļ����ݰ�
            		System.out.println("kcv = "+ Kcv);
                	System.out.println("Ŀ¼��"+FILE_PATH+"�Ѿ��ɹ�ˢ��");
                	System.out.println("********************");
                	System.out.println("");
            	}else {
            		String result=file_refresh.Server_toClient(Kcv,FILE_PATH1);
            		output.writeUTF(result);//�����ļ����ݰ�
            		System.out.println("kcv = "+ Kcv);
                	System.out.println("Ŀ¼��"+FILE_PATH+"�Ѿ��ɹ�ˢ��");
                	System.out.println("********************");
                	System.out.println("");
            	}*/
           
            	String result=file_refresh.Server_toClient(Kcv,FILE_PATH);
            	output.writeUTF(result);//�����ļ����ݰ�
            	System.out.println("Ŀ¼��"+FILE_PATH+"�Ѿ��ɹ�ˢ��");
            	System.out.println("********************");
            	System.out.println("");
            }
            
          //�����client�����ļ�ɾ�������  ���û��Ѿ�����֤
            else if(packet_type.equals(DELETE_CLIENT)  &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("�յ�client�û�����ɾ���ļ��İ�");
            	
            	FILE_delete file_delete=new FILE_delete();
            	String file_name=file_delete.Server_getdelete(receive, Kcv);
            	file_name=FILE_PATH+file_name;
            	File_tool file_tool=new File_tool();
            	boolean flag=file_tool.DeleteFolder(file_name);
            	
            	String result=file_delete.Server_deleteconfirm(flag, Kcv);
            	output.writeUTF(result);//�����ļ����ݰ�
            	if(flag==true) {
            		System.out.println("�ļ�:"+file_name+"�Ѿ��ɹ�ɾ��");
            	}
            	else {
            		System.out.println("�ļ�:"+file_name+"ɾ��ʧ��");
            	}
            	System.out.println("********************");
            	System.out.println("");
            }
            
          }//while ����
        } catch (
                IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}



