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
        } catch(Exception e){
            System.out.println("test sms is an ERROR " + e);
        }

    return null;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
