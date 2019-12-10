package com.rtry.rdr4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class fullscreen extends AppCompatActivity implements GestureDetector.OnGestureListener, Serializable {
    Uri content_describer;
    File source;
    ZipEntry ze;
    ZipInputStream zip;
    int page = 0;
    int buf_page = 0;
    Bitmap[] btm;
    ImageView i;
    GestureDetectorCompat gst;
    public static ArrayList<String> entries = new ArrayList<>();
    public MangaEntry men = new MangaEntry();
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    Context cont;
    ZipFile zipfl;
    Enumeration<? extends ZipEntry> entriesfl;
    ArrayList<ZipEntry> allEntries;
    ZipEntry ent;
    public class OpenFile {
        Uri uri;

        public OpenFile(Uri uri){
            this.uri = uri;
        }
        public OpenFile(){}
        public Context getContextOpen(){
            return getApplicationContext();
        }
        public void openFile(Uri filePath){
            try {
                zip = new ZipInputStream(cont.getContentResolver().openInputStream(filePath));
                ze = zip.getNextEntry();
                Bitmap bitmap = BitmapFactory.decodeStream(zip);
                MangaEntry man = new MangaEntry(content_describer.toString(), cont);
                entries = (ArrayList<String>)man.loadEntries();
                entries.add(man.uri);
                man.saveEntries(entries);
                entries = (ArrayList<String>)man.loadEntries();
                for(int b=0; b<entries.size(); b++)
                    Log.d("obj", entries.get(b));

                page = 0;
                buf_page = 0;
                for(int j=0; j<255; j++)
                    btm[j] = null;
                btm[page] = bitmap;
                i.setImageBitmap(btm[page]);


            } catch (Exception e){
                e.getStackTrace();
            }

        }

        public void openFile(String path){
            String convertedPath = path.replace("raw:", "");
            try {
                btm  = new Bitmap[255];
                zipfl = new ZipFile(convertedPath);
                entriesfl = zipfl.entries();
                ent = entriesfl.nextElement();

                Log.d("entry test", ent.getName());
                page = 0;
                buf_page = 0;
                for(int j=0; j<255; j++)
                    btm[j] = null;
                InputStream zi = zipfl.getInputStream(ent);
                btm[page] = BitmapFactory.decodeStream(zi);
                i.setImageBitmap(btm[page]);
            }catch (Exception e){
                e.getStackTrace();
            }

        }

    }

        public void next_page(){
        if(page == buf_page)
            try {
                page++;
                ent = entriesfl.nextElement();
                InputStream in = zipfl.getInputStream(ent);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                btm[page] = bitmap;
                i.setImageBitmap(btm[page]);
                buf_page++;
            } catch(Exception e) {
                page--;
                e.getStackTrace();
            }
        else
            try{
                i.setImageBitmap(btm[++page]);
            } catch (Exception ex){
                ex.getStackTrace();
            }
    }

    public void prev_page(){
        try{
            if(page > 0)
                i.setImageBitmap(btm[--page]);
        } catch (Exception ex){
            ex.getStackTrace();
        }
    }

    public static class MangaEntry implements Serializable {
            String uri;
            Context cont;
            //String title;
            public MangaEntry(){

            }
            public MangaEntry(String uri, Context cont) {
                this.uri = uri;
                this.cont = cont;
            }
            public void addEntry(String mangaEntry){
                entries.add(mangaEntry);
            }
            public void saveEntries(ArrayList<String> entries){
                try
                {
                    FileOutputStream fos = this.cont.openFileOutput("entries" ,Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(entries);
                    oos.close();
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
            public Object loadEntries(){
                try {
                        FileInputStream fis = this.cont.openFileInput("entries");
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        Object obj = ois.readObject();
                        return obj;
                    }
                 catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

    }
    OpenFile open;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            content_describer = data.getData();
            String src = content_describer.getPath();
            source = new File(src);
            i = (ImageView) findViewById(R.id.imageView);
            open = new OpenFile(content_describer);
            cont = open.getContextOpen();
            open.openFile(open.uri);
            open.openFile(Uri.parse(MainActivity.test.get(0)));
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gst = new GestureDetectorCompat(this, this);
        setContentView(R.layout.activity_fullscreen);
        if(getIntent().hasExtra("filePath")){
            Intent intent = getIntent();
            int which = intent.getIntExtra("which", -1);
            String str = RecyclerAdapter.uriList.get(which).getLastPathSegment();
            i = (ImageView) findViewById(R.id.imageView);
            open = new OpenFile();
            open.openFile(str);
        } else {
            Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            startActivityForResult(
                    Intent.createChooser(chooseFile, "Choose a file"),
                    1

            );
        }
        final Handler forceImmersive = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);

                forceImmersive.postDelayed(this, 1000);
            }
        };

        forceImmersive.postDelayed(runnable, 1000);

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            try{
                if(page > 0)
                    i.setImageBitmap(btm[--page]);
            } catch (Exception ex){
                try{
                    prev_page();
                } catch (Exception e){
                    e.getStackTrace();
                }
                ex.getStackTrace();
            }
        else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            if(page == buf_page)
                try {
                    ze = zip.getNextEntry();
                    Bitmap bitmap = BitmapFactory.decodeStream(zip);
                    if(bitmap != null)
                        btm[++page] = bitmap;
                    i.setImageBitmap(btm[page]);
                    buf_page++;
                } catch(Exception e) {
                    try{
                        next_page();
                    } catch(Exception ex){
                        ex.getStackTrace();
                    }
                    //page--;
                    e.getStackTrace();
                }
            else
                try{
                    i.setImageBitmap(btm[++page]);
                } catch (Exception ex){
                    //page--;
                    ex.getStackTrace();
                }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
            gst.onTouchEvent(event);
            return super.onTouchEvent(event);
    }
}

