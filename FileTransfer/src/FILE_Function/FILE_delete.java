package FILE_Function;

import DES.DES;
import Kerberos.kerberos;

public class FILE_delete {

	    //ɾ���������ļ�����
		// c ->v   ����ɾ�������
		// cpackge ɾ���ļ��ļ���    Kcv ��Կ
		// return  �������ݰ�   
		
	   public String Client_delete(String fileName,String Kcv) {
			//����message���ܰ�
			String message,message1;
			message1 = fileName+" "+"########";
			DES des = new DES(Kcv);
			kerberos kerberos_test=new kerberos();
			message = des.encrypt_string(message1);
			//���ɷ������ݰ�
			int length = message.length();
			String length1 = kerberos_test.getlength(length);
			String cpackage = "1012"+length1+message;
			return cpackage;
		}
		
		// v  ����c���͵�delete��
		// cpackge �������ݰ�    Kcv ��Կ
		// return   ɾ���ļ��ļ���
		public String Server_getdelete(String cpackage,String Kcv) {
			String[] result;
			char[]ss=cpackage.toCharArray();
			String message1="";
			for(int i=8;i<cpackage.length();i++) {
			    message1=message1+ss[i];
			}
			//�������ݰ�
			DES des = new DES(Kcv);
			String temp = des.decrypt_string(message1);
			result= temp.split(" ");
			return result[0];
		}

		
		// v -> c  ����ɾ���ɹ���
 		// Name ɾ���ɹ��� 11  ɾ��ʧ�ܴ�00    Kcv ��Կ
 		// return   ɾ���ļ��ļ���
		public String Server_deleteconfirm(Boolean flag ,String Kcv) {
 			//����message���ܰ�
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
			//���ɷ������ݰ�
			int length = message.length();
			String length1 = kerberos_test.getlength(length);
			String cpackage = "1112"+length1+message;
			return cpackage;
 		}
 		
 		
 		// v  ����c���͵�delete��
 		// cpackge �������ݰ�    Kcv ��Կ
 		// return   ɾ���ļ��ļ���
		public String Client_getdeleteconfirm(String cpackage,String Kcv) {
 			String[] result;
			char[]ss=cpackage.toCharArray();
			String message1="";
			for(int i=8;i<cpackage.length();i++) {
			    message1=message1+ss[i];
			}
			//�������ݰ�
			DES des = new DES(Kcv);
			String temp  = des.decrypt_string(message1);
			result = temp.split(" ");
			return result[0];
 		}
}
