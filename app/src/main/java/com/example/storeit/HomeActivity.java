package com.example.storeit;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    ArrayList<Items> list_items = new ArrayList<>();
    ArrayList<String> listitems = new ArrayList<>();
    RecyclerView listView;
    ArrayAdapter arrayAdapter;
    Button btnscan;
    private String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //assigning views
        listView = (RecyclerView) findViewById(R.id.listViewSummery);
        btnscan = findViewById(R.id.btn_scan);

        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });



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

    private void UpdateListViewWithResult(String rawValue) {
        boolean isold = true;
        for (Items i : list_items) {
            if (i.getDetails().equals(rawValue)) {
                Database.getInstance(getApplicationContext()).removeItem(i);
                list_items.remove(i);
                UpdateListViewWithOutDB();
                isold = false;
            }
        }

        if (isold) {
            Items it = new Items();
            it.setScanat(new Date());
            it.setDetails(rawValue);
            it.setQuantity(1);
            it.setItem("luggage");
            Database.getInstance(HomeActivity.this).addScanResult(it);
            UpdateListView();
            AddItemInServer(it);
        }
    }

    private void AddItemInServer(Items it) {
        FirebaseFirestore.getInstance().collection("ScanResult").add(it);
    }

    protected void UpdateListViewWithOutDB() {


        listitems.clear();
        int j = 0;
        for (Items i : list_items) {
            j++;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String res = String.valueOf(j) + " : " + i.getItem() + "\n" + "Qnt : " + i.getQuantity() + "\nDetails : " + i.getDetails() + "\n" + "Date : " + simpleDateFormat.format(i.getScanat());
            listitems.add(res);
        }

        arrayAdapter = new ArrayAdapter(this, R.layout.item_list, R.id.list_item_text, listitems);
        listView.setAdapter(arrayAdapter);
    }


    protected void UpdateListView() {
        try {

            list_items.clear();
            list_items = Database.getInstance(getApplicationContext()).getAllItems();
        } catch (Exception e) {
            Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show();
        }

        listitems.clear();
        int j = 0;
        for (Items i : list_items) {
            j++;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String res = String.valueOf(j) + " : " + i.getItem() + "\n" + "Qnt : " + i.getQuantity() + "\nDetails : " + i.getDetails() + "\n" + "Date : " + simpleDateFormat.format(i.getScanat());
            listitems.add(res);
        }

        arrayAdapter = new ArrayAdapter(this, R.layout.item_list, R.id.list_item_text, listitems);
        listView.setAdapter(arrayAdapter);
    }


}
