package com.example.delighter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.delighter.Cards.arrayAdapter;
import com.example.delighter.Cards.cards;
import com.example.delighter.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private cards cards_data[];
    private com.example.delighter.Cards.arrayAdapter arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    ListView listView;
    List<cards> rowItems;
    private DatabaseReference userDb;
    private String currentUId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDb=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentUId=mAuth.getCurrentUser().getUid();
        checkUserSex();

        rowItems= new ArrayList<cards>();


        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );

        SwipeFlingAdapterView flingContainer=(SwipeFlingAdapterView)findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                cards obj=(cards)dataObject;
                String userId=obj.getUserId();
                userDb.child(notUserSex).child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "YUCKK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj=(cards)dataObject;
                String userId=obj.getUserId();
                userDb.child(notUserSex).child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "OMG YESSSS!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float v) {

            }


        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionDb=userDb.child(userSex).child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this,"It's a Match❤",Toast.LENGTH_LONG).show();
                    String key=FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    userDb.child(notUserSex).child(currentUId).child("connections").child("Matches").child(snapshot.getKey()).child("ChatId").setValue(key);
                    userDb.child(notUserSex).child(snapshot.getKey()).child("connections").child("Matches").child(currentUId).child("ChatId").setValue(key);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//logout users
    public void logoutUser(View view){
mAuth.signOut();
    Intent intent=new Intent(MainActivity.this,ChooseLoginRegistrationActivity.class);
    startActivity(intent);
    finish();
    return;

}

//Move to settings
    public void goToSettings(View view){
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);
        finish();
        return;
    }

    //Move to matchesActivity
    public void goToMatches(View view){
        Intent intent=new Intent(MainActivity.this, MatchesActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);

        finish();
        return;
    }


private String userSex;
    private String notUserSex;
public void checkUserSex(){
    final FirebaseUser user;
    user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference gaydb= FirebaseDatabase.getInstance().getReference().child("Users").child("Gay");
    gaydb.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getKey().equals(user.getUid())){
                userSex="Gay";
                notUserSex="Gay";
                getOppositeSexUser();
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



    DatabaseReference lesbiandb= FirebaseDatabase.getInstance().getReference().child("Users").child("Lesbian");
    lesbiandb.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getKey().equals(user.getUid())){
                userSex="Lesbian";
                notUserSex="Lesbian";
                getOppositeSexUser();
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

public void getOppositeSexUser(){

    DatabaseReference oppositeSexdb= FirebaseDatabase.getInstance().getReference().child("Users").child(notUserSex);
    oppositeSexdb.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            if(snapshot.exists()&& !snapshot.child("connections").child("nope").hasChild(currentUId)&& !snapshot.child("connections").child("yeps").hasChild(currentUId)&&snapshot.child("profileImageUrl").getValue()!=null){
                    String profileImageUrl="default";
                if(!snapshot.child("profileImageUrl").getValue().equals("default"))
                    {
                    profileImageUrl=snapshot.child("profileImageUrl").getValue().toString();
                }
              cards Item=new cards(snapshot.getKey(),snapshot.child("name").getValue().toString(),profileImageUrl);

                   rowItems.add(Item);
                   arrayAdapter.notifyDataSetChanged();



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



}