package com.uz.chatuznewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDataBaseReference;
    private String mDisplayName;
    //deklaracja ArrayList, która będzie migawką z naszej bazy. Przekazywanie danych z Firebase do aplikacji
    private ArrayList<DataSnapshot> mSnapshotList;

    private ChildEventListener mListener = new ChildEventListener() {

        //Metoda wywołana gdy nowa wiadomośc czatu zostanie dodana do bazy
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            mSnapshotList.add(dataSnapshot);
        //Odswiezenie widoku czatu po dodaniu nowej wiadomosci czatu
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
//Konstruktor klasy ChildListAdapter przyjmujący 3 argumenty, activity czatu, referencje do bazy i nazwe wyswietlana autora
    public ChatListAdapter(Activity activity, DatabaseReference ref, String name )
    {
        mActivity = activity;
        mDisplayName = name;
        mDataBaseReference = ref.child("messages");
        mDataBaseReference.addChildEventListener(mListener);
        mSnapshotList = new ArrayList<>();
    }

    //statyczna klasa ViewHolder utrzymująca w widoku autora i tresc wiadomosci
    static class ViewHolder
    {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    //Pobranie liczby wiadomości znajdującej się liscie czatu
    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    //Pobranie wiadomości z m SnapShotList
    @Override
    public InstantMessage getItem(int position) {

        DataSnapshot snapshot = mSnapshotList.get(position);

        //Konwersja J-sona z migawki do obiektu wiadomości
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //Widok listy czatu aktualizujący widok przy przewijaniu
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Adapter sprawdza czy jest dostępny wiersz, który można odtworzyć, jeśli nie tworzy nowy wiersz w widoku convertView
        if(convertView == null)
        {
            //Pobranie widoku z layout chat_msg_row i umieszczenie go w convertView
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            final ViewHolder holder = new ViewHolder();
            //Połączenie autora wiadomości z wpisanym tekstem na czacie
            holder.authorName = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView) convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            //Tymczasowe przechowanie obencego widoku w convertView, obecnej pozycji na liscie
            convertView.setTag(holder);
        }
        //Pobierz wiadomosc czatu z aktualnej pozycji na liscie
        final InstantMessage message = getItem(position);
        //Pobranie widoku, który został tymczasowo zapisany w convertView
        final ViewHolder holder = (ViewHolder) convertView.getTag();


        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe, holder);

        //Zamiana starego widoku z wiadomością na aktualny
        String author = message.getAuthor();
        holder.authorName.setText(author);

        //Pobranie tresci widoku z obiektu InstantMessage i umieszczenie go w widoku wlascieicela
        String msg = message.getMessage();
        holder.body.setText(msg);

        return convertView;
    }

    //Nadanie stylu czatu dla nadawcy i dobiorcy
    private void setChatRowAppearance(boolean isItMe, ViewHolder holder){

        if(isItMe){
            //polozenie wiadomosci po prawej stronie jesli wiadomosc nalezy do nadawcy
            holder.params.gravity = Gravity.END;
            //Nick nadawcy w kolorze zielonym
            holder.authorName.setTextColor(Color.GREEN);
            //Ustawienie chmurki czatu
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }
        else{
            //Polozenie wiadomosci odbiorcy
            holder.params.gravity = Gravity.START;
            //Nick odbiorcy w kolorze niebieskim
            holder.authorName.setTextColor(Color.BLUE);
            //Ustawienie chmurki czatu
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    public void cleanUp(){

        mDataBaseReference.removeEventListener(mListener);
    }
}
