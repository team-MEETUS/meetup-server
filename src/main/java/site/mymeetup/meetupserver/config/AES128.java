package site.mymeetup.meetupserver.config;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class AES128 {
    private String ips;
    private Key keySpec;

    public AES128(){
        this("AES_KEY");
    }

    // 전달받은 키를 암호화 로직에 적용
    public AES128(String key) {
        try {
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            System.arraycopy(b, 0, keyBytes, 0, Math.min(b.length, keyBytes.length));
            this.ips = new String(keyBytes, "UTF-8");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            this.keySpec = keySpec;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // 암호화 하기
    @SuppressWarnings("deprecation")
    public String encrypt(String str) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ips.getBytes()));

            byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));
            String Str = new String(Base64.encodeBase64String(encrypted));

            return Str;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // 암호화 풀기
    @SuppressWarnings("deprecation")
    public String decrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ips.getBytes("UTF-8")));

            byte[] byteStr = Base64.decodeBase64(str.getBytes());
            String Str = new String(cipher.doFinal(byteStr));

            return Str;
        } catch (Exception e) {
            return null;
        }
    }

}
