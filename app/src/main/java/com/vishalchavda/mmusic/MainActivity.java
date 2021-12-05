package com.vishalchavda.mmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        runtimePermission();

    }

    public void runtimePermission() {
        Dexter.withContext(MainActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singlefile : files) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findSong(singlefile));
                } else {
                    if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav") && !singlefile.getName().startsWith(".")) {
                        arrayList.add(singlefile);
                    }
                }
            }
        }

        return arrayList;
    }

    void displaySong() {
        final ArrayList<File> mysong = findSong(Environment.getExternalStorageDirectory());
        item = new String[mysong.size()];
        for (int i = 0; i < mysong.size(); i++) {
            item[i] = mysong.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,item);
//        listView.setAdapter(adapter);
        CostomAdapter costomAdapter = new CostomAdapter();
        listView.setAdapter(costomAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songname = (String) listView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, Play_Song.class);
                intent.putExtra("songs", mysong);
                intent.putExtra("songname", songname);
                intent.putExtra("pos", i);
                startActivity(intent);
            }
        });
    }

    class CostomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return item.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview = getLayoutInflater().inflate(R.layout.list_layout, null);
            TextView textsong = myview.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(item[i]);
            return myview;
        }
    }
}