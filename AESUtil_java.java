import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("用法: java AESUtil <encrypt|decrypt> <key> <iv> <data>");
            return;
        }
        
        try {
            // AES解密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(args[1].getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(args[2].getBytes("UTF-8"));

            if ("encrypt".equalsIgnoreCase(args[0])) {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            
            // 加密并输出结果
            byte[] encryptedBytes = cipher.doFinal(args[3].getBytes("UTF-8"));
            String  result = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.print(result);

            } else if ("decrypt".equalsIgnoreCase(args[0])) {
            
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            // 解密并输出结果
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(args[3]));
            String result = new String(decryptedData, "UTF-8");
            System.out.print(result);

            } else {
                System.out.println("无效操作，请使用'encrypt'或者'decrypt'。");
            }            
            } catch (Exception e) {
            System.out.println("错误: " + e.getMessage());
            e.printStackTrace();
            }
    }
}
