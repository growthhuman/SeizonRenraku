package jp.techacademy.original.seizonrenraku;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;

public class SettingContactInfoActivity extends AppCompatActivity {

    //Preferenceの変数
    private SharedPreferences mPreference;

    private Button save_button;
    private EditText text_contact_info;
    private String mtext_contact_info;
    private EditText text_message_exit;
    private String mtext_message_exit;
    private EditText text_message_enter;
    private String mtext_message_enter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //---------------------------------------------------------------------------------------
        //SharedPreferencesクラスのオブジェクトを取得
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);

        //Geofenceの経度と緯度をPreferenceから取得する。
        mtext_contact_info =mPreference.getString("mtext_contact_info","");

        text_contact_info = (EditText)findViewById(R.id.text_contact_info);
        text_contact_info.setText(mtext_contact_info);
        text_contact_info.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
             }
         });

        //Geofenceのから出た際のメッセージをPreferenceから取得する。
        mtext_message_exit =mPreference.getString("mtext_message_exit","");
        text_message_exit = (EditText)findViewById(R.id.text_message_exit);
        text_message_exit.setText(mtext_message_exit);
        text_message_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //Geofenceに入った際のメッセージをPreferenceから取得する。
        mtext_message_enter =mPreference.getString("mtext_message_enter","");
        text_message_enter = (EditText)findViewById(R.id.text_message_enter);
        text_message_enter.setText(mtext_message_enter);
        text_message_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        save_button = (Button)findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //ToDo preferenceにデータ保存する
                SharedPreferences.Editor editor = mPreference.edit();
                editor.putString("mtext_contact_info", String.valueOf(text_contact_info.getText()));
                editor.putString("mtext_message_exit", String.valueOf(text_message_exit.getText()));
                editor.putString("mtext_message_enter", String.valueOf(text_message_enter.getText()));
                editor.apply();
                Toast.makeText(getApplicationContext(),"保存したよ",LENGTH_LONG).show();
            }
        });
    }

}
