package com.example.delighter.Matches;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.delighter.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolder> {
    private List<MatchesObject> matchesList;
    private Context context;


    public MatchesAdapter(List<MatchesObject>matchesList,Context context){
        this.matchesList=matchesList;
        this.context=context;
    }

    @NonNull
    @Override
    public MatchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolder rcv=new MatchesViewHolder((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolder holder, int position) {

        holder.mMatchName.setText(matchesList.get(position).getName());
        if(matchesList.get(position).getProfileImage()!="default"){
            Glide.with(context).load(matchesList.get(position).getProfileImage()).into(holder.mMatchImage);
        }
        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mMatchUserSex.setText(matchesList.get(position).getUserSex());




    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}
