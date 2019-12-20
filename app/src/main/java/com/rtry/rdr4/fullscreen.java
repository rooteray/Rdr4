package com.rtry.rdr4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
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


public class fullscreen extends AppCompatActivity implements GestureDetector.OnGestureListener, Serializable {
    Uri content_describer;
    int page = 0;
    int buf_page = 0;
    ImageView i;
    GestureDetectorCompat gst;
    public static ArrayList<String> entries = new ArrayList<>();
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    ZipFile zipfl;
    Enumeration<? extends ZipEntry> entriesfl;
    ArrayList<ZipEntry> entriesList;
    ZipEntry ent;
    String convertedPath;
    PdfRenderer renderer;
    File fl;
    PdfRenderer.Page pdfPage;
    Bitmap btm;
    public class OpenFile {
        public OpenFile(){}
        public Context getContextOpen(){
            return getApplicationContext();
        }
        public void openFile(Uri filePath) {
            try {

                MangaEntry man = new MangaEntry(content_describer.toString(), getContextOpen());
                try{
                    entries = (ArrayList<String>) man.loadEntries();
                } catch(Exception e){
                    entries = new ArrayList<>();
                    man.saveEntries(entries);
                }
                if(!(entries.contains(man.uri)))
                    entries.add(man.uri);
                man.saveEntries(entries);
                entries = (ArrayList<String>) man.loadEntries();

                openFile(filePath.getLastPathSegment());
            } catch(Exception e){
                e.getStackTrace();
            }
        }

        public void openFile(String path) {
            convertedPath = path.replace("raw:", "");
            if (path.contains(".pdf")) {
                try {
                    fl = new File(convertedPath);
                    renderer = new PdfRenderer(ParcelFileDescriptor.open(fl,  ParcelFileDescriptor.MODE_READ_ONLY));
                    page = 0;
                    pdfPage = renderer.openPage(page);
                    pdfPage.render(btm, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    i.setImageBitmap(btm);
                    pdfPage.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }

            } else if (path.contains(".zip")){
                try {
                    entriesList = new ArrayList<>();
                    zipfl = new ZipFile(convertedPath);
                    entriesfl = zipfl.entries();
                    while (entriesfl.hasMoreElements()) {
                        ent = entriesfl.nextElement();
                        entriesList.add(ent);
                    }
                    fullscreen.this.page = 0;
                    buf_page = 0;
                    InputStream zi = zipfl.getInputStream(entriesList.get(fullscreen.this.page));
                    i.setImageBitmap(BitmapFactory.decodeStream(zi));

                    zi.close();
                } catch (Exception e) {
                    e.getStackTrace();
                }

            }
        }

    }

        public void next_page(){
        if(convertedPath.contains(".zip")) {
            if (page + 1 < entriesList.size())
                try {
                    page++;
                    InputStream in = zipfl.getInputStream(entriesList.get(page));
                    i.setImageBitmap(BitmapFactory.decodeStream(in));
                    in.close();
                    buf_page++;
                } catch (Exception e) {
                    page--;
                    e.getStackTrace();
                }
        }
        else if(convertedPath.contains(".pdf")) {
                if (page + 1 < renderer.getPageCount())
                    try {
                        page++;
                        pdfPage = renderer.openPage(page);
                        pdfPage.render(btm, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        i.setImageBitmap(btm);
                        pdfPage.close();
                    } catch (Exception e) {
                        page--;
                        e.getStackTrace();
                    }
            }
    }

    public void prev_page(){
        if(convertedPath.contains(".zip"))
            try{
                if(page > 0) {
                    InputStream in = zipfl.getInputStream(entriesList.get(--page));
                    i.setImageBitmap(BitmapFactory.decodeStream(in));
                    in.close();
                }
            } catch (Exception ex){
                page++;
                ex.getStackTrace();
            }
        else if(convertedPath.contains(".pdf"))
            try{
                if(page > 0){
                    pdfPage = renderer.openPage(--page);
                    pdfPage.render(btm, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    i.setImageBitmap(btm);
                    pdfPage.close();
                }
            } catch (Exception e){
                e.getStackTrace();
            }
    }

    public static class MangaEntry implements Serializable {
            String uri;
            Context cont;
            public MangaEntry(String uri, Context cont) {
                this.uri = uri;
                this.cont = cont;
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
                        fis.close();
                        ois.close();
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
            i = (ImageView) findViewById(R.id.imageView);
            open = new OpenFile();
            open.openFile(content_describer);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btm = BitmapFactory.decodeResource(getResources(), R.drawable.nico);

        gst = new GestureDetectorCompat(this, this);
        setContentView(R.layout.activity_fullscreen);
        if(getIntent().hasExtra("filePath")){
            Intent intent = getIntent();
            int which = intent.getIntExtra("which", -1);
            String str = RecyclerAdapter.uriList.get(which).getLastPathSegment();
            Log.d("test rec" , str);
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
    public void onDestroy(){
        super.onDestroy();
        try {
            zipfl.close();
            System.gc();
        } catch (Exception e){
            e.getStackTrace();
        }
        try {
            btm = null;
            renderer.close();
            System.gc();
        } catch (Exception e){
            e.getStackTrace();
        }


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
                if(MainActivity.getMangaMode())
                    prev_page();
                else
                    next_page();
            } catch(Exception e){
                e.getStackTrace();
            }
        else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            try {
                if(MainActivity.getMangaMode())
                    next_page();
                else
                    prev_page();
            } catch(Exception e){
                e.getStackTrace();
            }


        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
            gst.onTouchEvent(event);
            return super.onTouchEvent(event);
    }


}

