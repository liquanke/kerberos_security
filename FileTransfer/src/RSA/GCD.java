package RSA;

import java.math.BigInteger;

public class GCD {
    /**
     * շת����������Լ��
     */
    public BigInteger gcd(BigInteger a, BigInteger b){
        if(b.equals(BigInteger.ZERO)){
            return a ;
        }else{
            return gcd(b, a.mod(b)) ;
        }
    }
    /**
     * ��չŷ������㷨��
     * ��ax + by = 1�е�x��y�������⣨a��b���أ�
     */
    public BigInteger[] extGcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            BigInteger x1 = BigInteger.ONE;
            BigInteger y1 = BigInteger.ZERO;
            BigInteger x = x1;
            BigInteger y = y1;
            BigInteger r = a;
            BigInteger[] result = {r, x, y};
            return result;
        } else {
            BigInteger[] temp = extGcd(b, a.mod(b));
            BigInteger r = temp[0];
            BigInteger x1 = temp[1];
            BigInteger y1 = temp[2];

            BigInteger x = y1;
            BigInteger y = x1.subtract(a.divide(b).multiply(y1));     //y=x1-a/b*y1
            BigInteger[] result = {r, x, y};
            return result;
        }
    }
}
