import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
public class md4 {
    public static void main(String[] args) {
        String md5 = getMD5("1567219895657562643b71d154e7f8c9f884554163823f8377e2911f4443aa3cbfd43c1f56e0clive");
        System.out.println(md5);
    }
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
