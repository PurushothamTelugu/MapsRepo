package com.nic.ts.tracking.localdbmanager;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.ts.tracking.R;
import com.nic.ts.tracking.modalobjects.LocationObject;
import java.util.ArrayList;

public class SavngDataLocally extends AppCompatActivity {
    RecyclerView mrecyclerView;
    ArrayList<LocationObject> dataHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savng_data_locally);

        mrecyclerView = findViewById(R.id.fetchdata_recyclerview);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataHolder = new ArrayList<>();

        Cursor cursor = new DbManager(this).readAllData();
        while (cursor.moveToNext()){
            try {

                LocationObject obj = new LocationObject(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
                dataHolder.add(obj);

            }catch (IllegalStateException ie){
                ie.printStackTrace();
            }

        }

        MyAdapter myAdapter = new MyAdapter(dataHolder);
        mrecyclerView.setAdapter(myAdapter);
    }
}