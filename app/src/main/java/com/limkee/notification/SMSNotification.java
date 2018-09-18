package com.limkee.notification;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SMSNotification extends AsyncTask<String,Void,String> {
    private Context context;
    private Activity activity;

    public SMSNotification(Context ctx, Activity act) {
        context = ctx;
        activity = act;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String contactNumber = params[0];
            String deliveryDate = params[1];
            String orderNo = params[2];
            String isEnglish = params[3];
            String result = "";
            String data = "";
            String smsMsg = "";
            contactNumber = "65" + contactNumber;

            if (contactNumber != null && deliveryDate != null && orderNo != null && isEnglish != null && contactNumber.length() == 10 && orderNo.length() == 11) {

                String api_key = "98060002";
                String api_secret = "Infoatlimkee1";
                String limkeeNum = "6758 5858";

                if (isEnglish.equals("Yes")) {
                    smsMsg = "(Lim Kee) Order #" + orderNo + "\n" + "Your order for delivery on "
                            + deliveryDate + " has been placed! If you did not authorize this order, please call " + limkeeNum + ".";

                    //use long ascii for type whereby it is split into 2 or more multipart due to length of text
                    data += "ID=" + api_key
                            + "&Password=" + api_secret
                            + "&Mobile=" + contactNumber
                            + "&Type=" + "LA"
                            + "&Message=" + smsMsg;

                } else {

                    //get unicode for every character
                    orderNo = "#" + orderNo;
                    String orderID = "";
                    for (int i = 0; i < orderNo.length(); i++) {
                        char c = orderNo.charAt(i);
                        orderID += toUnicode(c);
                    }

                    String date = "";
                    for (int i = 0; i < deliveryDate.length(); i++) {
                        char c = deliveryDate.charAt(i);
                        date += toUnicode(c);
                    }

                    String num = "";
                    for (int i = 0; i < limkeeNum.length(); i++) {
                        char c = limkeeNum.charAt(i);
                        num += toUnicode(c);
                    }

                    smsMsg = "FF0867978BB0FF0900208BA2535553F700200023" + orderID + "000A60A84E8e" + date + "4E0b76848BA253555DF25B8c62100021002082E5975e672c4EBa4EB281Ea64Cd4F5c002C00208BF762E862530020" + num + "002E";

                    //use unicode for type due to chinese characters
                    data += "ID=" + api_key
                            + "&Password=" + api_secret
                            + "&Mobile=" + contactNumber
                            + "&Type=" + "U"
                            + "&Message=" + smsMsg;
                }

                URL url = new URL("https://www.commzgate.net/gateway/SendMsg");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    // Print the response output...
                    System.out.println(line);
                    if ((line.substring(0, 5)).equals("01010")) {
                        //sms is sent
                        //result = line.substring(0, 5);
                        //return result;
                    }
                }

                wr.close();
                rd.close();
            }
        } catch(Exception e){
            System.out.println(e);
        }
    return null;
    }

    private static String toUnicode(char ch) {
        String unicode = "";

        String code = String.format("\\u%04x", (int) ch);
        unicode = code.substring(2);

        return unicode;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
