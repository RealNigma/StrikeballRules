package com.realnigma.strikeballrules;

import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
    private String nameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Запрещаем поворот экрана
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


    //Функция считвания нажантия кнопок
    public void startTest(View view) {

        //Поле ввода имени
        nameEditText = findViewById(R.id.nameText);


        Intent intent = new Intent(".TestingActivity");

        nameText = nameEditText.getText().toString();

        //Передаем введенное имя
        if (nameText.length() != 0) intent.putExtra("Имя",  nameText);
        else intent.putExtra("Имя", "Аноним");

        //Старт активности с тестированием
        startActivity(intent);
    }





}
