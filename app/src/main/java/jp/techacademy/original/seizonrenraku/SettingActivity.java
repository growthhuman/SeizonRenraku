package jp.techacademy.original.seizonrenraku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button send_button;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mToolbar = (Toolbar)findViewById(R.id.action_settings);
//        setSupportActionBar(mToolbar);

//        send_button = (Button)findViewById(R.id.send_button);
//        send_button.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                //ToDo preferenceにデータ貯める
//
//            }
//        });
//
//        mEditText = (EditText) findViewById(R.id.text_current_location);
//        mEditText.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                //ToDo キーボードだして操作する。
//
//            }
//        });

    }
}
