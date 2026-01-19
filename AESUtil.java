import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AESUtil {
    // 自定义Base64编码
    private static String encodeBase64(byte[] data) {
        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i += 3) {
            int b = (data[i] & 0xFF) << 16;
            if (i + 1 < data.length) b |= (data[i + 1] & 0xFF) << 8;
            if (i + 2 < data.length) b |= (data[i + 2] & 0xFF);
            
            sb.append(chars[(b >> 18) & 0x3F]);
            sb.append(chars[(b >> 12) & 0x3F]);
            sb.append(i + 1 < data.length ? chars[(b >> 6) & 0x3F] : '=');
            sb.append(i + 2 < data.length ? chars[b & 0x3F] : '=');
        }
        return sb.toString();
    }

    // 自定义Base64解码
    private static byte[] decodeBase64(String base64) {
        final byte[] map = new byte[128];
        Arrays.fill(map, (byte) -1);
        for (int i = 0; i < 64; i++) {
            map["ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(i)] = (byte) i;
        }
        
        base64 = base64.replaceAll("[^A-Za-z0-9+/=]", "");
        byte[] bytes = new byte[base64.length() * 3 / 4];
        int pos = 0, val = 0, count = 0;
        
        for (char c : base64.toCharArray()) {
            if (c == '=') break;
            val = (val << 6) | map[c];
            if (++count % 4 == 0) {
                bytes[pos++] = (byte) (val >> 16);
                bytes[pos++] = (byte) (val >> 8);
                bytes[pos++] = (byte) val;
                val = 0;
            }
        }
        
        switch (count % 4) {
            case 2:
                bytes[pos++] = (byte) (val >> 4);
                break;
            case 3:
                bytes[pos++] = (byte) (val >> 10);
                bytes[pos++] = (byte) (val >> 2);
                break;
        }
        
        return Arrays.copyOf(bytes, pos);
    }

    // 十六进制字符串转字节数组
    private static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length");
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    public static String encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return encodeBase64(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String base64Data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return new String(cipher.doFinal(decodeBase64(base64Data)), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        if (args.length < 6 || !args[0].equals("-K") || !args[2].equals("-iv") || 
            (!args[4].equals("-e") && !args[4].equals("-d"))) {
            System.out.println("Usage: java AESUtil -K <hexKey> -iv <hexIv> <-e|-d> <data>");
            System.out.println("  -K <hexKey> AES key in hex format (required, 16/24/32 bytes)");
            System.out.println("  -iv <hexIv> Initialization vector in hex format (required, 16 bytes)");
            System.out.println("  -e: encrypt mode");
            System.out.println("  -d: decrypt mode");
            System.out.println("Example (encrypt): java AESUtil -K \"hexKey\" -iv \"hexIv\" -e \"plaintext\"");
            System.out.println("Example (decrypt): java AESUtil -K \"hexKey\" -iv \"hexIv\" -d \"ciphertext\"");
            return;
        }

        try {
            String hexKey = args[1];
            String hexIv = args[3];
            String mode = args[4];
            String data = args[5];
            
            // 将十六进制密钥和IV转换为字符串
            String key = new String(hexToBytes(hexKey), StandardCharsets.UTF_8);
            String iv = new String(hexToBytes(hexIv), StandardCharsets.UTF_8);
            
            if (mode.equals("-e")) {
                String encrypted = encrypt(data, key, iv);
                // 只输出加密结果
                System.out.println(encrypted);
            } else {
                String decrypted = decrypt(data, key, iv);
                // 只输出解密结果
                System.out.println(decrypted);
            }
        } catch (Exception e) {
            // 静默处理异常，不输出任何错误信息
        }
    }
}