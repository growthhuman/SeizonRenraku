package jp.techacademy.original.seizonrenraku;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mSettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar)findViewById(R.id.action_settings);
        setSupportActionBar(mToolbar);

        mSettingButton =  (Button) findViewById(R.id.setting);
        mSettingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);

            }
        });

    }



    //ToDo メニューアイテムかどうかの判断はどこでされるのか？
    //ToDo MenuItemとはどこまでが含まれるのか？
    //ToDo menuディレクトリ配下にあるxmlかどうかではない
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

}
