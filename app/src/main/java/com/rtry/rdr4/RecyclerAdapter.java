package com.rtry.rdr4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder>{
    public static ArrayList<Uri> uriList;
    private Context mContext;
    public RecyclerAdapter(Context mContext, ArrayList<String> entries){
        this.mContext = mContext;
        this.uriList = new ArrayList<Uri>();
        int n=0;
        try{
            n = entries.size();
        } catch(Exception e){
            e.getStackTrace();
        }

        for(int i=0; i<n; i++)
            this.uriList.add(Uri.parse(entries.get(i)));
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
    ZipFile zip;

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        try {
            path = uriList.get(position).getLastPathSegment();
            zip = new ZipFile(path.replace("raw:", ""));
            Enumeration<? extends ZipEntry> entries = zip.entries();
            ZipEntry entry = entries.nextElement();
            InputStream zi = zip.getInputStream(entry);
            btm = BitmapFactory.decodeStream(zi);
            zi.close();
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


        holder.album_title.setText(getZipName(zip));

    }
    private String getZipName(ZipFile zipfile){
        String name = zipfile.getName();
        return name.substring(name.lastIndexOf('/') + 1);
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
