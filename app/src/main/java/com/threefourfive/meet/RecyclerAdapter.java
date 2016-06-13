package com.threefourfive.meet;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by root on 6/11/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    ArrayList<Scoped_Profile> arrayList = new ArrayList<Scoped_Profile>();

    public RecyclerAdapter(ArrayList<Scoped_Profile> arrayList){
        this.arrayList = arrayList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_display,parent,false);// possible error R.layout.activity_display -> list_layout
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Scoped_Profile scopedProfile = arrayList.get(position);
//        holder.list_image.setImageResource(scopedProfile.getPhotoURL());// TODO get photo from URL need to
        holder.list_image.setImageResource(R.drawable.me);
        holder.list_info.setText(scopedProfile.getName());
        holder.list_info.setText(scopedProfile.getMutual_friends());// TODO get more info-> mutual_likes etc
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        // variables for list
        CardView cv;
        ImageView list_image;
        TextView list_name, list_info;


        // Variables for head section

        public RecyclerViewHolder(View view)
        {
            super(view);
            cv = (CardView)itemView.findViewById(R.id.cv);
            list_image = (ImageView)view.findViewById(R.id.person_photo);
            list_name = (TextView)view.findViewById(R.id.person_name);
            list_info = (TextView)view.findViewById(R.id.person_info);
        }
    }

}
