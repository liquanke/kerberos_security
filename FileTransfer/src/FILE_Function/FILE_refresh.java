package FILE_Function;
import java.io.File;
import java.util.ArrayList;

import DES.DES;
import Kerberos.kerberos;
public class FILE_refresh {

	//客户端生成请求刷新包
	public String Client_refreshFile(String K_cv) {
		String result = null;
		
		String PType = "1015";
		String data;
		String len;
		String stateInfo = "refresh";
		String info = "refresh the directory!";
		data = stateInfo + " " + info;
		DES des = new DES(K_cv);
		kerberos kerberos_test=new kerberos();
		data = des.encrypt_string(data);
		len = kerberos_test.getlength(data.length());
		
		result = PType + len + data;
		return result;
	}
	
	//服务器端进行刷新目录操作
	public ArrayList<String> Server_RefreshFile(String Directory) {
		 ArrayList<String> files = new ArrayList<String>();
		    File file = new File(Directory);
		    File_tool file_tool=new File_tool();
		    //PathtoPath
		    File[] tempList = file.listFiles();

		    for (int i = 0; i < tempList.length; i++) {
		        if (tempList[i].isFile()) {
		        	//System.out.println("文     件：" + tempList[i]);
		            files.add(file_tool.PathtoPath(tempList[i].toString()));
		        }
		    }
		    file.delete();
		    return files;
		
	}
	//服务器端返回给客户端刷新后的文件列表 生成的数据包
    public String Server_toClient(String K_cv, String Directory) {
		
		ArrayList<String> files = Server_RefreshFile(Directory);

		String result;
		String PType = "1115";
		String Len;
		String data = "fileLists: ";
		
		
		for (int i = 0; i < files.size(); ++i) {
			data = data + files.get(i) + " ";
		}
		DES des = new DES(K_cv);
		kerberos kerberos_test=new kerberos();
		data = des.encrypt_string(data);
		Len = kerberos_test.getlength(data.length());
		result = PType + Len + data;

		return result;
		
	}
    
  //客户端解析服务器端返回的数据包得到文件列表
    public String[] Client_parseServer(String K_cv, String message) {
    	char[] ss = message.toCharArray();
    	String[] file;
    	
    	char[] len = new char[4];//获取数据包长度
    	for(int i = 4; i < 8; ++i) {
    		len[i-4] = ss[i];
    	}
    	String length = new String(len);
    	
    	//获取数据
    	char[] data = new char[Integer.parseInt(length)];
    	for (int i = 8; i < Integer.parseInt(length)+8; ++i) {
    		data[i-8] = ss[i];
    	}
    	
    	String filesList = new String(data);
    	DES des = new DES(K_cv);
    	filesList = des.decrypt_string(filesList);
    	System.out.println(filesList);
    	file = filesList.split(" ");
    	String[] files = new String[file.length-1];
    	for (int i = 1; i < file.length; ++i) {
    		files[i - 1] = file[i];
    	}
    	return files;
	
}

}
