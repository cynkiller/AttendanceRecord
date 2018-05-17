package backend.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Configuration;

@Configuration
@PropertySource("classpath:application.properties")
public class LoginService {
    //private List<SessionData> sessionData;
    @Value("${miniprogram.appid}") String appid;
    @Value("${miniprogram.appsecret}") String appsecret;
    @Value("${miniprogram.auth.url}") String authurl;

    /**
     * get miniprogram session_key and openid
     * session_key for decode encrypteddata
     * openid for user identification
     *
     * @author cynkiller
     * @param code from login callback
     * @return
     * @throws JSONException 
     */
    public String getSessionKeyOropenid(String code) throws JSONException{
        JSONObject requestUrlParam = new JSONObject();
        String requestUrl = authurl;
        requestUrlParam.put("appid", appid);
        requestUrlParam.put("secret", appsecret);
        requestUrlParam.put("js_code", code);
        requestUrlParam.put("grant_type", "authorization_code");
        //requestUrl += requestUrlParam.toString();
        //System.out.println(appid + appsecret + code + authurl);
        //System.out.println(requestUrl);

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(requestUrlParam.toString(), headers);
        //send request to wechat API for openid and session key
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(requestUrl, formEntity, String.class);
        System.out.println(result.toString());

        return result;
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
/*
    public static JSONObject getUserInfo(String encryptedData,String sessionKey,String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
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
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JSON.parseObject(result);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidParameterSpecException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            log.error(e.getMessage(), e);
        } catch (BadPaddingException e) {
            log.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
*/
}