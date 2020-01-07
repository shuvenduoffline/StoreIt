package com.example.storeit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeActivity extends AppCompatActivity {
    RecyclerView mylist;
    Button btnscan;
    private String TAG = "HomeActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("ScanResult");
    ScanResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //assigning views
        mylist = (RecyclerView) findViewById(R.id.listViewSummery);
        btnscan = findViewById(R.id.btn_scan);
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });

        Query query = notebookRef.orderBy("scanat", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Items> options = new FirestoreRecyclerOptions.Builder<Items>()
                .setLifecycleOwner(this)
                .setQuery(query, Items.class)
                .build();

        adapter = new ScanResultAdapter(options, HomeActivity.this);
        mylist.setHasFixedSize(true);
        mylist.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        mylist.setAdapter(adapter);

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
        Items it = new Items();
        it.setDetails(rawValue);
        it.setQuantity(1);
        it.setItem("luggage");
        AddItemInServer(it);
    }

    private void AddItemInServer(Items it) {
        FirebaseFirestore.getInstance().collection("ScanResult").add(it);
    }


}
