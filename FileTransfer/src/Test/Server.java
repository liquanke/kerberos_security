package Test;

import java.net.*;
import java.io.*;
 
public class Server extends Thread {
	private String dir;// �ļ�����·������main����������
 
	private int port;// Socket�������Ķ˿�
 
	public Server(String dir, int port) {
		this.dir = dir;
		this.port = port;
	}
 
	@SuppressWarnings("resource" )
	@Override
	public void run()
	{
		Socket socket = null;
		DataInputStream input = null;//���ｫsocket����������������Ϊ���ݵ�������
		DataOutputStream fileOut = null;//�ļ������
		DataOutputStream ack = null;//��Ϊ��client���ݴ���ɹ�����Ӧ
		int bufferSize = 8192;
        byte[] buf = new byte[bufferSize];//���ݴ洢
        long donelen = 0;//������ɵ����ݳ���
        long filelen = 0;//�ļ�����
		try{
			System.out.println("�ȴ�����");
			ServerSocket listen = new ServerSocket(port);//���ö˿ڼ��������ڼ������ͻ���֮ǰ��һֱ����
			do{
				socket = listen.accept();				
				System.out.println("�ͻ���"+ socket.getInetAddress() +"������");
				
				//��socket������Ϊ����������
				input = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));
				
				//�Կͻ��˵�IP��ַ��Ϊ�洢·��
				String fileDir = dir + "\\" + socket.getInetAddress().toString().substring(1, socket.getInetAddress().toString().length());;
								
				
				File file = new File(fileDir);
				
				//�ж��ļ����Ƿ���ڣ��������򴴽�
				if(!file.exists())
				{
					file.mkdir();
				}
				
				String fileName = input.readUTF();//��ȡ�ļ���
				
				//�����ļ�·��
				String filePath = fileDir + "\\" + fileName;
				
				
				file = new File(filePath);
				
				if(!file.exists())
				{
					file.createNewFile();
				}
				
				fileOut = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(file)));
				
				
				filelen = input.readLong();//��ȡ�ļ�����
 
                System.out.println("�ļ��ĳ���Ϊ:" + filelen + "\n");
                System.out.println("��ʼ�����ļ�!" + "\n");
                ack = new DataOutputStream(socket.getOutputStream());
                
                while (true) {
                    int read = 0;
                    if (input != null) {
                        read = input.read(buf);
                        ack.writeUTF("OK");//�����������Ժ��clientһ���ظ�
                    }
                    
                    if (read == -1) {
                        break;
                    }
                    donelen += read;
                    // �����������Ϊͼ�ν����prograssBar���ģ���������Ǵ��ļ������ܻ��ظ���ӡ��һЩ��ͬ�İٷֱ�
                    System.out.println("�ļ�������" + (donelen * 100 / filelen)
                            + "%\n");
                    fileOut.write(buf, 0, read);
                }
                
                if(donelen == filelen)
                	System.out.println("������ɣ��ļ���Ϊ" + file + "\n");
                else
                {
                	System.out.printf("IP:%s������%s���������ʧȥ����\n",socket.getInetAddress(),fileName);
                	file.delete();
                }
                ack.close();
                input.close();
                fileOut.close();
				
			}while(true);
		}catch (Exception e) {
            System.out.println("������Ϣ����" + "\n");
            e.printStackTrace();
            return;
        }
	}
 
	public static void main(String[] args) {
		Server server = new Server("D:\\test\\download\\tcp\\", 8880);
		server.start();
	}
 
}
