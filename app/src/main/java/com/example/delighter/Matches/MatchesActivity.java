package com.example.delighter.Matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.delighter.MainActivity;
import com.example.delighter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String currentUserID;
    private  String userSex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager=new LinearLayoutManager(MatchesActivity.this);
       mRecyclerView.setLayoutManager(mMatchesLayoutManager);
       mMatchesAdapter=new MatchesAdapter(getDataSetMatches(),MatchesActivity.this);
       mRecyclerView.setAdapter(mMatchesAdapter);
        userSex=getIntent().getExtras().getString("userSex");

       getUserMatchId();





    }

    private void getUserMatchId() {

        DatabaseReference matchDb= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(currentUserID).child("connections").child("Matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot match:snapshot.getChildren()){
                      FetchMatchInformation(match.getKey());
                      Log.i("matches","found");                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String name="";
                    String profileImageUrl="";
                    String gender=userSex;

                   String userId=snapshot.getKey();
                   if(snapshot.child("name").getValue()!=null){
                        name=snapshot.child("name").getValue().toString();
                   }
                   if(snapshot.child("profileImageUrl").getValue()!=null){
                       profileImageUrl=snapshot.child("profileImageUrl").getValue().toString();

                   }
                    MatchesObject obj=new MatchesObject(userId,name,profileImageUrl,gender);

                   resultsMatches.add(obj);


                }
                mMatchesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<MatchesObject> resultsMatches=new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return  resultsMatches;
    }
}