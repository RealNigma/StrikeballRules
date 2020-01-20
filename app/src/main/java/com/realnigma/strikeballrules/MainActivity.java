package com.realnigma.strikeballrules;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;


public class MainActivity extends AppCompatActivity {


    private EditText nameEditText;

    private static final String TAG = "MainActivity";

    //Имя файла настроек
    public static final String APP_PREFERENCES = "settings";

    public static final String APP_PREFERENCES_USERNAME = "UserName";

    private SharedPreferences mSettings;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String userId;

    private static final int RC_SIGN_IN = 123;


    //private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

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

        //createSignInIntent();
        //signInAnonymously();


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignIn();

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
        // Handle item selection
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Анонимная авторизация
    private void signInAnonymously() {
        //showProgressBar();
        // [START signin_anonymously]
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userId = user.getUid();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                       // hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
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
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        // В списке оставлена только Google-авторизация
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    //Авториция на Firebase используя Google-аккаунт
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userId = mAuth.getUid();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.CoordinatorLayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userId = user.getUid();


            } else {
                //createSignInIntent();

            }
        }
    } */

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
                        snackbarMessage("Вы успешно вышли из Google-аккаунта");
                        userId = null;
                    }
                });
    }



    //Начать тестирование
    public void startTest(View view) {
        if (userId == null) {
            CoordinatorLayout coordinatorLayout = findViewById(R.id.CoordinatorLayout);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,"Необходимо авторизоваться", Snackbar.LENGTH_LONG);
            snackbar.setAction("Войти", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           //createSignInIntent();
                            googleSignIn();
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
        if (nameText.length() != 0) intent.putExtra("Имя",  nameText);
        else intent.putExtra("Имя", "Аноним");

        intent.putExtra("userId" , userId);

        //Сохраняем введенное имя в настройки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_USERNAME, nameText);
        editor.apply();

        //Старт активности с тестированием
        startActivity(intent);
    }





}
