package com.example.delighter.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.delighter.Chat.ChatActivity;
import com.example.delighter.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId,mMatchName,mMatchUserSex;
    public ImageView mMatchImage;
    public MatchesViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
     
        mMatchName=(TextView)itemView.findViewById(R.id.matchName);
        mMatchUserSex=(TextView)itemView.findViewById(R.id.userSex);
        mMatchId=(TextView)itemView.findViewById(R.id.userId);
        mMatchImage=(ImageView)itemView.findViewById(R.id.matchImage);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(itemView.getContext(), ChatActivity.class);

        Bundle b=new Bundle();
        b.putString("matchId",mMatchId.getText().toString());
        b.putString("userSex",mMatchUserSex.getText().toString());
        intent.putExtras(b);

        v.getContext().startActivity(intent);
    }
}
