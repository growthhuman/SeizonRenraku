package jp.techacademy.original.seizonrenraku;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by kenta on 2017/09/17.
 */

public class MessageClient extends Application{

    private static final String TAG = "MessageClient";

    public MessageClient(){
    }

    /**
     *
     * @param phoneNumber
     * @param message
     */
    //ToDo インターフェースとか入れときたい　汎用化したい。
    //ToDo できない・・・
//09-17 17:28:02.270 14775-14894/jp.techacademy.original.seizonrenraku E/AndroidRuntime: FATAL EXCEPTION: IntentService[GeofenceTransitionsIS]
//    Process: jp.techacademy.original.seizonrenraku, PID: 14775
//    java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String android.content.Context.getPackageName()' on a null object reference
//    at android.content.ContextWrapper.getPackageName(ContextWrapper.java:133)
//    at android.app.PendingIntent.getBroadcastAsUser(PendingIntent.java:526)
//    at android.app.PendingIntent.getBroadcast(PendingIntent.java:515)
//    at jp.techacademy.original.seizonrenraku.MessageClient.sendMessage(MessageClient.java:32)
//    at jp.techacademy.original.seizonrenraku.GeofenceTransitionsIntentService.onHandleIntent(GeofenceTransitionsIntentService.java:93)
//    at android.app.IntentService$ServiceHandler.handleMessage(IntentService.java:66)
//    at android.os.Handler.dispatchMessage(Handler.java:102)
//    at android.os.Looper.loop(Looper.java:148)
//    at android.os.HandlerThread.run(HandlerThread.java:61)

    void sendMessage(String phoneNumber, String message){
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        //ToDo getApplicationContext()はもともとthisだった。extendsでApplicationを継承したらエラーでないけど。。。
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        Log.w(TAG,"sendMessage");
    }


}
