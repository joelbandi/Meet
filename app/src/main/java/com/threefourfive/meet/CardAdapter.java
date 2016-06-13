package com.threefourfive.meet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by joel on 6/12/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Scoped_Profile> datalist;

    public CardAdapter(List<Scoped_Profile> data) {
        this.datalist = data;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext()).
                        inflate(R.layout.view_card,parent,false);

        return new CardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        TextView name = holder.name;
        ImageView photo = holder.picture;


        name.setText(datalist.get(position).name);

        Ion.with(photo)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .load(datalist.get(position).picture);



    }



    @Override
    public int getItemCount() {
        return datalist.size();
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder{

        ImageView picture;
        TextView name;


        public CardViewHolder(View v) {

            super(v);
            picture = (ImageView) v.findViewById(R.id.picture);
            name = (TextView) v.findViewById(R.id.name);

        }
    }
}
