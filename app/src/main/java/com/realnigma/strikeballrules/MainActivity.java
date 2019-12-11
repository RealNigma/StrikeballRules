package com.realnigma.strikeballrules;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;

    //Имя файла настроек
    public static final String APP_PREFERENCES = "settings";

    public static final String APP_PREFERENCES_USERNAME = "UserName";

    private SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Запрещаем поворот экрана
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Поле ввода имени
        nameEditText = findViewById(R.id.nameText);

        //Инициализируем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_USERNAME)) {
            nameEditText.setText(mSettings.getString(APP_PREFERENCES_USERNAME,""));
        }

    }


    //Функция считвания нажантия кнопок
    public void startTest(View view) {

        //Имя пользователя
        String nameText;

        //Передаем введенное имя
        Intent intent = new Intent(".TestingActivity");
        nameText = nameEditText.getText().toString();
        if (nameText.length() != 0) intent.putExtra("Имя",  nameText);
        else intent.putExtra("Имя", "Аноним");

        //Сохраняем введенное имя в настройки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_USERNAME, nameText);
        editor.apply();

        //Старт активности с тестированием
        startActivity(intent);

        // Write a message to the database
       // FirebaseDatabase database = FirebaseDatabase.getInstance();

        //DatabaseReference myRef = database.getReference();

        //myRef.child("users").child("id").add







    }





}
