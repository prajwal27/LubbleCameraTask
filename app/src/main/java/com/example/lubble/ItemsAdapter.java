package com.example.lubble;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Image> images;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, time;
        public CircleImageView url;


        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.item_tv_title);
            time = view.findViewById(R.id.item_tv_time);
            url = view.findViewById(R.id.item_iv_image);
        }
    }


    public ItemsAdapter(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemsAdapter.MyViewHolder holder, final int position) {

        Image image = images.get(position);
        holder.title.setText(image.getTitle());
        holder.time.setText(image.getsTime());
        Glide.with(context).load(image.getUrl()).into(holder.url);
    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }

    public void added(Image image){
        Log.d("added @ adapter", images.size()+"s");
        images.add(image);
        notifyItemInserted(images.indexOf(image));
    }


}