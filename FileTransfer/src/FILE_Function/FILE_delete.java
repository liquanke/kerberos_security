package FILE_Function;

import DES.DES;
import Kerberos.kerberos;

public class FILE_delete {

	    //删除服务器文件流程
		// c ->v   发送删除请求包
		// cpackge 删除文件文件名    Kcv 密钥
		// return  发送数据包   
		
	   public String Client_delete(String fileName,String Kcv) {
			//生成message加密包
			String message,message1;
			message1 = fileName+" "+"########";
			DES des = new DES(Kcv);
			kerberos kerberos_test=new kerberos();
			message = des.encrypt_string(message1);
			//生成发送数据包
			int length = message.length();
			String length1 = kerberos_test.getlength(length);
			String cpackage = "1012"+length1+message;
			return cpackage;
		}
		
		// v  接受c发送的delete包
		// cpackge 接收数据包    Kcv 密钥
		// return   删除文件文件名
		public String Server_getdelete(String cpackage,String Kcv) {
			String[] result;
			char[]ss=cpackage.toCharArray();
			String message1="";
			for(int i=8;i<cpackage.length();i++) {
			    message1=message1+ss[i];
			}
			//解密数据包
			DES des = new DES(Kcv);
			String temp = des.decrypt_string(message1);
			result= temp.split(" ");
			return result[0];
		}

		
		// v -> c  发送删除成功包
 		// Name 删除成功传 11  删除失败传00    Kcv 密钥
 		// return   删除文件文件名
		public String Server_deleteconfirm(Boolean flag ,String Kcv) {
 			//生成message加密包
			String message,message1;
			if(flag==true) {
				message1 = "11"+" "+"########";
			}
			else {
				message1 = "00"+" "+"########";
			}
			DES des = new DES(Kcv);
			kerberos kerberos_test=new kerberos();
			message = des.encrypt_string(message1);
			//生成发送数据包
			int length = message.length();
			String length1 = kerberos_test.getlength(length);
			String cpackage = "1112"+length1+message;
			return cpackage;
 		}
 		
 		
 		// v  接受c发送的delete包
 		// cpackge 接收数据包    Kcv 密钥
 		// return   删除文件文件名
		public String Client_getdeleteconfirm(String cpackage,String Kcv) {
 			String[] result;
			char[]ss=cpackage.toCharArray();
			String message1="";
			for(int i=8;i<cpackage.length();i++) {
			    message1=message1+ss[i];
			}
			//解密数据包
			DES des = new DES(Kcv);
			String temp  = des.decrypt_string(message1);
			result = temp.split(" ");
			return result[0];
 		}
}
