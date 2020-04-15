package com.realnigma.strikeballrules;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;

    //private static final String TAG = "MainActivity";

    //Имя файла настроек
    public static final String APP_PREFERENCES = "settings";

    public static final String APP_PREFERENCES_USERNAME = "UserName";

    private SharedPreferences mSettings;

    //private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String userId;

    private static final int RC_SIGN_IN = 123;

    //private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Решение проблемы с перезапуском приложения при повторном нажатии на иконку приложения
        if (reRunProblemFix()) return;

        //Запрещаем поворот экрана
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Задаем нужный стиль текста у CollapsingToolbar (Белый вместо черного)
        CollapsingToolbarLayout mCollapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        mCollapsingToolbarLayout.setTitle(getTitle());
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        //Меню
        Toolbar mTopToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        //Поле ввода имени
        nameEditText = findViewById(R.id.nameText);

        //Инициализируем настройки
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_USERNAME)) {
            nameEditText.setText(mSettings.getString(APP_PREFERENCES_USERNAME,""));
        }

        //Сохраненное состояние пустое - значит Activity создается в первый раз
        if (savedInstanceState == null) {
            if (userId == null) {
                createSignInIntent();
            }
        }
        else
        {
            userId = savedInstanceState.getString("userId");
        }

    }

    private boolean reRunProblemFix() {
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return true;
        }
        return false;
    }

    //Сохраняем дополнительные данные формы, чтобы они не терялись при автоповороте или сворачивании на длительное время
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    savedInstanceState.putString("userId", userId);
    }



        //Добавляем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    //Действие при выборе элемента меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
               signOut();
                return true;
            case R.id.action_settings:
                //startSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Метод вызывающий snackbar с определенным сообщением
    public void snackbarMessage(String message){
                        CoordinatorLayout coordinatorLayout = findViewById(R.id.CoordinatorLayout);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout,message, Snackbar.LENGTH_LONG);
                snackbar.show();
    }

    //Авторизуемся используя FirebaseUI
    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // В списке оставлена только Google-авторизацию
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            //IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                }


            } //else {
                //createSignInIntent();

              //}
        }
    }

    //Выход из учетной записи
    public void signOut() {
        if (userId == null) {
            return;
        }
        AuthUI.getInstance()
                .signOut(this)
                //Действие при успешной выходе из учетной записи
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        snackbarMessage(getString(R.string.successful_exit));
                        userId = null;
                    }
                });
    }



    //Начать тестирование
    public void startTest(View view) {
        if (userId == null) {
            CoordinatorLayout coordinatorLayout = findViewById(R.id.CoordinatorLayout);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, R.string.need_auth, Snackbar.LENGTH_LONG);
            snackbar.setAction("Войти", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           createSignInIntent();
                            //googleSignIn();
                        }
            });
            snackbar.show();
            return;
        }
        //signOut(View view);
        //Имя пользователя
        String nameText;

        //Передаем введенное имя
        Intent intent = new Intent(".TestingActivity");
        nameText = nameEditText.getText().toString();
        if (nameText.length() != 0) intent.putExtra(getString(R.string.name),  nameText);
        else intent.putExtra("Имя", getString(R.string.anonymous));

        intent.putExtra("userId" , userId);

        //Сохраняем введенное имя в настройки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_USERNAME, nameText);
        editor.apply();

        //Старт активности с тестированием
        startActivity(intent);
    }





}
