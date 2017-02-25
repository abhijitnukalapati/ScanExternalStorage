package com.diaby.scanexternalstorage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.diaby.scanexternalstorage.MainActivity.PERMISSION_READ_EXTERNAL_STORAGE;

public class InformUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform_user);

        if(shouldByPassActivity()) {
            startMainActivity();
            finish();
        }


        Button button = (Button) findViewById(R.id.okay_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    private boolean shouldByPassActivity() {
        return ContextCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void startMainActivity() {
        Intent mainActivityIntent = new Intent();
        mainActivityIntent.setClass(InformUserActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
