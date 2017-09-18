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

    private Toolbar mToolbar;
    private Button save_button;
    private EditText mEditText;
    private EditText text_contact_info;
    private String mtext_contact_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //---------------------------------------------------------------------------------------
        //SharedPreferencesクラスのオブジェクトを取得
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);

        //Geofenceの経度と緯度をPreferenceから取得する。
        mtext_contact_info =mPreference.getString("mtext_contact_info","def");

        text_contact_info = (EditText)findViewById(R.id.text_contact_info);
        text_contact_info.setText(mtext_contact_info);
        text_contact_info.setOnClickListener(new View.OnClickListener() {
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
                editor.apply();
                Toast.makeText(getApplicationContext(),"保存したよ",LENGTH_LONG).show();
            }
        });
    }

}
