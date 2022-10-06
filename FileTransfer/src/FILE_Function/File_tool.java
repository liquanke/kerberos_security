package FILE_Function;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class File_tool {
	public String[] filesplit(String filename){
		try {
			int maxsize = 500;
			int count=0;//Ҫ�ֿ�ĳ���
			RandomAccessFile file = new RandomAccessFile(filename,"r");
			long length = file.length();                                     
			if(length%maxsize==0) count = (int) (length/maxsize);
			else count = (int) (length/maxsize)+1;
			String[] filedata = new String[count];          //�����洢�ֿ���ļ����ݵ�����
            long offSet = 0L;                                //��ʼ��ƫ����
            
            for(int i=0;i<count-1;i++) {
            	long begin = offSet;                         //��ʼ��ȡ���ļ������ĩλ��
                long end = (i + 1) * maxsize;
                
                byte[] b = new byte[maxsize];                //�ֿ��ļ����ݴ洢����
                int n = 0;
                file.seek(begin);                            //��ָ��λ�ö�ȡ�ļ��ֽ���
                while(file.getFilePointer() <= end && (n = file.read(b)) != -1){ //�ж��ļ�����ȡ�ı߽�
                    filedata[i] = new String(b,"ISO-8859-1");
                }
                offSet += maxsize;
            }
            if (length - offSet > 0) {                       //���һ�鵥������
                byte[] b = new byte[(int) (length-offSet)];  //�ֿ��ļ����ݴ洢����
                int n = 0;
                file.seek(offSet);                            //��ָ��λ�ö�ȡ�ļ��ֽ���
                while(file.getFilePointer() <= length && (n = file.read(b)) != -1){ //�ж��ļ�����ȡ�ı߽�
                    filedata[count-1] = new String(b,"ISO-8859-1");
                }
            }
            
        file.close();                                          //�ر��ļ���	
        return filedata;	
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean filemerge(String []filedata,String filename){
		try {
			RandomAccessFile file = new RandomAccessFile(filename,"rw");
			for(int i = 0;i<filedata.length;i++) {
				byte[] data = filedata[i].getBytes("ISO-8859-1");
				file.write(data,0,data.length);
			}
			file.close();
			RandomAccessFile file1 = new RandomAccessFile(filename,"r");
			System.out.println(file1.length());
			file1.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return true;
	}
	
	public String PathtoPath(String path) {//�����ļ�·����ȡ�ļ���
		String fileName;
		File tempFile =new File( path.trim());  
        fileName = tempFile.getName();  
		return fileName;
	}
	
	
	/* 
	 ����·��ɾ��ָ����Ŀ¼���ļ������۴������ 
	 String  fileName  Ҫɾ����Ŀ¼���ļ� 
	 return ɾ���ɹ����� true�����򷵻� false�� 
	 */  
	public boolean DeleteFolder(String fileName) {  
	    boolean flag = false;  
	    File file = new File(fileName);  
	    // �ж�Ŀ¼���ļ��Ƿ����  
	    if (!file.exists()) {  // �����ڷ��� false  
	    	System.out.println("�ļ�������");
	        return flag;  
	    } else {   
	            return file.delete();  
	        }
	    }  
	
	public byte[][] filesplit_byte(String filename){
		try {
			int maxsize1 = 999;
			int count1 = 0;
			RandomAccessFile file = new RandomAccessFile(filename,"r");
			long length = file.length();
			//System.out.println(length);
			if(length%maxsize1==0) count1 = (int) (length/maxsize1);
			else count1 = (int) (length/maxsize1)+1;
			byte[][] filedata = new byte[count1][maxsize1];    //�����洢�ֿ���ļ����ݵ�����
			//System.out.println(count1);
            long offSet = 0L;                                //��ʼ��ƫ����
            
            for(int i=0;i<count1-1;i++) {
            	long begin = offSet;                         //��ʼ��ȡ���ļ������ĩλ��
                long end = (i + 1) * maxsize1;
                
                byte[] b = new byte[maxsize1];                //�ֿ��ļ����ݴ洢����
                int n = 0;
                file.seek(begin);                            //��ָ��λ�ö�ȡ�ļ��ֽ���
                while(file.getFilePointer() < end && (n = file.read(b)) != -1){ //�ж��ļ�����ȡ�ı߽�
                	//System.out.println(n);
                	//System.out.println(filedata[i].length);
                	System.arraycopy(b, 0, filedata[i], 0, n);
                }
                offSet += maxsize1;
                
            }
            //num = (int) (length-offSet);
            //System.out.println("length "+length);
           // System.out.println("offset "+offSet);
            if (length - offSet > 0) {                       //���һ�鵥������
            	//System.out.println("hahaha");
                byte[] b = new byte[(int) (length-offSet)];  //�ֿ��ļ����ݴ洢����
                int n = 0;
                file.seek(offSet);                            //��ָ��λ�ö�ȡ�ļ��ֽ���
                while(file.getFilePointer() <= length && (n = file.read(b)) != -1){ //�ж��ļ�����ȡ�ı߽�
                	System.arraycopy(b, 0, filedata[count1-1], 0, n);
                    //System.out.println(n);
                }
                filedata[count1-1][(int) (length-offSet)]= '\0';
            }
            
        file.close();                                          //�ر��ļ���	
        return filedata;	
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	public boolean filemerge_byte(byte[][]filedata,String filename){
		try {
			int maxsize1 = 999;
			int count1 = filedata.length;
			RandomAccessFile file = new RandomAccessFile(filename,"rw");
			for(int i = 0;i<count1;i++) {
				if(i==count1-1) {
					int n = returnActualLength(filedata[i]);
					byte[] data = new byte[n];
					System.arraycopy(filedata[i], 0, data, 0, n);
					file.write(data,0,n);
					//System.out.println(n);
					continue;
				}
				byte[] data = new byte[maxsize1];
				System.arraycopy(filedata[i], 0, data, 0, maxsize1);
				file.write(data,0,data.length);
				//System.out.println(data.length);
			}
			file.close();
			RandomAccessFile file1 = new RandomAccessFile(filename,"r");
			//System.out.println(file1.length());
			file1.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return true;
	}
	
	public int returnActualLength(byte[] data) {
        int i = 0;
        for (; i < data.length; i++) {
            if (data[i] == '\0')
                break;
        }
        return i;
    }



	
}
