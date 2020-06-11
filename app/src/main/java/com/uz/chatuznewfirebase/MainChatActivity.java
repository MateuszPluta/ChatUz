package com.uz.chatuznewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    //Deklaracja zmiennej(referencji) do bazy danych
    private DatabaseReference mDataBaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);


        setupDisplayName();
        mDataBaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        // Wywyołanie metody Wyślij wiadomośc po naciśnięciu "enter"
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });



        //Wywołanie metody Wyślij wiadomośc po naciesnieciu przycisku sendbutton
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    // Pobranie wyswietlanej nazwy z SharedPreferences

    private void setupDisplayName(){

        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
        // Wyświetlenie nazwy użytkownika zapisanej w pamięci lokalnej
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        if(mDisplayName == null){
            mDisplayName = "Anonimowy";
        }
    }

    private void sendMessage() {

        Log.d("ChatUz", "I sent something"); //Log - info o pomyslnym wyslaniu
        // Pobranie tekstu wpisanego przez użytkownika i wysłanie do Firebase
        String input = mInputText.getText().toString();
        if(!input.equals(""))
        {
            //Utworzenie obiektu klasy InstantMessage i przekazanie do kontruktora autora wiadomosci i tresc
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            //okreslenie miejsca przechowywania wiadomosci w FireBase i zapis w bazie
            mDataBaseReference.child("messages").push().setValue(chat);
            //wyczyszczenie editText po wyslaniu wiadomosci
            mInputText.setText("");
        }
    }

    // onStart() uruchomienie adaptera

    @Override
    public void onStart(){
        super.onStart();
        mAdapter = new ChatListAdapter(this, mDataBaseReference, mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanUp();
    }

}
