package com.example.delighter.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaDrm;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.delighter.Matches.MatchesActivity;
import com.example.delighter.Matches.MatchesAdapter;
import com.example.delighter.Matches.MatchesObject;
import com.example.delighter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserID,matchId,userSex,chatId;
    private EditText mSendEditText;
    private Button  mSendButton;

    DatabaseReference mDatabaseUser,mDatabaseChat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager=new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter=new ChatAdapter(getDataSetChat(),ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        matchId=getIntent().getExtras().getString("matchId");
        userSex=getIntent().getExtras().getString("userSex");



        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(currentUserID).child("connections").child("Matches").child(matchId).child("ChatId");
        mDatabaseChat=FirebaseDatabase.getInstance().getReference().child("Chat");
        mSendEditText=(EditText)findViewById(R.id.message);
        mSendButton=(Button)findViewById(R.id.send);
        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        getChatId();



        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String sendMessageText=mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb=mDatabaseChat.push();

            Map newMessage=new HashMap();
            newMessage.put("createdByUser",currentUserID);
            newMessage.put("text",sendMessageText);
            newMessageDb.setValue(newMessage);


        }
        mSendEditText.setText(null);
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    chatId=snapshot.getValue().toString();
                    mDatabaseChat= mDatabaseChat.child(chatId);
                    getChatMessage();
                }
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatMessage() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    String message=null;
                    String createdByUser=null;
                    if(snapshot.child("text").getValue()!=null){
                        message=snapshot.child("text").getValue().toString();
                    }

                    if(snapshot.child("createdByUser").getValue()!=null){
                        createdByUser=snapshot.child("createdByUser").getValue().toString();
                    }

                    if(message!=null && createdByUser!=null){
                        Boolean currentUserBoolean=false;
                        if(createdByUser.equals(currentUserID)){
                            currentUserBoolean=true;
                        }
                        ChatObject newMessage=new ChatObject(message,currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }



                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private ArrayList<ChatObject> resultsChat=new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return  resultsChat;
    }




}