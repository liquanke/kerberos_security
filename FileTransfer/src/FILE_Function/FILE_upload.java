package FILE_Function;
import DES.DES;
import Kerberos.kerberos;

public class FILE_upload {

	//private static String K_cv= "default5";
	private static String Upfile= "1009";
	private static String Upfile_Result= "1109";
	
	public static void main(String[]argc)
    {
		/*
		String []message1=Client_generateFileUpload_Package("D:\\test\\333333333.txt", K_cv);
		for(int i=0;i<message1.length;i++) {
			System.out.println("Client_generateFileUpload_Package:"+message1[i]);
			
		}
		
		String []file_data = new String[message1.length];
		for(int i=0;i<message1.length;i++) {
			String []message2= Sever_parseFileUpload_Package(message1[i],K_cv);
			for(int j=0;j<message2.length;j++) {
				System.out.println("Sever_parseFileUpload_Package:"+message2[j]);
				file_data[i]=message2[3];
			}
		}
		
		for(int j=0;j<file_data.length;j++) {
			System.out.println("File_data:"+file_data[j]);
		}
		
		String status="11";
		String message3=V_generateFileUpload_Result_Package(K_cv,status);
		System.out.println("V_generateFileUpload_Result_Package:"+message3);
		
		String message4=Client_parseFileUpload_Result_Package(message3,K_cv);
		System.out.println("Client_parseFileUpload_Result_Package:"+message4);
		*/
    }
	
	/*(�������ݰ�1)
	 * �ͻ��������ļ��ϴ���������,�ð��������ļ�����
	����Ϊ�ļ�����kcv
	����ֵΪ��������ð���������ֻΪ������������
	*/
	public String Client_generateFileUpload_Package1(String file_Name,String Kcv) {
		File_tool file_tool=new File_tool(); 
		String data = file_tool.PathtoPath(file_Name)+" ######";
		DES des = new DES(Kcv);
		data = des.encrypt_string(data);//�����ݽ��м���
		String cpackage = Upfile+data; 
		return cpackage;
	}
	
	/*(�������ݰ�2)
	 * �ͻ��������ļ��ϴ�������,�ð��������ļ����ݣ�ֻ�����ļ�������Ϣ
	����Ϊ�ļ�����kcv
	����ֵΪ�����ļ�������Ϣ��
	*/
	public String Client_generateFileUpload_Package2(String file_Name,String Kcv) {
		File_tool file_tool=new File_tool();
		int block_length = file_tool.filesplit_byte(file_Name).length;//�ļ��ܿ���
		int num = 1;//��ǰ�ļ�����(��Ϊ����ֻ����һ��)
		String data = file_tool.PathtoPath(file_Name)+" "+block_length+" "+num;
		DES des = new DES(Kcv);
		data = des.encrypt_string(data);//�����ݽ��м���
		kerberos kerberos_test=new kerberos();
		String package_length=kerberos_test.getlength(data.length());
		String cpackage = Upfile+package_length+data; 
		return cpackage;
	}
	
	/*(�������ݰ�3)
	 * �ͻ��������ļ��ϴ�������,�ð����������ļ�����
	����Ϊ�ļ�����kcv
	����ֵΪ�ļ����ݰ�(byte����)
		*/
	public byte[][] Client_generateFileUpload_Package3(String file_Name,String Kcv) {
		File_tool file_tool=new File_tool();
		byte[][] file_data=file_tool.filesplit_byte(file_Name);
		int block_length = file_data.length;//�ļ��ܿ���
		DES des = new DES(Kcv);
		byte[][] data = new byte[block_length][file_data[0].length];
		for(int i=0;i<block_length;i++) {
			data[i] = des.encrypt_byte(file_data[i]);//�����ݽ��м���
		}
		return data;
	}
	
	
	/*(�������ݰ�2)
	 * ����˽����ļ��ϴ����������ú������ڻ�ȡ�ϴ��ļ��Ļ�����Ϣ�������ļ�������
	����cpackageΪ�յ��İ���K_cv
	����ֵΪstring���飬
	result[0];���ļ���   result[1];���ļ��ܿ��� 
	*/
	public String[] Sever_parseFileUpload_Package2(String cpackage,String Kcv) {
		
		char[] ss = cpackage.toCharArray();
		String[] message = new String[2];
		//��ȡ���ĳ���
		char[] dataLen = new char[4];
		for (int i = 4; i < 8; ++i) {
			dataLen[i-4] = ss[i];
		}
		message[0] = new String(dataLen);
		
		int datalength = Integer.parseInt(message[0]);
		char[] data = new char[datalength];
		for (int i = 8; i < 8 + datalength; ++i) {
			data[i-8] = ss[i];
		}
		message[1] = new String(data);
		DES des = new DES(Kcv);
		String m = des.decrypt_string(message[1]);//�������ݰ�
		String[] result = new String[2];
		result = m.split(" ");
		//String file_Name=result[0];//�ļ���
		//int file_length = Integer.parseInt(result[1]); //�ļ��ܿ���
		//int file_num=Integer.parseInt(result[2]);//��ǰ�ļ�����
		return result;
	}
	
	/*(�������ݰ�3)
	 * ����˽����ļ��ϴ����������ú������ڻ�ȡ�ϴ��ļ��Ļ�����Ϣ�������ļ�������
	����cpackageΪ�յ��İ���K_cv
	����ֵΪstring���飬
	result[0];���ļ���   result[1];���ļ�����   result[2];�ǵ�ǰ�ļ����� 
	*/
	public byte[] Sever_parseFileUpload_Package3(byte[] cpackage,String Kcv) {
		
		byte[] data=new byte[cpackage.length];
		DES des=new DES(Kcv);
		data=des.decrypt_byte(cpackage);
		return data;
	}
	
	/*����������ϴ��ļ������Ӧ������
	����ΪK_cv,ע����������ɹ�11     ����ʧ��10   �������00
	����ֵΪ���ɵ�string��*/
	public String V_generateFileUpload_Result_Package(String Kcv,String Status) {
		String data="####### "+Status;
		DES des = new DES(Kcv);
		kerberos kerberos_test = new kerberos();
		data = des.encrypt_string(data);//�����ݽ��м���
		String result=Upfile_Result+kerberos_test.getlength(data.length())+data;
		return result;
	}
	
	/*�ͻ��˽����ļ��ϴ��������Ӧ������
	����Ϊ���յ����ݰ���K_cv
	����ֵΪע����*/
	public String Client_parseFileUpload_Result_Package(String cpackage,String Kcv) {
		char[] ss = cpackage.toCharArray();
		String[] message = new String[2];
		//��ȡ���ĳ���
		char[] dataLen = new char[4];
		for (int i = 4; i < 8; ++i) {
			dataLen[i-4] = ss[i];
		}
		message[0] = new String(dataLen);
		int datalength = Integer.parseInt(message[0]);
		char[] data = new char[datalength];
		for (int i = 8; i < 8 + datalength; ++i) {
			data[i-8] = ss[i];
		}
		message[1] = new String(data);
		DES des = new DES(Kcv);
		String m = des.decrypt_string(message[1]);//�������ݰ�
		String[] result = new String[2];
		result = m.split(" ");
		return result[1];
	}
	
	//ͨ��һ������ȡ�ļ��ܿ���
	public int V_getPackageNum(String cpackage,String Kcv) {
		char[] ss = cpackage.toCharArray();
		String[] message = new String[2];
		
		//��ȡ���ĳ���
		char[] dataLen = new char[4];
		for (int i = 4; i < 8; ++i) {
			dataLen[i-4] = ss[i];
		}
		message[0] = new String(dataLen);
		
		int datalength = Integer.parseInt(message[0]);
		char[] data = new char[datalength];
		for (int i = 8; i < 8 + datalength; ++i) {
			data[i-8] = ss[i];
		}
		message[1] = new String(data);
		DES des = new DES(Kcv);
		String m = des.decrypt_string(message[1]);//�������ݰ�
		String[] result = new String[4];
		result = m.split(" ");
		//String file_Name=result[0];//�ļ���
		int file_length = Integer.parseInt(result[1]); //�ļ��ܿ���
		//int file_num=Integer.parseInt(result[2]);//��ǰ�ļ�����
		//String file_data=result[3];//�ļ���������
		return file_length;
	}
	
}
