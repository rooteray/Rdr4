package com.rtry.rdr4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
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
    File fl;
    PdfRenderer renderer;
    PdfRenderer.Page pdfPage;
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        try {
            path = uriList.get(position).getLastPathSegment();
            if(path.contains(".zip") || path.contains(".cbz"))
                try {

                    zip = new ZipFile(path.replace("raw:", ""));
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    ZipEntry entry = entries.nextElement();
                    InputStream zi = zip.getInputStream(entry);
                    btm = BitmapFactory.decodeStream(zi);
                    zi.close();
                    holder.album_title.setText(getZipName(zip));
                    holder.album.setImageBitmap(btm);
                    zip.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            else if(path.contains(".pdf"))
                try {
                    fl = new File(path.replace("raw:", ""));
                    renderer = new PdfRenderer(ParcelFileDescriptor.open(fl,  ParcelFileDescriptor.MODE_READ_ONLY));
                    pdfPage = renderer.openPage(0);
                    btm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.nico);
                    pdfPage.render(btm, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    holder.album_title.setText(fl.getName());
                    holder.album.setImageBitmap(btm);
                    pdfPage.close();
                    renderer.close();
                } catch (Exception e){
                    e.getStackTrace();
                }
        } catch(Exception e){
            e.getStackTrace();
        }

        holder.album.setOnClickListener((view) -> {
            Intent intent = new Intent(mContext, fullscreen.class);
            intent.putExtra("filePath", path );
            intent.putExtra("which", position);
            mContext.startActivity(intent);

        });




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
    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        ImageView album;
        TextView album_title;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            album = itemView.findViewById(R.id.album);
            album_title = itemView.findViewById(R.id.album_tile);
            album.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 121, 0, "Delete");
        }
    }
    public void removeItem(int position){
        uriList.remove(position);
        notifyDataSetChanged();
    }
    public ArrayList<Uri> getUriList(){
        return uriList;
    }
    public void clearItems(){
        uriList.clear();
        notifyDataSetChanged();
    }
}
