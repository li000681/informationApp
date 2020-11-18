package com.example.cst2335_graphicalinterfaceprogramming;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetJSONData {
    private static final String TAG = GetJSONData.class.getSimpleName();

    public GetJSONData() {
    }

    public String establishConnection(String reqUrl) {
        /**
         * START Establish connection using the HttpURLConnection library
         * @param url; store the url path
         * @param TAG; inform debug error (e) which exception was triggered
         */

        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(inputStream);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    } // End establishConnection()

    private String convertStreamToString(InputStream is) {
        /**
         * START convert data stream to readable string by initializing the BufferReader
         * @param reader; local variable to store input
         * @param sb; create local variable to build string and append
         * @return sb.toString(); input to string plus append
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
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
    } // End convertStreamToString()

} // End GetJSONData Class