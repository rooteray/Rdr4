package com.rtry.rdr4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    fullscreen.MangaEntry ent;
    String[] permissionArray;
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
    static ArrayList<String> entries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionArray = new String[2];
        permissionArray[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissionArray[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionArray,
                    3);
        }

        mContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ent = new fullscreen.MangaEntry("abc", mContext);
        ArrayList<String> entry = new ArrayList<>();

        try{
            InputStream is = openFileInput("entries");
            is.close();
        } catch(IOException e){
            ent.saveEntries(entry);
        }

        try {
            entries = (ArrayList<String>) ent.loadEntries();
        } catch (Exception e){
            entries = new ArrayList<>();
            ent.saveEntries(entries);
        }

        recycler = (RecyclerView) findViewById(R.id.recycler);
        layoutManager = new GridLayoutManager(this, 2);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(this, entries );
        recycler.setAdapter(adapter);

    }

    @Override
    protected void onResume(){
        super.onResume();
        entries = (ArrayList<String>) ent.loadEntries();
        adapter = new RecyclerAdapter(this, entries );
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
        if (id == R.id.action_reset) {
            entries = new ArrayList<>();
            ent.saveEntries(entries);
            adapter = new RecyclerAdapter(this, entries );
            recycler.setAdapter(adapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
