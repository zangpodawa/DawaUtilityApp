package com.dawa369.dawautilityapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {
    //to output an amount with 2 decimal places
    //String.format("%.2f", total)

    // return current date and time
    static String getCurrentTime(){

        Date currentDateTime = Calendar.getInstance().getTime();
        DateFormat formatter = DateFormat.getDateTimeInstance();
        return formatter.format(currentDateTime);
    }

    //get info from api
    static String getInfo(String api_url){

        try {

            URL url = new URL(api_url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            final StringBuilder builder = new StringBuilder();
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    builder.append(line);
                }
                reader.close();
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
