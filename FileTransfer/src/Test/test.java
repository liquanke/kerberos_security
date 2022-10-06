package Test;

public class test {
	public static void main(String[]argc)
    {
		//string ת byte[]

		String str = "Hello444545";

		byte[] srtbyte = str.getBytes();

		// byte[] ת string

		String res = new String(srtbyte);

		System.out.println(res);
    }
}