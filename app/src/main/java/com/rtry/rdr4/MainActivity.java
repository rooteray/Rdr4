package com.rtry.rdr4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    int[] images = {R.drawable.eli, R.drawable.honoka, R.drawable.maki,
                    R.drawable.nico, R.drawable.rin};
    private RecyclerAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    public void onClick(View view){
        Intent intent = new Intent(this, fullscreen.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        layoutManager = new GridLayoutManager(this, 2);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(images);
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
