package com.example.merdekable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This is MainActivity class which is our main page.
 */
public class MainActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfo();
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConnect();
            }
        });

        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
    }

    /**
     * This method opens Info Page.
     */
    public void openInfo()
    {
        Intent intent1 = new Intent(this, MerdekaInfo.class);
        startActivity(intent1);
    }

    /**
     * This method opens Connect Page.
     */
    public void openConnect()
    {
        Intent intent3 = new Intent(this, MerdekaConnect.class);
        startActivity(intent3);
    }

    /**
     * This method opens Settings Page.
     */
    public void openSettings()
    {
        Intent intent2 = new Intent(this,MerdekaSetting.class);
        startActivity(intent2);
    }
}