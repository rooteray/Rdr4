package com.rtry.rdr4;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder>{
    public static ArrayList<Uri> uriList;
    private int[] images;
    private Context mContext;
    public RecyclerAdapter(Context mContext, ArrayList<String> entries){
        this.mContext = mContext;
        this.uriList = new ArrayList<Uri>();
        for(int i=0; i<entries.size(); i++)
            this.uriList.add(Uri.parse(entries.get(i)));
        Log.d("urilist: ", uriList.get(0).toString());
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);

        return imageViewHolder;
    }
    String path;
    Bitmap btm;
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        path = uriList.get(position).getLastPathSegment();
        try {
            ZipFile zip = new ZipFile(path.replace("raw:", ""));
            Enumeration<? extends ZipEntry> entries = zip.entries();
            ZipEntry entry = entries.nextElement();
            InputStream zi = zip.getInputStream(entry);
            btm = BitmapFactory.decodeStream(zi);
        } catch(Exception e){
            e.getStackTrace();
        }

        holder.album.setImageBitmap(btm);

        holder.album.setOnClickListener((view) -> {
            Intent intent = new Intent(mContext, fullscreen.class);
            intent.putExtra("filePath", path );
            intent.putExtra("which", position);
            mContext.startActivity(intent);

        });
        Uri uri = this.uriList.get(position);
        Log.d("uri:  ", uri.toString());
        holder.album_title.setText(uri.getPath());
        Log.d("title:    ", holder.album_title.toString());

    }


    @Override
    public int getItemCount() {
        try{
            return uriList.size();
        } catch (Exception e){
            return 0;
        }
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView album;
        TextView album_title;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            album = itemView.findViewById(R.id.album);
            album_title = itemView.findViewById(R.id.album_tile);

        }
    }
}
