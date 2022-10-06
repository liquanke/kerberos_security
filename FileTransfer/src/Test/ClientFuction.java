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
	public TextArea textarea;//ui�����ı���
	public TextArea textarea_1;
	public TextArea textarea_2;
	//public ProgressBar pb;
	
	private static Charset charset = Charset.forName("UTF-8"); //�ַ���������

	private  BigInteger[] pubkey = new BigInteger[2];//AS�Ĺ�Կ
	
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
    
    //��ʼ���û�����
    public void init() throws UnknownHostException, IOException {
    	//����Ӧ�÷�����
    	Socket socket = new Socket(V_IP, SERVER_PORT);
    	output = new DataOutputStream(socket.getOutputStream());
		input = new DataInputStream(socket.getInputStream());
    
        pubkey[0]=new BigInteger("25210376174222502674597043575587868433359845921729777429392593376801244718803287391093663295857829689208086882496347648829616115541377572451813241929958704483188200039335156767743378800207443537960952226191738855785981692041259305829486869081914154060612653741010933367421479008336912956487753130634749042193278479439430114897156732011964917535436211234837825681273904534007141953227436227382619843005754349476204056079297744670168472659413655833978852141561891871277772065229804420765889528128945186167113014241572555603967433653284618158672392942089685732576941187113403406438710612351646692434368229939197562886053");
        pubkey[1]=new BigInteger("65537");
    }
    
    //kerberos��֤
    public boolean verify(String ID_c, String K_c){
    	try {
        
    		//String ID_c = "22345678";      //�û�id

    		String AD_c = "192.168.43.208";  //�û�ip
    		//String K_c = "12345678";       //�û�����
        
    		kerberos kerberos = new kerberos();
        
    		//AS��֤
    		Socket socket1 = new Socket(AS_IP, AS_PORT);
            DataOutputStream output1 = new DataOutputStream(socket1.getOutputStream());
            DataInputStream input1 = new DataInputStream(socket1.getInputStream());
    		String message1 = kerberos.Client_CToAS(ID_c, TGS_IP);
    		output1.writeUTF(message1);                 //������Ϣ
    		System.out.println("��AS������֤���ģ�"+message1);
    		textarea.appendText("client->as, client���͵���֤����: " + message1 + "\n");//��ui��������ʾ
    		String receive1 = input1.readUTF();         //������Ϣ
    		
    		System.out.println("�յ�AS���������ģ�"+receive1);
    		textarea.appendText("as->client, client�յ�as���صļ��ܱ���: " + receive1 + "\n");//��ui��������ʾ
    		String ps = kerberos.generateHash(K_c);   //�����hashֵ
    		String []message2 = kerberos.Client_parseAS(ps, receive1); //���ܱ���
    		textarea.appendText("client���ܳ���as���صı���: ");//��ui��������ʾ
    		for(int i=0;i<message2.length;i++) {                       //���
    			System.out.println(message2[i]);
    			textarea.appendText(message2[i] + "     ");//��ui��������ʾ
    			
    		}
    		textarea.appendText("\n");//��ui��������ʾ
    		String Kctgs = message2[0];                 //����c��v�Ự��session key
            String Ticket_tgs = message2[4];            //tgs�������ڷ���v��������Ʊ��
    		
    		//tgs��֤
    		Socket socket2 = new Socket(TGS_IP, TGS_PORT);
            DataOutputStream output2 = new DataOutputStream(socket2.getOutputStream());
            DataInputStream input2 = new DataInputStream(socket2.getInputStream());
            String message3  = kerberos.CtoTGS(V_IP, Ticket_tgs, ID_c, AD_c, Kctgs);
            output2.writeUTF(message3);                 //������Ϣ
            System.out.println("��TGS������֤���ģ�"+message3);
            textarea.appendText("client->tgs, client���͵���֤����: " + message3 + "\n");//��ui��������ʾ
            String receive2 = input2.readUTF();         //������Ϣ
            System.out.println("�յ�TGS���������ģ�"+receive2);
            textarea.appendText("tgs->client, client�յ�tgs���صļ��ܱ���: " + receive2 + "\n");//��ui��������ʾ
            String []message4 = kerberos.GetTGStoC(receive2, Kctgs);
            textarea.appendText("client���ܳ���tgs���صı���: ");//��ui��������ʾ
            for(int i=0;i<message4.length;i++) {       //���
    			System.out.println(message4[i]);
    			textarea.appendText(message4[i] + "     ");//��ui��������ʾ
    		}
            textarea.appendText("\n");//��ui��������ʾ
            Kcv = message4[0];                 //����c��v�Ự��session key
            String Ticket_v = message4[3];            //tgs�������ڷ���v��������Ʊ��
            
            //v��֤
            String message5 = kerberos.CtoV(ID_c, Kcv, AD_c, Ticket_v);
            output.writeUTF(message5);                 //������Ϣ
            System.out.println("��V������֤���ģ�"+message5);
            textarea.appendText("client->v, client���͵���֤����: " + message5 + "\n");//��ui��������ʾ
            String receive3 = input.readUTF();         //������Ϣ
            System.out.println("�յ�V���������ģ�"+receive3);
            textarea.appendText("v-client, client�յ�v���صļ��ܱ���: " + receive3 + "\n");//��ui��������ʾ
            String []message6 = kerberos.GetVtoC(receive3, Kcv);
            textarea.appendText("client���ܳ���v���صı���: ");//��ui��������ʾ
            for(int i=0;i<message6.length;i++) {       //���
    			System.out.println(message6[i]);
    			textarea.appendText(message6[i] + "   ");//��ui��������ʾ
    		}
            textarea.appendText("\n");//��ui��������ʾ
            System.out.println("��֤�ɹ���");
            System.out.println("��ԿKcv: " + Kcv);
    		
    	} catch (IOException e) {
    		e.printStackTrace();
        }finally {
        	
        }
    
    	return true;
    	
    }
    
    //ע��
    public boolean register(String ID_c, String K_c){
    	try {
    		Socket socket1 = new Socket(AS_IP, AS_PORT);
            DataOutputStream output1 = new DataOutputStream(socket1.getOutputStream());
            DataInputStream input1 = new DataInputStream(socket1.getInputStream());
        
    		//String ID_c = "22345678";      //�û�id
    		//String K_c = "12345678";       //�û�����
        
    		kerberos kerberos = new kerberos();
    		String message1 = kerberos.CtoAS(ID_c, K_c);
    		output1.writeUTF(message1);//������Ϣ
    		System.out.println("��AS����ע�ᱨ�ģ�"+message1);
    		textarea.appendText("client��AS���͵�ע�ᱨ��: " + message1 + "\n");//��ui��������ʾ
    		String receive1 = input1.readUTF();  //������Ϣ
    		System.out.println("�յ�AS���������ģ�"+receive1);
    		textarea.appendText("client�յ�AS����������: " + receive1 + "\n");//��ui��������ʾ
    		String []message2 = kerberos.GetAStoC(receive1);
    		textarea.appendText("client������������: ");//��ui��������ʾ
    		for(int i=0;i<message2.length;i++) {
    			System.out.println(message2[i]);
    			textarea.appendText(message2[i] + "    ");//��ui��������ʾ
    		}
    		textarea.appendText("\n");//��ui��������ʾ
    	} catch (IOException e) {
    		e.printStackTrace();
        } 	
        return true;
    }
    
    //�ϴ��ļ�
    /*public boolean uploadfile (String filename) {
    	try {
    		FILE_upload file = new FILE_upload();
    		//filefunction file = new filefunction();
    		String[] message1 = file.Client_generateFileUpload_Package(filename, Kcv); //�����ļ����ݰ���
    		textarea_1.appendText("client��v�ϴ��ļ����͵İ�: ");//��ui��������ʾ
    		for(int i=0;i<message1.length;i++) {
    			output.writeUTF(message1[i]);                                          //ѭ�������ļ����ݰ�
    			System.out.println("��v�����ϴ��ļ����ݰ���"+message1[i]);
    			textarea_1.appendText("message1[i]" + "   ");//��ui��������ʾ
    			
    			double p;
    			p = (double)i / (double)message1.length;
    			pb.setProgress(p);//ui�������������ʾ
    		}
    		textarea_1.appendText("\n");//��ui��������ʾ
    		String receive1 = input.readUTF();                                         //��ȡV�����Ľ����Ӧ��
    		System.out.println("��ȡV�������ϴ��ļ�������ݰ���"+receive1);
    		textarea_1.appendText("client�յ�v��������Ӧ���ݰ�: " + receive1 + "\n");//��ui��������ʾ
    		String result = file.Client_parseFileUpload_Result_Package(receive1, Kcv);               //�������
    		System.out.println("�ϴ��ļ������"+result);
    		textarea_2.appendText("client�յ�v��������Ӧ���ݰ����: " + result + "\n");//��ui��������ʾ
    		
    	} catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	
    	return true;
    }*/
    
  //�ϴ��ļ�
    public boolean uploadfile1 (String filename) throws InterruptedException {
    	try {
    		FILE_upload file = new FILE_upload();
    		File_tool tool = new File_tool();
    		String message1 = file.Client_generateFileUpload_Package1(filename, Kcv);   		
    		output.writeUTF(message1);
    		System.out.println("�������ݰ�1��"+message1);
    		
    		String message2 = file.Client_generateFileUpload_Package2(filename, Kcv);
    		output.writeUTF(message2);
    		System.out.println("�������ݰ�2��"+message2);
    		textarea_1.appendText("client��v���͵��ļ����Ͱ�: " + message2 + "\n");//��ui��������ʾ
    		byte[][]filedata = file.Client_generateFileUpload_Package3(filename, Kcv);
    		textarea_1.appendText("client��v���͵��ļ����ݰ�: ");//��ui��������ʾ
    		for(int i=0;i<filedata.length;i++) {
    			output.write(filedata[i]);
    			System.out.println("�����ļ����ݰ���"+filedata[i]);
    			if(i <= 10) {
    				textarea_1.appendText("filedata[i]" + "   ");//��ui��������ʾ,ֻ��ʾǰʮ�����ݰ�
    			}
    			
    			Thread.sleep(400);
    			System.out.println(i);
    			pb.setProgress(0.5);
    		}
    		textarea_1.appendText("\n");//��ui��������ʾ
    		String receive = input.readUTF();
    		System.out.println("��ȡV�������ϴ��ļ�������ݰ���"+receive);
    		textarea_1.appendText("��ȡV�������ϴ��ļ�������ݰ���" + receive+ "\n");//��ui��������ʾ
    		String result = file.Client_parseFileUpload_Result_Package(receive, Kcv);
    		System.out.println("�ϴ��ļ������"+result);
    		textarea_2.appendText("��ȡV�������ϴ��ļ�������ݰ���" + receive+ "\n");//��ui��������ʾ
    		
    	} catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	
    	return true;
    }
    
    //�����ļ�
   /* public boolean downloadfile(String filename) {
    	try {
    		FILE_download file = new FILE_download();
    		//filefunction file = new filefunction();
    		String message1 = file.Client_Filedownload(filename, Kcv);
    		output.writeUTF(message1); 
    		System.out.println("��v���������ļ��������"+message1);
    		textarea_1.appendText("client��v���͵������ļ���: " + message1 + "\n");//��ui��������ʾ
    		String receive1 = input.readUTF();
    		System.out.println("�յ�����˷������ļ����ݰ���"+ receive1);
    		textarea_1.appendText("client�յ�v��������Ӧ���ݰ�: " + receive1 + "   ");//��ui��������ʾ
    		int length = file.V_getPackageNum(receive1, Kcv);
    		String[] filedata = new String[length];
    		filedata[0]= file.CLient_getfiledata(receive1, Kcv)[3];
    		//textarea_1.appendText("client�յ�v�����İ����ļ������ݰ�: ");//��ui��������ʾ
    		for(int i=1;i<length;i++) {
    			String receive2 = input.readUTF();
    			System.out.println("�յ�����˷������ļ����ݰ���"+ receive2);
    			textarea_1.appendText(receive2 + "   ");//��ui��������ʾ
    			filedata[i]= file.CLient_getfiledata(receive2, Kcv)[3];
    			
    			double p;
    			p = (double)i / (double)length;
    			pb.setProgress(p);//ui�������������ʾ
    		}
    		textarea_1.appendText("\n");//��ui��������ʾ
    		
    		File_tool t = new File_tool();
    		//test t = new test();
    		t.filemerge(filedata, "D:\\test\\"+filename);
    		textarea_1.appendText(filename + "�ļ��Ѵ洢!" + "�ļ�λ��: D:\\test\\" + "\n");
    		
    	}catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	return true;
    }*/
    
    //�����ļ�
    public boolean downloadfile1(String filename) {
    	try {
    		FILE_download file = new FILE_download();
    		String message1 = file.Client_Filedownload(filename, Kcv);    	
    		output.writeUTF(message1); 
    		System.out.println("��v���������ļ��������"+message1);
    		textarea_1.appendText("client��v���͵����������ļ���: " + message1 + "\n");//��ui��������ʾ
    		String receive1 = input.readUTF();
    		System.out.println("�յ�����˷������ļ���Ϣ����"+ receive1);
    		textarea_1.appendText("client�յ�����˷������ļ���Ϣ: " + receive1 + "\n");//��ui��������ʾ
    		String[] message2 = file.Client_getfilemore(receive1, Kcv);
    		
    		byte[]receive2 = new byte[1000];
    		int length = input.read(receive2);
    		byte[]temp = new byte[length];
    		System.arraycopy(receive2, 0, temp, 0, length);
    		byte[][] filedata = new byte[Integer.parseInt(message2[1])][length];
    		System.out.println("�յ�����˷������ļ����ݰ���"+ filedata[0]);
    		filedata[0] = file.Client_getfilebyte(temp, Kcv);
    		textarea_1.appendText("client�յ�v�����İ����ļ������ݰ�: ");//��ui��������ʾ
    		for(int i=1;i<Integer.parseInt(message2[1]);i++) {
    			length = input.read(receive2);
    			System.arraycopy(receive2, 0, temp, 0, length);
    			filedata[i] = file.Client_getfilebyte(temp, Kcv);
    			System.out.println("�յ�����˷������ļ����ݰ���"+ filedata[i]);
    			textarea_1.appendText(filedata[i] + "   ");//��ui��������ʾ
    			//pb.setProgress(0.5);
    		}
    		textarea_1.appendText("\n");//��ui��������ʾ
    		File_tool tool =new File_tool();
    		tool.filemerge_byte(filedata, "C:\\data\\"+filename);
    		textarea_2.appendText(filename + "�ļ��Ѿ��洢��λ���ڣ�C:\\data\\");//��ui��������ʾ
    		
    	}catch (IOException e) {
    		e.printStackTrace();
        } 
    	
    	return true;
    }
    
    //ˢ��Ŀ¼(�޸ģ���ӷ���ֵ)
    public String[] getdirectory() {
    	try {
    		FILE_refresh file = new FILE_refresh();
    		System.out.println("��ԿKcv: " + this.Kcv);
    		//filefunction file = new filefunction();
    		String message1  = file.Client_refreshFile(Kcv);                  //����Ŀ¼�����
    		System.out.println("c->vĿ¼�������"+message1);
    		textarea_1.appendText("client��������˷��͵�����ˢ��Ŀ¼��: " + message1 + "\n");//��ui��������ʾ
    		output.writeUTF(message1);                                        //���������
    		String receive1 = input.readUTF();                               //���ܷ���˷�����Ŀ¼���ݰ�
    		System.out.println("v->cĿ¼���ݰ���"+receive1);
    		textarea_1.appendText("client���շ������˷��صİ����ļ��б�İ�: " + receive1 + "\n");//��ui��������ʾ
    		String[] message2 = file.Client_parseServer(Kcv, receive1);       //���Ŀ¼���ݰ�
    		textarea_2.appendText("client���ܳ����ļ��б���Ϣ: ");
    		for(int i=0;i<message2.length;i++) {
    			System.out.println(message2[i]);
    			textarea_2.appendText(message2[i] + "   ");//��ui��������ʾ
    		}
    		textarea_2.appendText("\n");//��ui��������ʾ
    		return message2;
    		
    		
     	}catch (IOException e) {
    		e.printStackTrace();
        }
  
    	return null;
    }
    
    //ɾ���ļ�
    public boolean deletefile(String filename) {
    	try {
    		FILE_delete file = new FILE_delete();
    		//filefunction file = new filefunction();
    		String message1 = file.Client_delete(filename, Kcv);                //���ɼ��������
    		System.out.println("c->vɾ���ļ��������"+message1);
    		textarea_1.appendText("c->vɾ���ļ������: " + message1 + "\n");//��ui��������ʾ
    		output.writeUTF(message1);                                          //����ɾ�������
    		String receive1 = input.readUTF();                                  //���ܷ���˷�����ɾ����Ӧ��
    		System.out.println("v->cɾ���������"+receive1);       
    		textarea_1.appendText("v->cɾ���ļ������: " + receive1 + "\n");//��ui��������ʾ
    		String message2 = file.Client_getdeleteconfirm(receive1, Kcv);     //���Ŀ¼���ݰ�
    		textarea_2.appendText("v->cɾ���ļ������: " + message2 + "\n");//��ui��������ʾ
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
