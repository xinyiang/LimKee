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

public class SMSNotification  extends AsyncTask<String,Void,String> {
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

            String data = "";
            String smsMsg = "";
            contactNumber = "65" + contactNumber;

            if (contactNumber != null && deliveryDate != null && orderNo != null && isEnglish != null && contactNumber.length() == 10 && orderNo.length() == 11) {

                //String api_key = "98060002";
                String api_key = "98190002";
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
                        //return "sent";
                    }
                }

                wr.close();
                rd.close();
            }
            /*
            // This URL is used for sending messages
            String myURI = "https://api.bulksms.com/v1/messages";

            // change these values to match your own account
            String myUsername = "843009B3A5A34954B10AC8DBA47473C3-02-E";
            String myPassword = "V3wMbc89W0GL!8nphs74oaQAxWIhl";

            String smsMsg = "";
            String toNumber = "+65" + contactNumber;

            if (isEnglish.equals("Yes")){
                smsMsg = "{to: \"" + toNumber + "\", encoding: \"UNICODE\", body: \"[Lim Kee] Order #" + orderNo
                        + "\n" + "Your order for delivery on " + deliveryDate + " has been placed! If you did not authorize this order, please call +65 6758 5858." +  "\"}";
            } else {
                smsMsg = "{to: \"" + toNumber + "\", encoding: \"UNICODE\", body: \"[林记] 订単号 #" + orderNo
                        + "\n" + "您于 " + deliveryDate + " 下的订单已完成！若非本人亲自操作，请拨打 +65 6758 5858。" + "\"}";
            }
            // build the request based on the supplied settings
            URL url = new URL(myURI);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setDoOutput(true);

            // supply the credentials
            String authStr = myUsername + ":" + myPassword;
            String authEncoded = java.util.Base64.getEncoder().encodeToString(authStr.getBytes());
            request.setRequestProperty("Authorization", "Basic " + authEncoded);

            // we want to use HTTP POST
            request.setRequestMethod("POST");
            request.setRequestProperty("Content-Type", "application/json");

            // write the data to the request
            OutputStreamWriter out = new OutputStreamWriter(request.getOutputStream());
            out.write(smsMsg);
            out.close();
            System.out.println("test sms is going into try");
            // try ... catch to handle errors nicely
            try {
                // make the call to the API
                InputStream response = request.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(response));
                String replyText;
                while ((replyText = in.readLine()) != null) {
                    System.out.println(replyText);

                }
                System.out.println("test sms is a SUCCESS");
                in.close();
            } catch (IOException ex) {
                System.out.println("An error occurred:" + ex.getMessage());
                BufferedReader in = new BufferedReader(new InputStreamReader(request.getErrorStream()));
                // print the detail that comes with the error
                String replyText;
                while ((replyText = in.readLine()) != null) {
                    System.out.println(replyText);
                }
                System.out.println("test sms is error " + replyText);
                in.close();
            }
            request.disconnect();

            */
        } catch(Exception e){
            System.out.println("send sms FAILED in line 173 " + e);
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
