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
	
	/*(生成数据包1)
	 * 客户端生成文件上传包请求函数,该包不包含文件数据
	参数为文件名，kcv
	返回值为请求包，该包无需解包，只为发送请求类型
	*/
	public String Client_generateFileUpload_Package1(String file_Name,String Kcv) {
		File_tool file_tool=new File_tool(); 
		String data = file_tool.PathtoPath(file_Name)+" ######";
		DES des = new DES(Kcv);
		data = des.encrypt_string(data);//对数据进行加密
		String cpackage = Upfile+data; 
		return cpackage;
	}
	
	/*(生成数据包2)
	 * 客户端生成文件上传包函数,该包不包含文件数据，只包含文件基本信息
	参数为文件名，kcv
	返回值为包含文件基本信息包
	*/
	public String Client_generateFileUpload_Package2(String file_Name,String Kcv) {
		File_tool file_tool=new File_tool();
		int block_length = file_tool.filesplit_byte(file_Name).length;//文件总块数
		int num = 1;//当前文件块编号(因为本包只发送一次)
		String data = file_tool.PathtoPath(file_Name)+" "+block_length+" "+num;
		DES des = new DES(Kcv);
		data = des.encrypt_string(data);//对数据进行加密
		kerberos kerberos_test=new kerberos();
		String package_length=kerberos_test.getlength(data.length());
		String cpackage = Upfile+package_length+data; 
		return cpackage;
	}
	
	/*(生成数据包3)
	 * 客户端生成文件上传包函数,该包仅仅包含文件数据
	参数为文件名，kcv
	返回值为文件数据包(byte数组)
		*/
	public byte[][] Client_generateFileUpload_Package3(String file_Name,String Kcv) {
		File_tool file_tool=new File_tool();
		byte[][] file_data=file_tool.filesplit_byte(file_Name);
		int block_length = file_data.length;//文件总块数
		DES des = new DES(Kcv);
		byte[][] data = new byte[block_length][file_data[0].length];
		for(int i=0;i<block_length;i++) {
			data[i] = des.encrypt_byte(file_data[i]);//对数据进行加密
		}
		return data;
	}
	
	
	/*(解析数据包2)
	 * 服务端解析文件上传包函数，该函数用于获取上传文件的基本信息，不含文件块数据
	参数cpackage为收到的包和K_cv
	返回值为string数组，
	result[0];是文件名   result[1];是文件总块数 
	*/
	public String[] Sever_parseFileUpload_Package2(String cpackage,String Kcv) {
		
		char[] ss = cpackage.toCharArray();
		String[] message = new String[2];
		//获取包的长度
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
		String m = des.decrypt_string(message[1]);//解密数据包
		String[] result = new String[2];
		result = m.split(" ");
		//String file_Name=result[0];//文件名
		//int file_length = Integer.parseInt(result[1]); //文件总块数
		//int file_num=Integer.parseInt(result[2]);//当前文件块数
		return result;
	}
	
	/*(解析数据包3)
	 * 服务端解析文件上传包函数，该函数用于获取上传文件的基本信息，不含文件块数据
	参数cpackage为收到的包和K_cv
	返回值为string数组，
	result[0];是文件名   result[1];是文件长度   result[2];是当前文件块数 
	*/
	public byte[] Sever_parseFileUpload_Package3(byte[] cpackage,String Kcv) {
		
		byte[] data=new byte[cpackage.length];
		DES des=new DES(Kcv);
		data=des.decrypt_byte(cpackage);
		return data;
	}
	
	/*服务端生成上传文件结果响应包函数
	参数为K_cv,注册结果，传输成功11     传输失败10   传输结束00
	返回值为生成的string包*/
	public String V_generateFileUpload_Result_Package(String Kcv,String Status) {
		String data="####### "+Status;
		DES des = new DES(Kcv);
		kerberos kerberos_test = new kerberos();
		data = des.encrypt_string(data);//对数据进行加密
		String result=Upfile_Result+kerberos_test.getlength(data.length())+data;
		return result;
	}
	
	/*客户端解析文件上传包结果响应包函数
	参数为接收的数据包，K_cv
	返回值为注册结果*/
	public String Client_parseFileUpload_Result_Package(String cpackage,String Kcv) {
		char[] ss = cpackage.toCharArray();
		String[] message = new String[2];
		//获取包的长度
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
		String m = des.decrypt_string(message[1]);//解密数据包
		String[] result = new String[2];
		result = m.split(" ");
		return result[1];
	}
	
	//通过一个包获取文件总块数
	public int V_getPackageNum(String cpackage,String Kcv) {
		char[] ss = cpackage.toCharArray();
		String[] message = new String[2];
		
		//获取包的长度
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
		String m = des.decrypt_string(message[1]);//解密数据包
		String[] result = new String[4];
		result = m.split(" ");
		//String file_Name=result[0];//文件名
		int file_length = Integer.parseInt(result[1]); //文件总块数
		//int file_num=Integer.parseInt(result[2]);//当前文件块数
		//String file_data=result[3];//文件数据内容
		return file_length;
	}
	
}
