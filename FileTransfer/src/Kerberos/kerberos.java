package Kerberos;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import DES.DES;
import RSA.RSA;

public class kerberos {
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
	
	public String generateHash(String input){//md5 hash
        try {
            //参数校验
            if (null == input) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            BigInteger bi = new BigInteger(1, digest);
            String hashText = bi.toString(16);
            while(hashText.length() < 32){
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	//C->AS
	public  String CtoAS(String IDc,String Kc) {
			String message = IDc+" "+Kc;
			//进行加密
			BigInteger[] pubkey = new BigInteger[2];// as的公钥 
		    BigInteger[] selfkey = new BigInteger[2];//
		    pubkey[0]=new BigInteger("25210376174222502674597043575587868433359845921729777429392593376801244718803287391093663295857829689208086882496347648829616115541377572451813241929958704483188200039335156767743378800207443537960952226191738855785981692041259305829486869081914154060612653741010933367421479008336912956487753130634749042193278479439430114897156732011964917535436211234837825681273904534007141953227436227382619843005754349476204056079297744670168472659413655833978852141561891871277772065229804420765889528128945186167113014241572555603967433653284618158672392942089685732576941187113403406438710612351646692434368229939197562886053");
	        pubkey[1]=new BigInteger("65537");
	        RSA rsa_1=new RSA(pubkey,selfkey);
	        message=rsa_1.encrypt_string(message);
			//message=test.encrypt_string(message);
	        
			int length = message.length();
			String length1 = getlength(length);
			String cpackage = "1001"+length1+message;
			return cpackage;
		}
	
	//AS解析C->AS的包
	public String[] GetCtoAS(String cpackage) {
			
		String []result;
		char[]ss=cpackage.toCharArray();
		String message="";
		for(int i=8;i<cpackage.length();i++) {
			message=message+ss[i];
		}
			
		//先进行rsa解密
		//message = test.decrypt_string(message);
		BigInteger[] pubkey1 = new BigInteger[2];// 
		BigInteger[] selfkey1 = new BigInteger[2];//as的私钥 
	    selfkey1[0]=new BigInteger("25210376174222502674597043575587868433359845921729777429392593376801244718803287391093663295857829689208086882496347648829616115541377572451813241929958704483188200039335156767743378800207443537960952226191738855785981692041259305829486869081914154060612653741010933367421479008336912956487753130634749042193278479439430114897156732011964917535436211234837825681273904534007141953227436227382619843005754349476204056079297744670168472659413655833978852141561891871277772065229804420765889528128945186167113014241572555603967433653284618158672392942089685732576941187113403406438710612351646692434368229939197562886053");
	    selfkey1[1]=new BigInteger("7966212676686052274108216357276792764185865224119229760063646737570791718902257939517504206691178952863424802014386735120809926251527963561112050095785812764428405856456532363446566543685184677779473574503024550474875182881157803445730557880546259615808283051140507180767069117958544492666812328643590916803743230918998859396916879335178013369522311005488324026610045994121717660760451950068281301680966849291309938724927135552896273162877329023669569465557061157101029234954680266130151096816933472713677410888633942626849606657904456520762801554926516494475475413862039683685098803478093492093451471858905326660669"); 
	    RSA rsa_2=new RSA(pubkey1,selfkey1);
	    message=rsa_2.decrypt_string(message);
			
		result = message.split(" ");
		return result;
	}

	//AS->C
	public String AStoC(String IDc,String result) {//result表示注册结果，00是失败，11是成功
			
		String message = IDc+" "+result;
			
		//进行加密
		//message=test.encrypt_string(message);
		BigInteger[] pubkey = new BigInteger[2];// client的公钥
		BigInteger[] selfkey = new BigInteger[2];// 
		pubkey[0]=new BigInteger("15953647027545295465747805382866819526117440265215991308033458652664612350105479503548918269729662417554196828789324434596466902083041772175917887059400125691500007961017266938917738050521832462742456217678582185049805673470256119849702409420801653989737607638660404802938371025939922085578897008974600311573073492980981836831285026649067928786525625207998174712353145920235093522356288006523516279472152289699410257843345037745127281744055032154485461900797286285244753248225027717169101087813977000729794268302831580540477753061465428433116763121401082465834870860517886585078606317057802511464076412575658383250809");
	    pubkey[1]=new BigInteger("65537");
	    RSA ras_1=new RSA(pubkey,selfkey);
	    message=ras_1.encrypt_string(message);
			
	    int length = message.length();
		String length1 = getlength(length);
		String cpackage = "1002"+length1+message;
		return cpackage;
	}
	
	
	//C解析AS->C的包
	public String[] GetAStoC(String cpackage) {
		String []result;
		char[]ss=cpackage.toCharArray();
		String message="";
		for(int i=8;i<cpackage.length();i++) {
			message=message+ss[i];
		}
		
		//先进行rsa解密
		//message = test.decrypt_string(message);
		BigInteger[] pubkey1 = new BigInteger[2];// 
	    BigInteger[] selfkey1 = new BigInteger[2];//client的私钥 
        selfkey1[0]=new BigInteger("15953647027545295465747805382866819526117440265215991308033458652664612350105479503548918269729662417554196828789324434596466902083041772175917887059400125691500007961017266938917738050521832462742456217678582185049805673470256119849702409420801653989737607638660404802938371025939922085578897008974600311573073492980981836831285026649067928786525625207998174712353145920235093522356288006523516279472152289699410257843345037745127281744055032154485461900797286285244753248225027717169101087813977000729794268302831580540477753061465428433116763121401082465834870860517886585078606317057802511464076412575658383250809");
        selfkey1[1]=new BigInteger("59396827360438410266604582349199749216055898571992948703177806601616879219764972441001816650350758043291942356601540534988448114931446242747210956291768476871477210468715582542623679514279370751013310299732579354443331008846033435209536412998391802699177201639274589497794566890906525914845825567081228558255122121273652755788489617213322121985060793098535126275056223374966967314489939092202193956148368913056284695478569383618140081506428293737561957811924023654777353332693279350674527681132964824706390897122890044823285611906156733033490332548458987162603441485582017746100262582508091460872276712559219383121"); 
        RSA rsa_2=new RSA(pubkey1,selfkey1);
        message=rsa_2.decrypt_string(message);
		
		result = message.split(" ");
		return result;
	}

	
	
	//生成c->as的发送报文
	public String Client_CToAS(String IDc, String IDtgs) {
		String result = null;
			
		String packageType;
		String dataLength;
		String data;
		//String verify;
			
		Date TS1 = new Date();//数据包内容
		data = IDc + " " + IDtgs + " " +  TS1.getTime();
			
		packageType = "1003";
		dataLength = getlength(data.length());//调用getlength函数得到数据长度
		result = packageType + dataLength + data;
			
		return result;
		}
	//AS解析从client发过来的包
	public String[] AS_parseClient(String packageFromClient) {
			//if (packageFromClient.length() == 0)
				//return null;
			
		String[] message = new String[4];//存储包的解析结果
			
		char[] ss = packageFromClient.toCharArray();//将字符串转换为字符数组
			
		char[] PType = new char[4];
		for(int i = 0; i < 4; ++i) {
			PType[i] = ss[i];
		}
		message[0] = new String(PType);//数据包类型
			
		char[] dataLen = new char[4];
		for (int i = 4; i < 8; ++i) {
			dataLen[i-4] = ss[i];
		}
		message[1] = new String(dataLen);//数据长度
			
		char[] data = new char[Integer.parseInt(message[1])];
		for (int i = 8; i < 8 + Integer.parseInt(message[1]); ++i) {
			data[i -8] = ss[i];
		}
		message[2] = new String(data);//获取数据
			
		String[] result = new String[3];//存储数据包解析内容
		result = message[2].split(" ");

		return result;
	}	
	//生成as->client的数据包
		public String asToClient(String Kc, String Ktgs, String Kctgs, String IDc, String ADc, String IDtgs) {
			String dataType = "1004";
			String datalen;
			String data;
			
			String ticketTgs;
			Date date = new Date();
			long TS2 = date.getTime();
			long lifetime2 = 10000000;
			//生成票据
			ticketTgs = Kctgs + " " + IDc + " " + ADc + " "
						+ IDtgs + " " + TS2 + " " + lifetime2;
			DES des = new DES(Ktgs);
			ticketTgs = des.encrypt_string(ticketTgs);//加密用tgs的sessionkey票据
			
			data = Kctgs + " " + IDtgs + " " + TS2 + " "
					+ lifetime2 + " " + ticketTgs;
			DES des1 = new DES(Kc);
			data = des1.encrypt_string(data);//加密数据用client的sessionkey
			
			datalen = getlength(data.length());//调用getlength函数获取数据长度
			
			String result;
			result = dataType + datalen + data;
			
			return result;
		}
		
	//客户端解析从AS返回的报文
	public String[] Client_parseAS(String Kc, String packageFromAS) {
		char[] ss = packageFromAS.toCharArray();
			
		String[] message = new String[4];
			
		char[] DType = new char[4];
		for (int i = 0; i < 4; ++i) {
			DType[i] = ss[i];
		}
		message[0] = new String(DType);
			
		char[] dataLen = new char[4];
		for (int i = 4; i < 8; ++i) {
			dataLen[i-4] = ss[i];
		}
		message[1] = new String(dataLen);
			
		int datalength = Integer.parseInt(message[1]);
		char[] data = new char[datalength];
		for (int i = 8; i < 8 + datalength; ++i) {
			data[i-8] = ss[i];
		}
		message[2] = new String(data);
			
		DES des = new DES(Kc);
		String m = des.decrypt_string(message[2]);//解密数据包
		String[] result = new String[5];
		result = m.split(" ");
			
		return result;
		}

	
	//c->tgs
	public String CtoTGS(String IDv,String Tickettgs,String IDc,String ADc,String EKctgs) {
		//生成认证
		Date TS_3 = new Date();
		DES des = new DES(EKctgs);
		String Authenticatortgs= des.encrypt_string(IDc+" "+ADc+" "+TS_3.getTime());
			
		String message = IDv+" "+Tickettgs+" "+Authenticatortgs;
		int length = message.length();
		String length1 = getlength(length);
		String cpackage = "1005"+length1+message;
		return cpackage;
	}
	//tgs获取c->tgs的包
	public String[] GetCtoTGS(String cpackage,String EKtgs) {
		String []result;
		char[]ss=cpackage.toCharArray();
		String message="";
		for(int i=8;i<cpackage.length();i++) {
			message=message+ss[i];
		}
		result = message.split(" ");
		//解密标签
		DES des = new DES(EKtgs);
		String []Tickettgs = des.decrypt_string(result[1]).split(" ");
		//解密认证
		DES des1 = new DES(Tickettgs[0]);
		String []Authenticatortgs =des1.decrypt_string(result[2]).split(" ");
		//合并数组
		int l = Tickettgs.length;
		int l1 = Authenticatortgs.length;
		result = Arrays.copyOf(result, l+1+l1);// 数组扩容
	    System.arraycopy(Tickettgs, 0, result, 1, l);
	    System.arraycopy(Authenticatortgs, 0, result, 1+l, l1);	
		return result;
	}
	//TGS->c
	public String TGStoC(String Kcv,String EKctgs,String EKv,String IDv,String IDc,String ADc) {
		//生成票据
		String Ticketv1,Ticketv2;
		Date TS_4 = new Date();
		TS_4.getTime();
		long lifetime = 10000000;
		Ticketv1 = Kcv+" "+IDc+" "+ADc+" "+IDv+" "+TS_4.getTime()+" "+lifetime;
		DES des = new DES(EKv);
		Ticketv2 = des.encrypt_string(Ticketv1);
		//生成message
		String message1=Kcv+" "+IDv+" "+TS_4.getTime()+" "+Ticketv2;
		DES des1 = new DES(EKctgs);
		String message2=des1.encrypt_string(message1);
		//生成包		
		int length = message2.length();
		String length1 = getlength(length);
		String cpackage = "1006"+length1+message2;
		return cpackage;
	}
	//c获取tgs->c的包
	public String[] GetTGStoC(String cpackage,String EKctgs) {
		String []result;
		char[]ss=cpackage.toCharArray();
		String message1="";
		for(int i=8;i<cpackage.length();i++) {
			message1=message1+ss[i];
		}
			//解密数据包
			DES des1 = new DES(EKctgs);
			String message2=des1.decrypt_string(message1);
			result = message2.split(" ");
			return result;
		}
	
	//c -->v
	public String CtoV(String  IDc,String Kcv ,String ADc,String Ticketv) {
		//生成认证包Authenticator，加密
		String Authenticator1,Authenticator;
		Date TS_5 = new Date();
		Authenticator1 = IDc+" "+ADc+" "+TS_5.getTime();
		DES des = new DES(Kcv);
		Authenticator = des.encrypt_string(Authenticator1);
		//生成message,生成包
		String message = Ticketv+" "+Authenticator;
		int length = message.length();
		String length1 = getlength(length);
		String cpackage = "1007"+length1+message;
		return cpackage;
	}
	
	//         v获取c->v 的包
	public String[] GetCtoV(String cpackage,String EKv) {
		String [] result;
		String [] temp;
		String [] temp1;
		char[]ss=cpackage.toCharArray();
		String message1="";
		for(int i=8;i<cpackage.length();i++) {
			message1=message1+ss[i];
		}
		temp = message1.split(" ");
		//解密数据包
		//解密Ticketv
		String a = temp[0];
		DES des1 = new DES(EKv);
		String b=des1.decrypt_string(a);
		temp1 = b.split(" "); 
		//解密Authenticator
		//从ticketv获取kcv
		String Kcv = temp1[0];
		String c = temp[1];
		DES des2 = new DES(Kcv);
		String d=des2.decrypt_string(c);
		b = b+" "+ d;
		result= b.split(" "); 
		return result;
	}

    //  v ->c
	public String VtoC(String Kcv) {
		String message1,message;
		Date TS_6 = new Date();
		message1 = String.valueOf(TS_6.getTime());
		DES des = new DES(Kcv);
		message = des.encrypt_string(message1);
		int length = message.length();
		String length1 = getlength(length);
		String cpackage = "1008"+length1+message;
		return cpackage;
	}
	
//c获取v->c 的包
	public  String[] GetVtoC(String cpackage,String Kcv) {
		String [] result;
		char[]ss=cpackage.toCharArray();
		String message1="";
		for(int i=8;i<cpackage.length();i++) {
			message1=message1+ss[i];
		}
						//解密数据包
		DES des = new DES(Kcv);
		String message2=des.decrypt_string(message1);
		result= message2.split(" "); 
		return result;
	}
	
	/**
            * 生成随机7位DES密码 用于k_c_tgs k_c_v
    */
    public String create_sessionkey(){
    	//String sessionKey = new String();
    	StringBuilder stringBuilder = new StringBuilder();
    	int[] temp = new int[7];  //ascll 码值 随机49 -- 122
    	Random random = new Random();
    	for(int i=0; i<temp.length; i++){
    	    temp[i] = random.nextInt(122) % (122 - 49 + 1) + 49;
    	    stringBuilder.append((char)temp[i]);
    	}
    	return stringBuilder.toString();
    }	
}
