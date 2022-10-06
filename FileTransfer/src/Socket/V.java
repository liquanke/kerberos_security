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
	private String FILE_PATH = "D:\\test\\file_disk\\";//本地磁盘路径
	String IF_VERIFY = "0000";//如果被验证，则设为1111
	String Kcv = "default0";//如果被验证，则更新为正确的kcv
	String Status = "default0";//如果上传有结果，则更新为正确的status
	//传输成功11  传输失败10  传输结束00
    private static String USER_VERIFY_V = "1007";//客户请求验证
    private static String UPFILE_CLIENT = "1009";//客户请求上传文件目录
    private static String DOWNFILE_CLIENT = "1011";//客户请求下载文件目录
    private static String REFRESH_CLIENT = "1015";//客户请求刷新文件目录
    private static String DELETE_CLIENT = "1012";//客户请求删除文件目录
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
            String receive = input.readUTF(); //接收数据
            
            System.out.println("V 接收到 Client的报文"+ receive);
            //log.info("TGS 接收到 Client的报文"+ receive);
            
            //下面对包的前四位进行判断
    		String packet_type=receive.substring(0,4);
    		System.out.print("packet_type:");
    		System.out.println(packet_type);
    		
    		System.out.println("packet_type:"+packet_type);
            System.out.println("IF_VERIFY:"+IF_VERIFY);
    		
    		//如果是client请求验证包
            if(packet_type.equals(USER_VERIFY_V))
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("********************");
            	IF_VERIFY="0000";//修改为默认值
            	System.out.println("收到用户请求验证V的包，包的内容为：");
            	String []data=kerberos_instance.GetCtoV(receive,K_TGS);
            	//data为解析后的数据
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
        		
        	    //验证信息是否一致
        		if(IDc_Tickettgs.equals(IDc_Authenticator)  &&  ADc_Tickettgs.equals(ADc_Authenticator)) {
        			System.out.println("V端信息验证成功");
        			System.out.println("k_c_v_Tickettgs:"+k_c_v_Tickettgs);
        			String result=kerberos_instance.VtoC(k_c_v_Tickettgs);
        			//下面发送注册结果包给client
                    output.writeUTF(result);//发送包
                    IF_VERIFY="1111";//验证后，设置为1111
                    Kcv=k_c_v_Tickettgs;
        		}
        		System.out.println("********************");
        		System.out.println("");
            }
            
            //如果是client发来文件上传请求包  且用户已经被验证
            else if(packet_type.equals(UPFILE_CLIENT)   &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("收到client请求上传文件的1号数据包，该包无实际内容，仅仅包含包类型");
            	
            	//下面接收包含文件数据的包
            	String receive_data2 = input.readUTF(); //接收数据包2,包含文件名，文件块编号，总块数
            	FILE_upload file_upload=new FILE_upload();
            	String []message = file_upload.Sever_parseFileUpload_Package2(receive_data2, Kcv);
            	System.out.println("收到client请求上传文件的2号数据包，包的内容为：");
            	String file_name=message[0];
            	int file_block_num=Integer.parseInt(message[1]);
            	System.out.println("文件名："+message[0]);
            	System.out.println("文件总块数："+message[1]);
            	//message[0]为文件名，message[1]为是文件总块数 
            	
            	//下面循环接收文件数据包
            	byte[] receive_data3=new byte[1000];
            	int receive3= input.read(receive_data3); //接收数据
            	byte[]temp = new byte[receive3];
            	System.arraycopy(receive_data3, 0, temp, 0, receive3);
            	byte[][] file_data=new byte[file_block_num][receive3];
            	file_data[0]=file_upload.Sever_parseFileUpload_Package3(temp, Kcv);//存储文件数据
            	
            	for(int i=1;i<file_block_num;i++) {
            		System.out.println(i);
            		receive3= input.read(receive_data3); //接收数据
                	System.arraycopy(receive_data3, 0, temp, 0, receive3);
            		file_data[i]=file_upload.Sever_parseFileUpload_Package3(temp, Kcv);//存储文件数据
            	}
            	
            	//下面合并文件数据包，生成文件
            	File_tool file_tool=new File_tool();
        		String file_name_1=FILE_PATH+message[0];
        		file_tool.filemerge_byte(file_data, file_name_1);
        		Status="11";
            	
            	//file_upload.Sever_parseFileUpload_Package2
        		//下面给client发送文件上传结果包
        		String upFileResult=file_upload.V_generateFileUpload_Result_Package(Kcv,Status);
        		//下面发送上传结果包给client
                output.writeUTF(upFileResult);//发送包
                System.out.println("用户："+address.getHostAddress()+"已经上传文件"+file_name+"到服务器");
                System.out.println("********************");
                System.out.println("");
            }
            
            //如果是client发来文件下载请求包  且用户已经被验证
            else if(packet_type.equals(DOWNFILE_CLIENT)  &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	
            	FILE_download file_download=new FILE_download();
            	String file_Name=file_download.Server_GetFiledownload(receive,Kcv);
            	System.out.println("收到client用户请求下载文件的包，包的内容为：");
            	System.out.println("file_name:"+file_Name);
        		   
        		//下面发送包含文件数据的包
            	file_Name=FILE_PATH+file_Name;
            	String message=file_download.Server_filemore(file_Name,Kcv);
            	output.writeUTF(message);//发送文件数据包
            	
            	//下面循环发送文件数据包
            	byte[][] send_data3=file_download.Server_filebyte(file_Name,Kcv);
            	int file_block_num=send_data3.length;
            	for(int i=0;i<file_block_num;i++) {
            		output.write(send_data3[i]);//发送文件数据包
            		Thread.sleep(300);
            	}
            	
            	System.out.println("文件"+file_Name+"已经发送给用户："+address.getHostAddress());
            	System.out.println("********************");
            	System.out.println("");
            }
            
          //如果是client发来文件刷新载请求包  且用户已经被验证
            else if(packet_type.equals(REFRESH_CLIENT)  &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("收到client用户请求刷新文件的包");
            	
            	InetAddress now = socket.getInetAddress();
            	
            	String IP = now.getHostAddress()+"";
            	
            	
            	FILE_refresh file_refresh=new FILE_refresh();
            	/*if(IP.equals("192.168.43.208")) {
            		String result=file_refresh.Server_toClient(Kcv,FILE_PATH);
            		output.writeUTF(result);//发送文件数据包
            		System.out.println("kcv = "+ Kcv);
                	System.out.println("目录："+FILE_PATH+"已经成功刷新");
                	System.out.println("********************");
                	System.out.println("");
            	}else {
            		String result=file_refresh.Server_toClient(Kcv,FILE_PATH1);
            		output.writeUTF(result);//发送文件数据包
            		System.out.println("kcv = "+ Kcv);
                	System.out.println("目录："+FILE_PATH+"已经成功刷新");
                	System.out.println("********************");
                	System.out.println("");
            	}*/
           
            	String result=file_refresh.Server_toClient(Kcv,FILE_PATH);
            	output.writeUTF(result);//发送文件数据包
            	System.out.println("目录："+FILE_PATH+"已经成功刷新");
            	System.out.println("********************");
            	System.out.println("");
            }
            
          //如果是client发来文件删除请求包  且用户已经被验证
            else if(packet_type.equals(DELETE_CLIENT)  &&IF_VERIFY.equals("1111") )
            {
            	System.out.println("");
            	System.out.println("********************");
            	System.out.println("收到client用户请求删除文件的包");
            	
            	FILE_delete file_delete=new FILE_delete();
            	String file_name=file_delete.Server_getdelete(receive, Kcv);
            	file_name=FILE_PATH+file_name;
            	File_tool file_tool=new File_tool();
            	boolean flag=file_tool.DeleteFolder(file_name);
            	
            	String result=file_delete.Server_deleteconfirm(flag, Kcv);
            	output.writeUTF(result);//发送文件数据包
            	if(flag==true) {
            		System.out.println("文件:"+file_name+"已经成功删除");
            	}
            	else {
            		System.out.println("文件:"+file_name+"删除失败");
            	}
            	System.out.println("********************");
            	System.out.println("");
            }
            
          }//while 结束
        } catch (
                IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}



