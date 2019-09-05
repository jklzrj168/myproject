package com.example.routeplandemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
private TextView roteplan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        roteplan=findViewById(R.id.roteplan);
        roteplan.setClickable(true);
        roteplan.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this,RoutePlanActivity.class));

            }

        });
    }
}
