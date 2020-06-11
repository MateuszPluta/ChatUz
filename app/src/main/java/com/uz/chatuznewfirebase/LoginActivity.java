package com.uz.chatuznewfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    // Objekt FireBaseAuth konieczny do uwierzytelnienia użytkownika
    private FirebaseAuth mAuth;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        //Próba logowania przy wciśnięciu przycisku lub entera
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(); // wywołanie metody próby logowania
                    return true;
                }
                return false;
            }
        });

        // Pobranie instancji FirebaseAuth

        mAuth = FirebaseAuth.getInstance();
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // wywołanie metody attemptLogin
        attemptLogin();

    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.uz.chatuznewfirebase.RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    // Próba logowania
    private void attemptLogin() {

        String email = mEmailView.getText().toString(); //email wprowadzony przez użytkownika
        String password = mPasswordView.getText().toString(); // haslo wprowadzone przez uzytkownika

        if(email.equals("") || password.equals("")) return; //przerwanie próby logowania jeśli pole jest puste
        Toast.makeText(this, "Logowanie...", Toast.LENGTH_SHORT).show(); //logowanie w trakcie - toast message

        //Logowanie przy użyciu Firebase
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("ChatUz", "signInWithEmail() onComplete: " + task.isSuccessful());

                if(!task.isSuccessful())
                {
                    Log.d("ChatUz", "Problem z logowaniem: " + task.getException()); //Log przy wyjatku
                    showErrorDialog("Wystąpił problem z logowaniem"); // Komunikat Error
                }
                else
                {
                    //Przekierowanie do MainChatActivity
                    Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    // Komunikat błedu przy logowaniu
    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Błąd")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }




}