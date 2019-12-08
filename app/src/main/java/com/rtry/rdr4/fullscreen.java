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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
    Bitmap[] btm = new Bitmap[255];
    ImageView i;
    GestureDetectorCompat gst;
    public static ArrayList<String> entries = new ArrayList<>();
    public MangaEntry men = new MangaEntry();
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    Context cont;
    public class OpenFile {
        Uri uri;

        public OpenFile(Uri uri){
            this.uri = uri;
        }
        public Context getContextOpen(){
            return getApplicationContext();
        }
        public void openFile(Uri filePath){
            try {
                zip = new ZipInputStream(cont.getContentResolver().openInputStream(filePath));
                ze = zip.getNextEntry();
                Bitmap bitmap = BitmapFactory.decodeStream(zip);
                MangaEntry man = new MangaEntry(content_describer.toString(), cont);
                man.addEntry(man.uri); //zmienic na dodawanie obiektu context+string
                man.saveEntries(man); //zmienic na dodawanie obiektu context+string
                entries = (ArrayList<String>)man.loadEntries(); //zmienic na dodawanie obiektu context+string
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

    }

        public void next_page(View view){
        if(page == buf_page)
            try {
                ze = zip.getNextEntry();
                Bitmap bitmap = BitmapFactory.decodeStream(zip);
                btm[++page] = bitmap;
                i.setImageBitmap(btm[page]);
                buf_page++;
            } catch(Exception e) {
                e.getStackTrace();
            }
        else
            try{
                i.setImageBitmap(btm[++page]);
            } catch (Exception ex){
                ex.getStackTrace();
            }
    }

    public void prev_page(View view){
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
            public void saveEntries(MangaEntry entries){
                try
                {
                    //zmienic na dodawanie obiektu context+string
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
                //zmienic na wczytywanie obiektu context+string
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
        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                1
        );
        /*ImageView view = (ImageView) findViewById(R.id.imageView);
        view.setImageDrawable(getResources().getDrawable(R.drawable.nico));*/
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

