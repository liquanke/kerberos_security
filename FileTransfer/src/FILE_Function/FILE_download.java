package FILE_Function;

import DES.DES;

public class FILE_download {

	public String getlength(int length) {//����ת��
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
	//�û������˷��������ļ������
			public String Client_Filedownload(String filename,String Kcv) {
				DES des = new DES(Kcv);
				String message = des.encrypt_string(filename+" #########");            //�������ݰ� ����ֹ���Ĺ�С
				int length = message.length();   
				String length1 = getlength(length);
				String cpackage = "1011"+length1+message;                              //��װ����
				
				return cpackage;
			}
			
			//�����������ͻ��˷������ļ����������
			public String Server_GetFiledownload(String cpackage ,String Kcv) {
				DES des = new DES(Kcv);
				
				char[]ss=cpackage.toCharArray();
				String message="";
				for(int i=8;i<cpackage.length();i++) {                                //ȥ��ǰ��λ
					message=message+ss[i];
				}
				String filename = des.decrypt_string(message).split(" ")[0];           //�������ݰ�
				
				return filename;
			}
			
			//���������û��˷����ļ���Ϣ���ݰ�
			public String Server_filemore(String filename,String Kcv) {
				DES des = new DES(Kcv);
				File_tool file_tool = new File_tool();
				byte[][] filedata = file_tool.filesplit_byte(filename);   //�ļ����ݷֿ�洢Ϊ�ֽ�����
				String message = des.encrypt_string(filename+" "+filedata.length+" "+111111);
				int length = message.length();
				String length1 = getlength(length);
				String filemore = "1017"+length1+message;                  //��װ����
				return filemore;
			}
			
			//���������û��˷����ļ����ݰ�
			public byte[][] Server_filebyte(String filename,String Kcv) {
				DES des = new DES(Kcv);
				
				File_tool file_tool = new File_tool();
				byte[][] filedata = file_tool.filesplit_byte(filename); 
				byte[][] message1 = new byte[filedata.length][filedata[0].length];
				for(int i=0;i<filedata.length;i++) {
					message1[i]=des.encrypt_byte(filedata[i]);          //��װ����
				}
				return message1;
			}
			
			//�ͻ��˽����ļ���Ϣ��
			public String[] Client_getfilemore(String filemore,String Kcv) {
				DES des = new DES(Kcv);
				char[]ss=filemore.toCharArray();
				String message="";
				for(int i=8;i<filemore.length();i++) {                                //ȥ��ǰ��λ
					message=message+ss[i];
				}
				String message1 = des.decrypt_string(message);
				String[] message2 = message1.split(" ");
				String[] message3 = new String[2];                  //���뱨��
				message3[0] =message2[0];                           //��ȡ�ļ���
				message3[1] =message2[1];                           //��ȡ�ļ���
				
				return message3;                      
			}
			
			//�ͻ��˽�������˷����ļ����ݰ�
			public byte[]Client_getfilebyte(byte[] filedata,String Kcv){
				DES des = new DES(Kcv);
				byte[] message = des.decrypt_byte(filedata);
				return message;
			}
}
