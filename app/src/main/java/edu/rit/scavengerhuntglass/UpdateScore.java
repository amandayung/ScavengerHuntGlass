package edu.rit.scavengerhuntglass;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;




    public class UpdateScore extends AsyncTask<String, String, String> {

        String result = null;
        int code;

        InputStream is = null;
        String line;

        @Override
        protected String doInBackground(String... params) {

            String team_name = params[0];
            String score = params[1];

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("name", team_name));
            nameValuePairs.add(new BasicNameValuePair("score", score));


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://www.noella.kolash.org/hcin722/updateScore.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                is = httpEntity.getContent();
                Log.i("TAG", "Connection Successful");
            } catch (Exception e) {
                Log.i("TAG", e.toString());
            }

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                result = sb.toString();
                Log.i("TAG", "Result Retrieved");
            } catch (Exception e) {
                Log.i("TAG", e.toString());
            }

            try {
                JSONObject json = new JSONObject(result);
                code = (json.getInt("code"));

                if (code == 1) {
                    Log.i("msg", "Data Successfully Inserted");
                } else if (code == 2) {
                    Log.i("msg", "Error inserting data");
                } else {
                    Log.i("msg", "Error");
                }
            } catch (Exception e) {
                Log.i("TAG", e.toString());
            }

            return null;
        }

    }
