package Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import FILE_Function.FILE_delete;
import FILE_Function.FILE_download;
import FILE_Function.FILE_refresh;
import FILE_Function.FILE_upload;
import FILE_Function.File_tool;
import Kerberos.kerberos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;


public class ClientFuction {
	public TextArea textarea;//ui界面文本框
	public TextArea textarea_1;
	public TextArea textarea_2;
	//public ProgressBar pb;
	
	private static Charset charset = Charset.forName("UTF-8"); //字符编码类型

	private  BigInteger[] pubkey = new BigInteger[2];//AS的公钥
	
	private static final String AS_IP = "192.168.43.208"; 
    private static final String TGS_IP = "192.168.43.244";
    private static final String V_IP = "192.168.43.49";
    private static final int AS_PORT = 8888;
    private static final int TGS_PORT = 7777;
    private static final int SERVER_PORT = 6666;
    
    static Socket socket = null;
    static DataOutputStream output = null;
    static DataInputStream input = null;
    public String Kcv = "";
	public ProgressBar pb;
    
    //private static final Logger log = LogManager.getLogger(BackgroundClient.class);
    
    //初始化用户进程
    public void init() throws UnknownHostException, IOException {
    	//连接应用服务器
    	Socket socket = new Socket(V_IP, SERVER_PORT);
    	output = new DataOutputStream(socket.getOutputStream());
		input = new DataInputStream(socket.getInputStream());
    
        pubkey[0]=new BigInteger("25210376174222502674597043575587868433359845921729777429392593376801244718803287391093663295857829689208086882496347648829616115541377572451813241929958704483188200039335156767743378800207443537960952226191738855785981692041259305829486869081914154060612653741010933367421479008336912956487753130634749042193278479439430114897156732011964917535436211234837825681273904534007141953227436227382619843005754349476204056079297744670168472659413655833978852141561891871277772065229804420765889528128945186167113014241572555603967433653284618158672392942089685732576941187113403406438710612351646692434368229939197562886053");
        pubkey[1]=new BigInteger("65537");
    }
    
    //kerberos认证
    public boolean verify(String ID_c, String K_c){
    	try {
        
    		//String ID_c = "22345678";      //用户id

    		String AD_c = "192.168.43.208";  //用户ip
    		//String K_c = "12345678";       //用户口令
        
    		kerberos kerberos = new kerberos();
        
    		//AS认证
    		Socket socket1 = new Socket(AS_IP, AS_PORT);
            DataOutputStream output1 = new DataOutputStream(socket1.getOutputStream());
            DataInputStream input1 = new DataInputStream(socket1.getInputStream());
    		String message1 = kerberos.Client_CToAS(ID_c, TGS_IP);
    		output1.writeUTF(message1);                 //发送消息
    		System.out.println("向AS发送认证报文："+message1);
    		textarea.appendText("client->as, client发送的认证报文: " + message1 + "\n");//在ui界面上显示
    		String receive1 = input1.readUTF();         //接受信息
    		
    		System.out.println("收到AS发来的密文："+receive1);
    		textarea.appendText("as->client, client收到as返回的加密报文: " + receive1 + "\n");//在ui界面上显示
    		String ps = kerberos.generateHash(K_c);   //口令的hash值
    		String []message2 = kerberos.Client_parseAS(ps, receive1); //解密报文
    		textarea.appendText("client解密出的as返回的报文: ");//在ui界面上显示
    		for(int i=0;i<message2.length;i++) {                       //输出
    			System.out.println(message2[i]);
    			textarea.appendText(message2[i] + "     ");//在ui界面上显示
    			
    		}
    		textarea.appendText("\n");//在ui界面上显示
    		String Kctgs = message2[0];                 //用于c与v会话的session key
            String Ticket_tgs = message2[4];            //tgs生成用于访问v服务器的票据
    		
    		//tgs认证
    		Socket socket2 = new Socket(TGS_IP, TGS_PORT);
            DataOutputStream output2 = new DataOutputStream(socket2.getOutputStream());
            DataInputStream input2 = new DataInputStream(socket2.getInputStream());
            String message3  = kerberos.CtoTGS(V_IP, Ticket_tgs, ID_c, AD_c, Kctgs);
            output2.writeUTF(message3);                 //发送消息
            System.out.println("向TGS发送认证报文："+message3);
            textarea.appendText("client->tgs, client发送的认证报文: " + message3 + "\n");//在ui界面上显示
            String receive2 = input2.readUTF();         //接受信息
            System.out.println("收到TGS发来的密文："+receive2);
            textarea.appendText("tgs->client, client收到tgs返回的加密报文: " + receive2 + "\n");//在ui界面上显示
            String []message4 = kerberos.GetTGStoC(receive2, Kctgs);
            textarea.appendText("client解密出的tgs返回的报文: ");//在ui界面上显示
            for(int i=0;i<message4.length;i++) {       //输出
    			System.out.println(message4[i]);
    			textarea.appendText(message4[i] + "     ");//在ui界面上显示
    		}
            textarea.appendText("\n");//在ui界面上显示
            Kcv = message4[0];                 //用于c与v会话的session key
            String Ticket_v = message4[3];            //tgs生成用于访问v服务器的票据
            
            //v认证
            String message5 = kerberos.CtoV(ID_c, Kcv, AD_c, Ticket_v);
            output.writeUTF(message5);                 //发送消息
            System.out.println("向V发送认证报文："+message5);
            textarea.appendText("client->v, client发送的认证报文: " + message5 + "\n");//在ui界面上显示
            String receive3 = input.readUTF();         //接受信息
            System.out.println("收到V发来的密文："+receive3);
            textarea.appendText("v-client, client收到v返回的加密报文: " + receive3 + "\n");//在ui界面上显示
            String []message6 = kerberos.GetVtoC(receive3, Kcv);
            textarea.appendText("client解密出的v返回的报文: ");//在ui界面上显示
            for(int i=0;i<message6.length;i++) {       //输出
    			System.out.println(message6[i]);
    			textarea.appendText(message6[i] + "   ");//在ui界面上显示
    		}
            textarea.appendText("\n");//在ui界面上显示
            System.out.println("认证成功！");
            System.out.println("密钥Kcv: " + Kcv);
    		
    	} catch (IOException e) {
    		e.printStackTrace();
        }finally {
        	
        }
    
    	return true;
    	
    }
    
    //注册
    public boolean register(String ID_c, String K_c){
    	try {
    		Socket socket1 = new Socket(AS_IP, AS_PORT);
            DataOutputStream output1 = new DataOutputStream(socket1.getOutputStream());
            DataInputStream input1 = new DataInputStream(socket1.getInputStream());
        
    		//String ID_c = "22345678";      //用户id
    		//String K_c = "12345678";       //用户口令
        
    		kerberos kerberos = new kerberos();
    		String message1 = kerberos.CtoAS(ID_c, K_c);
    		output1.writeUTF(message1);//发送消息
    		System.out.println("向AS发送注册报文："+message1);
    		textarea.appendText("client向AS发送的注册报文: " + message1 + "\n");//在ui界面上显示
    		String receive1 = input1.readUTF();  //接受信息
    		System.out.println("收到AS发来的密文："+receive1);
    		textarea.appendText("client收到AS发来的密文: " + receive1 + "\n");//在ui界面上显示
    		String []message2 = kerberos.GetAStoC(receive1);
    		textarea.appendText("client解析出来密文: ");//在ui界面上显示
    		for(int i=0;i<message2.length;i++) {
    			System.out.println(message2[i]);
    			textarea.appendText(message2[i] + "    ");//在ui界面上显示
    		}
    		textarea.appendText("\n");//在ui界面上显示
    	} catch (IOException e) {
    		e.printStackTrace();
        } 	
        return true;
    }
    
    //上传文件
    /*public boolean uploadfile (String filename) {
    	try {
    		FILE_upload file = new FILE_upload();
    		//filefunction file = new filefunction();
    		String[] message1 = file.Client_generateFileUpload_Package(filename, Kcv); //生成文件数据包组
    		textarea_1.appendText("client向v上传文件发送的包: ");//在ui界面上显示
    		for(int i=0;i<message1.length;i++) {
    			output.writeUTF(message1[i]);                                          //循环发送文件数据包
    			System.out.println("向v发送上传文件数据包："+message1[i]);
    			textarea_1.appendText("message1[i]" + "   ");//在ui界面上显示
    			
    			double p;
    			p = (double)i / (double)message1.length;
    			pb.setProgress(p);//ui界面进度条的显示
    		}
    		textarea_1.appendText("\n");//在ui界面上显示
    		String receive1 = input.readUTF();                                         //读取V发来的结果响应包
    		System.out.println("获取V发来的上传文件结果数据包："+receive1);
    		textarea_1.appendText("client收到v发来的响应数据包: " + receive1 + "\n");//在ui界面上显示
    		String result = file.Client_parseFileUpload_Result_Package(receive1, Kcv);               //解包函数
    		System.out.println("上传文件结果："+result);
    		textarea_2.appendText("client收到v发来的响应数据包结果: " + result + "\n");//在ui界面上显示
    		
    	} catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	
    	return true;
    }*/
    
  //上传文件
    public boolean uploadfile1 (String filename) throws InterruptedException {
    	try {
    		FILE_upload file = new FILE_upload();
    		File_tool tool = new File_tool();
    		String message1 = file.Client_generateFileUpload_Package1(filename, Kcv);   		
    		output.writeUTF(message1);
    		System.out.println("发送数据包1："+message1);
    		
    		String message2 = file.Client_generateFileUpload_Package2(filename, Kcv);
    		output.writeUTF(message2);
    		System.out.println("发送数据包2："+message2);
    		textarea_1.appendText("client向v发送的文件类型包: " + message2 + "\n");//在ui界面上显示
    		byte[][]filedata = file.Client_generateFileUpload_Package3(filename, Kcv);
    		textarea_1.appendText("client向v发送的文件数据包: ");//在ui界面上显示
    		for(int i=0;i<filedata.length;i++) {
    			output.write(filedata[i]);
    			System.out.println("发送文件数据包："+filedata[i]);
    			if(i <= 10) {
    				textarea_1.appendText("filedata[i]" + "   ");//在ui界面上显示,只显示前十个数据包
    			}
    			
    			Thread.sleep(400);
    			System.out.println(i);
    			pb.setProgress(0.5);
    		}
    		textarea_1.appendText("\n");//在ui界面上显示
    		String receive = input.readUTF();
    		System.out.println("获取V发来的上传文件结果数据包："+receive);
    		textarea_1.appendText("获取V发来的上传文件结果数据包：" + receive+ "\n");//在ui界面上显示
    		String result = file.Client_parseFileUpload_Result_Package(receive, Kcv);
    		System.out.println("上传文件结果："+result);
    		textarea_2.appendText("获取V发来的上传文件结果数据包：" + receive+ "\n");//在ui界面上显示
    		
    	} catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	
    	return true;
    }
    
    //下载文件
   /* public boolean downloadfile(String filename) {
    	try {
    		FILE_download file = new FILE_download();
    		//filefunction file = new filefunction();
    		String message1 = file.Client_Filedownload(filename, Kcv);
    		output.writeUTF(message1); 
    		System.out.println("向v发送下载文件请求包："+message1);
    		textarea_1.appendText("client向v发送的请求文件包: " + message1 + "\n");//在ui界面上显示
    		String receive1 = input.readUTF();
    		System.out.println("收到服务端发来的文件数据包："+ receive1);
    		textarea_1.appendText("client收到v发来的响应数据包: " + receive1 + "   ");//在ui界面上显示
    		int length = file.V_getPackageNum(receive1, Kcv);
    		String[] filedata = new String[length];
    		filedata[0]= file.CLient_getfiledata(receive1, Kcv)[3];
    		//textarea_1.appendText("client收到v发来的包含文件的数据包: ");//在ui界面上显示
    		for(int i=1;i<length;i++) {
    			String receive2 = input.readUTF();
    			System.out.println("收到服务端发来的文件数据包："+ receive2);
    			textarea_1.appendText(receive2 + "   ");//在ui界面上显示
    			filedata[i]= file.CLient_getfiledata(receive2, Kcv)[3];
    			
    			double p;
    			p = (double)i / (double)length;
    			pb.setProgress(p);//ui界面进度条的显示
    		}
    		textarea_1.appendText("\n");//在ui界面上显示
    		
    		File_tool t = new File_tool();
    		//test t = new test();
    		t.filemerge(filedata, "D:\\test\\"+filename);
    		textarea_1.appendText(filename + "文件已存储!" + "文件位置: D:\\test\\" + "\n");
    		
    	}catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	return true;
    }*/
    
    //下载文件
    public boolean downloadfile1(String filename) {
    	try {
    		FILE_download file = new FILE_download();
    		String message1 = file.Client_Filedownload(filename, Kcv);    	
    		output.writeUTF(message1); 
    		System.out.println("向v发送下载文件请求包："+message1);
    		textarea_1.appendText("client向v发送的请求下载文件包: " + message1 + "\n");//在ui界面上显示
    		String receive1 = input.readUTF();
    		System.out.println("收到服务端发来的文件信息包："+ receive1);
    		textarea_1.appendText("client收到服务端发来的文件信息: " + receive1 + "\n");//在ui界面上显示
    		String[] message2 = file.Client_getfilemore(receive1, Kcv);
    		
    		byte[]receive2 = new byte[1000];
    		int length = input.read(receive2);
    		byte[]temp = new byte[length];
    		System.arraycopy(receive2, 0, temp, 0, length);
    		byte[][] filedata = new byte[Integer.parseInt(message2[1])][length];
    		System.out.println("收到服务端发来的文件数据包："+ filedata[0]);
    		filedata[0] = file.Client_getfilebyte(temp, Kcv);
    		textarea_1.appendText("client收到v发来的包含文件的数据包: ");//在ui界面上显示
    		for(int i=1;i<Integer.parseInt(message2[1]);i++) {
    			length = input.read(receive2);
    			System.arraycopy(receive2, 0, temp, 0, length);
    			filedata[i] = file.Client_getfilebyte(temp, Kcv);
    			System.out.println("收到服务端发来的文件数据包："+ filedata[i]);
    			textarea_1.appendText(filedata[i] + "   ");//在ui界面上显示
    			//pb.setProgress(0.5);
    		}
    		textarea_1.appendText("\n");//在ui界面上显示
    		File_tool tool =new File_tool();
    		tool.filemerge_byte(filedata, "C:\\data\\"+filename);
    		textarea_2.appendText(filename + "文件已经存储，位置在：C:\\data\\");//在ui界面上显示
    		
    	}catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	return true;
    }
    
    //刷新目录(修改，添加返回值)
    public String[] getdirectory() {
    	try {
    		FILE_refresh file = new FILE_refresh();
    		System.out.println("密钥Kcv: " + this.Kcv);
    		//filefunction file = new filefunction();
    		String message1  = file.Client_refreshFile(Kcv);                  //生成目录请求包
    		System.out.println("c->v目录请求包："+message1);
    		textarea_1.appendText("client向服务器端发送的请求刷新目录包: " + message1 + "\n");//在ui界面上显示
    		output.writeUTF(message1);                                        //发送请求包
    		String receive1 = input.readUTF();                               //接受服务端发来的目录数据包
    		System.out.println("v->c目录数据包："+receive1);
    		textarea_1.appendText("client接收服务器端返回的包含文件列表的包: " + receive1 + "\n");//在ui界面上显示
    		String[] message2 = file.Client_parseServer(Kcv, receive1);       //解包目录数据包
    		textarea_2.appendText("client解密出的文件列表信息: ");
    		for(int i=0;i<message2.length;i++) {
    			System.out.println(message2[i]);
    			textarea_2.appendText(message2[i] + "   ");//在ui界面上显示
    		}
    		textarea_2.appendText("\n");//在ui界面上显示
    		return message2;
    		
    		
     	}catch (IOException e) {
    		e.printStackTrace();
        }
  
    	return null;
    }
    
    //删除文件
    public boolean deletefile(String filename) {
    	try {
    		FILE_delete file = new FILE_delete();
    		//filefunction file = new filefunction();
    		String message1 = file.Client_delete(filename, Kcv);                //生成加密请求包
    		System.out.println("c->v删除文件请求包："+message1);
    		textarea_1.appendText("c->v删除文件请求包: " + message1 + "\n");//在ui界面上显示
    		output.writeUTF(message1);                                          //发送删除请求包
    		String receive1 = input.readUTF();                                  //接受服务端发来的删除响应包
    		System.out.println("v->c删除结果包："+receive1);       
    		textarea_1.appendText("v->c删除文件结果包: " + receive1 + "\n");//在ui界面上显示
    		String message2 = file.Client_getdeleteconfirm(receive1, Kcv);     //解包目录数据包
    		textarea_2.appendText("v->c删除文件结果包: " + message2 + "\n");//在ui界面上显示
    		System.out.println(message2);
    		
    	}catch (IOException e) {
    		e.printStackTrace();
        } 
    	return true;
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException{
    	ClientFuction c = new ClientFuction();
        //c.init();
        //c.verify();
        //c.uploadfile("E:\\project\\eclipse\\File_Share\\png\\jba.txt");
        //c.downloadfile("jba.txt");
        //c.getdirectory();
        c.deletefile("jba.txt");
    }
    

}
