package Test;

import java.net.*;
import java.io.*;
 
public class Client
{
    // 上传的文件保存路径，可在main中修改
    private String dir;
    // socket服务器地址和端口号
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
		
		DataInputStream input = null;//文件输入流
		DataOutputStream output = null;
		DataInputStream getAck = null;//获得服务器的ACK
		int bufferSize = 8192;
        byte[] buf = new byte[bufferSize];//数据存储
		
		try{
			
			socket = new Socket(host, port);//设置socket，并进行连接connect
			
			 // 选择进行传输的文件
            File file = new File(dir + fileName);
            System.out.println("文件长度:" + (int) file.length());
			
			input = new DataInputStream(new FileInputStream(dir + fileName));
			output = new DataOutputStream(socket.getOutputStream());//将socket设置为数据的传输出口
			
			getAck = new DataInputStream(socket.getInputStream());//设置socket数据的来源
			
			//将文件名传输过去
			output.writeUTF(file.getName());
			output.flush();
			//将文件长度传输过去
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
					System.out.println("服务器"+ host + ":" + port + "失去连接！"); 
					break;
				}
			}
            output.flush();
            // 注意关闭socket链接哦，不然客户端会等待server的数据过来，
            // 直到socket超时，导致数据不完整。
            input.close();
            output.close();
            socket.close();
            getAck.close();
            System.out.println("文件传输完成");
			
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
