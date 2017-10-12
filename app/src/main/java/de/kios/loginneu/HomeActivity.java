package de.kios.loginneu;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by mh on 04.10.2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button_signin:

                break;
        }
    }
}
