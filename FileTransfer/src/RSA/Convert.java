package RSA;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.math.BigInteger;

public class Convert {

    public static BigInteger[] StringToBig(String text,int n){
        String code = new String(Base64.encode(text.getBytes()));
        String codes[] = split(code,n); //�ָ�
        BigInteger[] m = new BigInteger[codes.length];
        for(int i=0; i<codes.length; i++){
            byte[] bytes = codes[i].getBytes();
            StringBuilder stringBuilder = new StringBuilder("999");      //"999"��Ϊ��ʼ�޶���  ��ΪstringתbigInteger ��λ��0�Ļ��ᶪ��
            for(int j=0; j<bytes.length; j++){
                int temp = bytes[j];
                String str = Integer.toString(temp);
                while(str.length()<3){
                    str= "0"+str;
                }
                stringBuilder.append(str);
            }
            m[i] = new BigInteger(stringBuilder.toString());
        }
        return m;
    }

    public static String BigToString(BigInteger[] d){
        StringBuilder stringBuilder_merge = new StringBuilder();
        for(int k=0; k<d.length; k++){  //�ѽ��ܺ��BigInteger������ӵ�һ���Ϊ�ַ�������
            String result = d[k].toString();
            result = result.substring(3,result.length());
            byte[] bytes2 = new byte[result.length()/3];
            for(int i=0,j=0; i<bytes2.length;i++){
                bytes2[i] = Byte.parseByte(result.substring(j,j+3));
                j+=3;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<bytes2.length; i++){
                stringBuilder.append((char)bytes2[i]);
            }
            stringBuilder_merge.append(stringBuilder.toString());
        }
        String result2 = new String(Base64.decode(stringBuilder_merge.toString()));
        return result2;
    }

    public static BigInteger[] StringToBigWithoutAscll(String text){
        String[] message_array = text.split("A");
        BigInteger[] m = new BigInteger[message_array.length];
        for(int i=0; i<m.length; i++){
            m[i] = new BigInteger(message_array[i]);
        }
        return m;
    }

    public static String BigToStringWithoutAscll(BigInteger[] d){//ֻ�ǰ���������������������ͨ��char������תΪString��
        StringBuilder stringBuilder_merge = new StringBuilder();
        for(int k=0; k<d.length; k++){
            String result = d[k].toString();
            if(k != d.length-1){
                stringBuilder_merge.append(result+"A");
            }else {
                stringBuilder_merge.append(result);
            }
        }
        return stringBuilder_merge.toString();
    }

    public static String[] split(String text, int n){
        int size = text.length()/n;
        String[] string_array;
        String[] string_array1;
        if(size > 0){
            if(size * n ==text.length()){
                string_array = new String[size];
            }else {
                string_array = new String[size+1];
            }
        }else {
            string_array1 = new String[1];
            string_array1[0] = text;
            return string_array1;
        }
        int i = 0;
        while (size>0){
            string_array[i] = text.substring(i*n,(i+1)*n);
            i++;
            size--;
        }
        if(i*n < text.length()){//������һ���СС��N���Ͱ������һ�鲹��
            string_array[i] = text.substring(i*n,text.length());
        }
        return string_array;
    }
}
