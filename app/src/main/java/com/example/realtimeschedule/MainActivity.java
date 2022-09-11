package com.example.realtimeschedule;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button services, availableServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        services= findViewById(R.id.btnServices);
        availableServices= findViewById(R.id.ViewAvailableServices);

//        availableServices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent addServices= new Intent(MainActivity.this, AddService.class);
//                startActivity(addServices);
//            }
//        });

//        services.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent admin= new Intent(MainActivity.this, AdminTasks.class);
//                startActivity(admin);
//            }
//        });
    }
}