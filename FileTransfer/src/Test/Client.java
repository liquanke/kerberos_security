package Test;

import java.net.*;
import java.io.*;
 
public class Client
{
    // �ϴ����ļ�����·��������main���޸�
    private String dir;
    // socket��������ַ�Ͷ˿ں�
    private String host;
    private int port;
    
	public Client(String dir, String host, int port)
	{
		this.dir = dir;
		this.host = host;
		this.port = port;
	}
	
	public void uploadFile(String fileName)
	{
		Socket socket = null;
		
		DataInputStream input = null;//�ļ�������
		DataOutputStream output = null;
		DataInputStream getAck = null;//��÷�������ACK
		int bufferSize = 8192;
        byte[] buf = new byte[bufferSize];//���ݴ洢
		
		try{
			
			socket = new Socket(host, port);//����socket������������connect
			
			 // ѡ����д�����ļ�
            File file = new File(dir + fileName);
            System.out.println("�ļ�����:" + (int) file.length());
			
			input = new DataInputStream(new FileInputStream(dir + fileName));
			output = new DataOutputStream(socket.getOutputStream());//��socket����Ϊ���ݵĴ������
			
			getAck = new DataInputStream(socket.getInputStream());//����socket���ݵ���Դ
			
			//���ļ��������ȥ
			output.writeUTF(file.getName());
			output.flush();
			//���ļ����ȴ����ȥ
			output.writeLong((long) file.length());
			output.flush();
			
			int readSize = 0;
			
			while(true)
			{
				if(input != null)
				{
					readSize = input.read(buf);
				}
				if(readSize == -1)
					break;
				
				output.write(buf, 0, readSize);
				
				if(!getAck.readUTF().equals("OK"))
				{
					System.out.println("������"+ host + ":" + port + "ʧȥ���ӣ�"); 
					break;
				}
			}
            output.flush();
            // ע��ر�socket����Ŷ����Ȼ�ͻ��˻�ȴ�server�����ݹ�����
            // ֱ��socket��ʱ���������ݲ�������
            input.close();
            output.close();
            socket.close();
            getAck.close();
            System.out.println("�ļ��������");
			
		}catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args)
	{
		Client client=new Client("D:\\test\\upload\\","127.0.0.1",8880);
		client.uploadFile("mysql-connector-java-8.0.16.jar");
	}
}
