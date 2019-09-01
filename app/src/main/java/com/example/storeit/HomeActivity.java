package com.example.storeit;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    static SQLiteDatabase myDatabase;
    ArrayList<String> notes = new ArrayList<>();
    ListView listView;
    ArrayAdapter arrayAdapter;
    Button btnscan;
    ArrayList<String> dateList = new ArrayList<>();
    private String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //assigning views
        listView = (ListView) findViewById(R.id.listViewSummery);
        btnscan = findViewById(R.id.btn_scan);

        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });

        //assign the database
        myDatabase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS usersdata (srno VARCHAR NOT NULL,details VARCHAR NOT NULL)");

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(HomeActivity.this);
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                String selectedid = dateList.get(i);
                                myDatabase.execSQL("DELETE FROM usersdata WHERE srno = '" + selectedid + "';");
                                UpdateListView();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });

       // addtempitem();
        UpdateListView();
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(HomeActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        UpdateListViewWithResult(barcode.rawValue);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    protected void UpdateListView() {
        try {

            myDatabase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);

            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS usersdata (srno VARCHAR NOT NULL,details VARCHAR NOT NULL)");


            Cursor cc = myDatabase.rawQuery("SELECT srno,details FROM usersdata", null);
            int ccsrnoIndex = cc.getColumnIndex("srno");
            int detailsIndex = cc.getColumnIndex("details");

            notes.clear();
            cc.moveToFirst();
            int slno = 1;
            String result;
            dateList.clear();
            while (cc != null) {
                String id = cc.getString(ccsrnoIndex);
                String details = cc.getString(detailsIndex);

                long time = Long.valueOf(id);
                Date date = new Date(time);
                result = Integer.toString(slno) + ": " + date.toString() + " \nDetails::  " + details;
                dateList.add(id);
                notes.add(result);
                cc.moveToNext();

                Log.d(TAG, "UpdateListView: "+result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        arrayAdapter = new ArrayAdapter(this, R.layout.item_list,R.id.list_item_text, notes);
        listView.setAdapter(arrayAdapter);
    }

    protected void UpdateListViewWithResult(String scanresult) {

        String selectedid= null;
        int slno = 1;
        try{
        myDatabase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS usersdata (srno VARCHAR NOT NULL,details VARCHAR NOT NULL)");


        Cursor cc = myDatabase.rawQuery("SELECT srno,details FROM usersdata", null);
        int ccsrnoIndex = cc.getColumnIndex("srno");
        int detailsIndex = cc.getColumnIndex("details");

        notes.clear();
        cc.moveToFirst();
        String result;
        dateList.clear();
        while (cc != null) {
            String id = cc.getString(ccsrnoIndex);
            String details = cc.getString(detailsIndex);

            long time = Long.valueOf(id);
            Date date = new Date(time);
            result = Integer.toString(slno) + ": " + date.toString() + " \nDetails::  " + details;
            if (details.equals(scanresult)) {
                selectedid = id;
                cc.moveToNext();
                continue;
            }



            dateList.add(id);
            notes.add(result);
            cc.moveToNext();

            Log.d(TAG, "UpdateListView: "+result);
        }

    } catch (Exception e) {
        e.printStackTrace();

    }
        if (selectedid == null) {
            //its a new item add it to database
            Date date = new Date();
            String id = String.valueOf(date.getTime());
            ContentValues values = new ContentValues();
            values.put("srno",id);
            values.put("details", scanresult);
            myDatabase.insert("usersdata",null,values);
            dateList.add(id);
            String tempresult = Integer.toString(slno) + ": " + id + " \nDetails::  " + scanresult;
            notes.add(tempresult);
            Toast.makeText(this, "Item Added!", Toast.LENGTH_SHORT).show();
        } else {
            //its an old item delete it
            myDatabase.execSQL("DELETE FROM usersdata WHERE srno = '" + selectedid + "';");
            Toast.makeText(this, "Item Removed!", Toast.LENGTH_SHORT).show();
        }

        arrayAdapter = new ArrayAdapter(this, R.layout.item_list,R.id.list_item_text, notes);
        listView.setAdapter(arrayAdapter);
    }
    private void addtempitem(){
        String id = String.valueOf(new Date().getTime());
        String scanresult = "Piyus_Pranjal";
        ContentValues values = new ContentValues();
        values.put("srno",id);
        values.put("details", scanresult);
        myDatabase.insert("usersdata",null,values);
        //Insert data

        String id2 = String.valueOf(new Date().getTime()+100);
        String scanresult2 = "Piyus_Pranjal_Amit";
        ContentValues values1 = new ContentValues();
        values1.put("srno",id2);
        values1.put("details", scanresult2);
        myDatabase.insert("usersdata",null,values1);

        Log.d(TAG, "addtempitem: Temp item added");
    }

}
