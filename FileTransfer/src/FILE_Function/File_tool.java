package FILE_Function;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class File_tool {
	public String[] filesplit(String filename){
		try {
			int maxsize = 500;
			int count=0;//要分块的长度
			RandomAccessFile file = new RandomAccessFile(filename,"r");
			long length = file.length();                                     
			if(length%maxsize==0) count = (int) (length/maxsize);
			else count = (int) (length/maxsize)+1;
			String[] filedata = new String[count];          //用来存储分块的文件数据的数组
            long offSet = 0L;                                //初始化偏移量
            
            for(int i=0;i<count-1;i++) {
            	long begin = offSet;                         //初始读取该文件快的起末位置
                long end = (i + 1) * maxsize;
                
                byte[] b = new byte[maxsize];                //分快文件数据存储数组
                int n = 0;
                file.seek(begin);                            //从指定位置读取文件字节流
                while(file.getFilePointer() <= end && (n = file.read(b)) != -1){ //判断文件流读取的边界
                    filedata[i] = new String(b,"ISO-8859-1");
                }
                offSet += maxsize;
            }
            if (length - offSet > 0) {                       //最后一块单独处理
                byte[] b = new byte[(int) (length-offSet)];  //分快文件数据存储数组
                int n = 0;
                file.seek(offSet);                            //从指定位置读取文件字节流
                while(file.getFilePointer() <= length && (n = file.read(b)) != -1){ //判断文件流读取的边界
                    filedata[count-1] = new String(b,"ISO-8859-1");
                }
            }
            
        file.close();                                          //关闭文件流	
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
	
	public String PathtoPath(String path) {//根据文件路径获取文件名
		String fileName;
		File tempFile =new File( path.trim());  
        fileName = tempFile.getName();  
		return fileName;
	}
	
	
	/* 
	 根据路径删除指定的目录或文件，无论存在与否 
	 String  fileName  要删除的目录或文件 
	 return 删除成功返回 true，否则返回 false。 
	 */  
	public boolean DeleteFolder(String fileName) {  
	    boolean flag = false;  
	    File file = new File(fileName);  
	    // 判断目录或文件是否存在  
	    if (!file.exists()) {  // 不存在返回 false  
	    	System.out.println("文件不存在");
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
			byte[][] filedata = new byte[count1][maxsize1];    //用来存储分块的文件数据的数组
			//System.out.println(count1);
            long offSet = 0L;                                //初始化偏移量
            
            for(int i=0;i<count1-1;i++) {
            	long begin = offSet;                         //初始读取该文件快的起末位置
                long end = (i + 1) * maxsize1;
                
                byte[] b = new byte[maxsize1];                //分快文件数据存储数组
                int n = 0;
                file.seek(begin);                            //从指定位置读取文件字节流
                while(file.getFilePointer() < end && (n = file.read(b)) != -1){ //判断文件流读取的边界
                	//System.out.println(n);
                	//System.out.println(filedata[i].length);
                	System.arraycopy(b, 0, filedata[i], 0, n);
                }
                offSet += maxsize1;
                
            }
            //num = (int) (length-offSet);
            //System.out.println("length "+length);
           // System.out.println("offset "+offSet);
            if (length - offSet > 0) {                       //最后一块单独处理
            	//System.out.println("hahaha");
                byte[] b = new byte[(int) (length-offSet)];  //分快文件数据存储数组
                int n = 0;
                file.seek(offSet);                            //从指定位置读取文件字节流
                while(file.getFilePointer() <= length && (n = file.read(b)) != -1){ //判断文件流读取的边界
                	System.arraycopy(b, 0, filedata[count1-1], 0, n);
                    //System.out.println(n);
                }
                filedata[count1-1][(int) (length-offSet)]= '\0';
            }
            
        file.close();                                          //关闭文件流	
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
