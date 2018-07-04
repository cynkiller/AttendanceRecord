package backend.util;

import java.util.Iterator;
import java.io.IOException;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

/*
import java.net.URL;
import java.net.URLConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
*/

import backend.util.PositionUtil;
import backend.util.Gps;
import backend.util.Debug;

public class Utility {

    private String[] holidays;

    private static double rad(double degree) {
        return degree * Math.PI / 180.00;
    }

    // ref: https://www.cnblogs.com/claireyuancy/p/7095725.html
    public static double latlongDistance(double longitude1, double latitude1, double longitude2, double latitude2) {

        Debug.Log("Before transmissition.");
        Debug.Log(latitude1);
        Debug.Log(longitude1);
        Debug.Log(latitude2);
        Debug.Log(longitude2);

        // GCJ-02 to gps
        Gps p1 = PositionUtil.gcj_To_Gps84(latitude1, longitude1);
        Gps p2 = PositionUtil.gcj_To_Gps84(latitude2, longitude2);
        latitude1 = p1.getWgLat();
        longitude1 = p1.getWgLon();
        latitude2 = p2.getWgLat();
        longitude2 = p2.getWgLon();

        Debug.Log("After transmissition.");
        Debug.Log(latitude1);
        Debug.Log(longitude1);
        Debug.Log(latitude2);
        Debug.Log(longitude2);

        double Lat1 = Utility.rad(latitude1); // 纬度
        double Lat2 = Utility.rad(latitude2);
        double latdiff = Lat1 - Lat2;//两点纬度之差
        double longdiff = rad(longitude1) - rad(longitude2); //经度之差
        double s = 2 * Math.asin( Math.sqrt(
                                        Math.pow(Math.sin(latdiff / 2), 2) +
                                        Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(longdiff / 2), 2)
                                    )
                                );//计算两点距离的公式
        s = s * 6378137.0;//弧长乘地球半径（半径为米）
        //s = Math.round(s * 10000d) / 10000d;//精确距离的数值
        return s;
    }

    public boolean isHoliday(String date) {
        // TBD
        return false;
    }

    public static String UrlRequest(String requestUrl) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        Debug.Log(requestUrl);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestUrl, String.class);
        return responseEntity.getBody();
    }

    public static String UrlRequest(String requestUrl, JSONObject parms) throws IOException {
        Iterator iter = parms.keys();
        if (iter.hasNext()) {
            requestUrl += "?";
        }
        while(iter.hasNext()) {
            String key = (String) iter.next();
            Debug.Log(key + " : " + parms.getString(key));
            requestUrl += key + "=" + parms.getString(key) + "&";
        }
        /* GET */
        //requestUrl += String.format("?appid=%s&secret=%s&js_code=%s&grant_type=%s", appid, appsecret, code, grant_type);
        return UrlRequest(requestUrl);

        /* POST WAY
        JSONObject requestUrlParam = new JSONObject();
        requestUrlParam.put("appid", appid);
        requestUrlParam.put("secret", appsecret);
        requestUrlParam.put("js_code", code);
        requestUrlParam.put("grant_type", "authorization_code");
        //requestUrl += requestUrlParam.toString();
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

        return result;        */
    }

}