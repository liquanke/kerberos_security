package FILE_Function;

import DES.DES;

public class FILE_download {

	public String getlength(int length) {//长度转换
		String len="";
		if(length<10) {
			len ="000"+String.valueOf(length);
		}else if(length<100) {
			len ="00"+String.valueOf(length);
		}else if(length<1000) {
			len ="0"+String.valueOf(length);
		}else {
			len = String.valueOf(length);
		}
		return len;
	}
	//用户向服务端发送下载文件请求包
			public String Client_Filedownload(String filename,String Kcv) {
				DES des = new DES(Kcv);
				String message = des.encrypt_string(filename+" #########");            //加密数据包 填充防止报文过小
				int length = message.length();   
				String length1 = getlength(length);
				String cpackage = "1011"+length1+message;                              //组装报文
				
				return cpackage;
			}
			
			//服务器解析客户端发来的文件下载请求包
			public String Server_GetFiledownload(String cpackage ,String Kcv) {
				DES des = new DES(Kcv);
				
				char[]ss=cpackage.toCharArray();
				String message="";
				for(int i=8;i<cpackage.length();i++) {                                //去除前八位
					message=message+ss[i];
				}
				String filename = des.decrypt_string(message).split(" ")[0];           //解密数据包
				
				return filename;
			}
			
			//服务器向用户端发送文件信息数据包
			public String Server_filemore(String filename,String Kcv) {
				DES des = new DES(Kcv);
				File_tool file_tool = new File_tool();
				byte[][] filedata = file_tool.filesplit_byte(filename);   //文件数据分块存储为字节数组
				String message = des.encrypt_string(filename+" "+filedata.length+" "+111111);
				int length = message.length();
				String length1 = getlength(length);
				String filemore = "1017"+length1+message;                  //组装报文
				return filemore;
			}
			
			//服务器向用户端发送文件数据包
			public byte[][] Server_filebyte(String filename,String Kcv) {
				DES des = new DES(Kcv);
				
				File_tool file_tool = new File_tool();
				byte[][] filedata = file_tool.filesplit_byte(filename); 
				byte[][] message1 = new byte[filedata.length][filedata[0].length];
				for(int i=0;i<filedata.length;i++) {
					message1[i]=des.encrypt_byte(filedata[i]);          //组装报文
				}
				return message1;
			}
			
			//客户端解析文件信息包
			public String[] Client_getfilemore(String filemore,String Kcv) {
				DES des = new DES(Kcv);
				char[]ss=filemore.toCharArray();
				String message="";
				for(int i=8;i<filemore.length();i++) {                                //去除前八位
					message=message+ss[i];
				}
				String message1 = des.decrypt_string(message);
				String[] message2 = message1.split(" ");
				String[] message3 = new String[2];                  //分离报文
				message3[0] =message2[0];                           //获取文件名
				message3[1] =message2[1];                           //获取文件块
				
				return message3;                      
			}
			
			//客户端解析服务端发来文件数据包
			public byte[]Client_getfilebyte(byte[] filedata,String Kcv){
				DES des = new DES(Kcv);
				byte[] message = des.decrypt_byte(filedata);
				return message;
			}
}
