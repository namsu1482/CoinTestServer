package network;


import coin.COIN_TYPE;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RequestHttpConnection {
    private static final String TAG = "RequestHttpConnection";
    String token = "";
    String header = "Bearer ";
    static Map<String, String> requestHeader;
    private REQ_TYPE reqType;
    private COIN_TYPE coin_type;

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    Logger Logger = java.util.logging.Logger.getLogger(RequestHttpConnection.class.getSimpleName());

    public enum REQ_TYPE {
        PARAM,
        JSON
    }

    public void addHeader(String token) {
        header = header + token;
        requestHeader = new HashMap<>();
        requestHeader.put("Authorization", header);
    }

    public JSONObject request(COIN_TYPE coin_type, REQ_TYPE reqType, String requestUrl, String urlParameters) {
        this.reqType = reqType;
        InputStream inputStream;
        HttpURLConnection urlConnection = null;
        String responseData;
        this.coin_type = coin_type;

        if (coin_type.equals(COIN_TYPE.BITCOIN)) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("smavis", "smavis".toCharArray());
                }
            });
        }

        JSONObject jsonObject = null;
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);

            if (urlParameters == null || urlParameters.length() < 1) {
                urlConnection.setRequestMethod("GET");

            } else {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
            }
            urlConnection.setConnectTimeout(5000);
            if (reqType.equals(REQ_TYPE.PARAM)) {
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            } else {
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");

                if (coin_type.equals(COIN_TYPE.BITCOIN)) {

                    String encoded = Base64.getEncoder().encodeToString(("smavis" + ":" + "smavis").getBytes(StandardCharsets.UTF_8));  //Java 8
                    urlConnection.setRequestProperty("Authorization", "Basic " + encoded);
//                    urlConnection.setRequestProperty("Authorization", "basic " + Base64.encode("smavis:smavis".getBytes(),2));
                }

            }


            if (requestHeader != null) {
                for (Map.Entry<String, String> header : requestHeader.entrySet()) {
                    urlConnection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            if (urlParameters.length() > 0) {
                OutputStream os = urlConnection.getOutputStream();
                os.write(urlParameters.getBytes("UTF-8"));
                os.flush();
                os.close();

            }
            if (reqType.equals(REQ_TYPE.PARAM)) {
                Logger.info("request " + reqType + ": " + requestUrl + "?" + urlParameters);
            } else {
                Logger.info("request " + reqType + ": " + requestUrl);
                Logger.info("Json Param " + urlParameters);
            }

//            Logger.i(TAG, urlConnection.getRequestMethod());

            int responseCode = urlConnection.getResponseCode();

            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            responseData = convertStreamToString(inputStream);
            inputStream.close();

            if (responseData.isEmpty()) {
                jsonObject = new JSONObject();
                jsonObject.put("response_code", responseCode);


            } else {
                jsonObject = new JSONObject(responseData);
                Logger.info("response :" + responseData);

            }

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return jsonObject;
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
