package com.rag.postapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rag.postapp.model.Picture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Raghavendra Kallubandi on 09/12/18.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    Context c;
    ArrayList<Picture> spacecrafts;

    public ImageAdapter(Context c, ArrayList<Picture> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.model, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Picture s = spacecrafts.get(position);
        Picasso.with(c).load(s.getUri()).placeholder(R.drawable.ic_categories).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return spacecrafts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.spacecraftImg);

        }
    }

}