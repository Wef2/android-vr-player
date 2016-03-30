package com.neointernet.neo360.activity;

/**
 * Created by neo-202 on 2016-03-22.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.neointernet.neo360.R;
import com.neointernet.neo360.adapter.VideoFileAdapter;

import java.io.File;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private String folderPath = Environment.getExternalStorageDirectory() + File.separator + "360Videos";
    private File folder;
    private File[] files;
    private ArrayList<File> fileArrayList;
    private FileObserver fileObserver;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        folder = new File(folderPath);
        Log.i("Folder", folder.toString());
        Log.i("Folder", folder.getAbsolutePath().toString());
        Log.i("Folder", folder.listFiles().toString());

        fileArrayList = new ArrayList<>();
        setFileArray();
        adapter = new VideoFileAdapter(this, fileArrayList);
        recyclerView = (RecyclerView) findViewById(R.id.video_recyler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        fileObserver = new FileObserver(folderPath) {
            @Override
            public void onEvent(int event, String path) {
                Log.i("EVENT", Integer.toString(event));
                switch (event) {
                    case FileObserver.CREATE:
                        Log.i("FILE OBSERVER", "CREATE");
                        break;
                    case FileObserver.DELETE:
                        Log.i("FILE OBSERVER", "DELETE");
                        break;
                }

            }
        };
    }

    public void setFileArray() {
        files = folder.listFiles();
        fileArrayList.clear();
        for (File file : files) {
            fileArrayList.add(file);
        }
    }

    @Override
    public void onClick(View v) {
        File newFile = (File)v.getTag();
        Intent intent = new Intent(ListActivity.this, VideoActivity.class);
        intent.putExtra("videopath", newFile.getPath());
        startActivity(intent);
    }
}