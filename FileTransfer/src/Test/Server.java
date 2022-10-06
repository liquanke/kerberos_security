package Test;

import java.net.*;
import java.io.*;
 
public class Server extends Thread {
	private String dir;// 文件保存路径，在main函数中设置
 
	private int port;// Socket服务器的端口
 
	public Server(String dir, int port) {
		this.dir = dir;
		this.port = port;
	}
 
	@SuppressWarnings("resource" )
	@Override
	public void run()
	{
		Socket socket = null;
		DataInputStream input = null;//这里将socket监听到的数据流作为数据的输入流
		DataOutputStream fileOut = null;//文件输出流
		DataOutputStream ack = null;//作为对client数据传输成功的响应
		int bufferSize = 8192;
        byte[] buf = new byte[bufferSize];//数据存储
        long donelen = 0;//传输完成的数据长度
        long filelen = 0;//文件长度
		try{
			System.out.println("等待连接");
			ServerSocket listen = new ServerSocket(port);//设置端口监听器，在监听到客户端之前，一直堵塞
			do{
				socket = listen.accept();				
				System.out.println("客户端"+ socket.getInetAddress() +"已连接");
				
				//将socket数据作为数据输入流
				input = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));
				
				//以客户端的IP地址作为存储路径
				String fileDir = dir + "\\" + socket.getInetAddress().toString().substring(1, socket.getInetAddress().toString().length());;
								
				
				File file = new File(fileDir);
				
				//判断文件夹是否存在，不存在则创建
				if(!file.exists())
				{
					file.mkdir();
				}
				
				String fileName = input.readUTF();//读取文件名
				
				//设置文件路径
				String filePath = fileDir + "\\" + fileName;
				
				
				file = new File(filePath);
				
				if(!file.exists())
				{
					file.createNewFile();
				}
				
				fileOut = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(file)));
				
				
				filelen = input.readLong();//读取文件长度
 
                System.out.println("文件的长度为:" + filelen + "\n");
                System.out.println("开始接收文件!" + "\n");
                ack = new DataOutputStream(socket.getOutputStream());
                
                while (true) {
                    int read = 0;
                    if (input != null) {
                        read = input.read(buf);
                        ack.writeUTF("OK");//结束到数据以后给client一个回复
                    }
                    
                    if (read == -1) {
                        break;
                    }
                    donelen += read;
                    // 下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
                    System.out.println("文件接收了" + (donelen * 100 / filelen)
                            + "%\n");
                    fileOut.write(buf, 0, read);
                }
                
                if(donelen == filelen)
                	System.out.println("接收完成，文件存为" + file + "\n");
                else
                {
                	System.out.printf("IP:%s发来的%s传输过程中失去连接\n",socket.getInetAddress(),fileName);
                	file.delete();
                }
                ack.close();
                input.close();
                fileOut.close();
				
			}while(true);
		}catch (Exception e) {
            System.out.println("接收消息错误" + "\n");
            e.printStackTrace();
            return;
        }
	}
 
	public static void main(String[] args) {
		Server server = new Server("D:\\test\\download\\tcp\\", 8880);
		server.start();
	}
 
}
