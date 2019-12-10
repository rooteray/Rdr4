package com.rtry.rdr4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    int[] images = {R.drawable.eli, R.drawable.honoka, R.drawable.maki,
                    R.drawable.nico, R.drawable.rin};
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static Context mContext;
    public void onClick(View view){
        Intent intent = new Intent(this, fullscreen.class);
        startActivity(intent);
    }
    static ArrayList<String> test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fullscreen.MangaEntry ent = new fullscreen.MangaEntry("abc", mContext);
        ArrayList<String> entry = new ArrayList<>();
        entry.add("content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fiji.zip");
        entry.add("content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fiji2.zip");
        entry.add("content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fiji3.zip");
        entry.add("content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fiji4.zip");
        ent.saveEntries(entry);
        //ent.uri = "content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fiji3.zip";
        //entry.add(ent.uri);
        //ent.saveEntries(this, entry);
        test = (ArrayList<String>) ent.loadEntries();

        recycler = (RecyclerView) findViewById(R.id.recycler);
        layoutManager = new GridLayoutManager(this, 2);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(this, test );
        recycler.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
