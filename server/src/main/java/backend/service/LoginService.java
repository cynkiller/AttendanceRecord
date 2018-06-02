package backend.service;

import java.util.List;
import java.util.Map;
import java.util.Formatter;
import java.util.HashMap;
import java.io.IOException;
import java.util.Base64;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Configuration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.BadPaddingException;

import backend.util.Debug;
import backend.util.Utility;

@Configuration
@PropertySource("classpath:application.properties")
public class LoginService {
    //private List<SessionData> sessionData;
    @Value("${miniprogram.appid}") String appid;
    @Value("${miniprogram.appsecret}") String appsecret;
    @Value("${miniprogram.auth.url}") String authurl;

    /**
     * get miniprogram session_key and openid, expire_in
     * session_key for decode encrypteddata
     * openid for user identification
     *
     * @author cynkiller
     * @param code from login callback
     * @return
     * @throws IOException JsonParseException JsonMappingException
     */
    public String getWeixinOpenidAndSessionkey(String code) throws IOException {
        String requestUrl = authurl;
        JSONObject parms = new JSONObject();
        parms.put("appid", appid);
        parms.put("secret", appsecret);
        parms.put("js_code", code);
        parms.put("grant_type", "authorization_code");
        return Utility.UrlRequest(requestUrl, parms);
    }

    public Boolean isValidData(JSONObject obj) {
        String parsedAppid = obj.getJSONObject("watermark").getString("appid");
        if (! parsedAppid.equals(appid)) {
            Debug.Log("Bad session key interpration! Parsed appid is " + parsedAppid);
            return false;
        }
        return true;
    }

    /**
     * 解密用户敏感数据获取用户信息
     *
     * @author zhy
     * @param sessionKey 数据进行加密签名的密钥
     * @param encryptedData 包括敏感数据在内的完整用户信息的加密数据
     * @param iv 加密算法的初始向量
     * @return
     */  

    public JSONObject getEncryptedInfo(String sessionKey,String encryptedData,String iv) throws BadPaddingException {
        // 被加密的数据
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] dataByte = decoder.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = decoder.decode(sessionKey);
        // 偏移量
        byte[] ivByte = decoder.decode(iv);

        Debug.Log("dataByte: 0x" + Debug.byteArrayToString(dataByte));
        Debug.Log("keyByte: 0x" + Debug.byteArrayToString(keyByte));
        Debug.Log("ivByte: 0x" + Debug.byteArrayToString(ivByte));

        try {
               // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            //AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            //parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(ivByte));// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                //Debug.Log(result);
                JSONObject info = new JSONObject(result);
                /* verification should be performed in client
                String parsedAppid = info.getJSONObject("watermark").getString("appid");
                if (! parsedAppid.equals(appid)) {
                    Debug.Log("Bad session key interpration! Parsed appid is " + parsedAppid);
                }
                */
                return info;
            }
        } catch(BadPaddingException e) {
            e.printStackTrace();
            throw new BadPaddingException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        //} catch (InvalidParameterSpecException e) {
        //    e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        */
        return null;
    }

}