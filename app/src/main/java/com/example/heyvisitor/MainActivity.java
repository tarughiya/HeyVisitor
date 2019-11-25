package com.example.heyvisitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button visitor,host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        visitor=findViewById(R.id.visitor);
        host=findViewById(R.id.host);

    }

    public void Visitorform(View view) {
        Intent visitorintent= new Intent(MainActivity.this, VisitorActivity.class);
        startActivity(visitorintent);
    }


    public void Hostform(View view) {
        Intent hostintent=new Intent(MainActivity.this, HostActivity.class);
        startActivity(hostintent);
    }
}
