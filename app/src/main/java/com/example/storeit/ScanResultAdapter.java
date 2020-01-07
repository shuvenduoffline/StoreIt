package com.example.storeit;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

public class ScanResultAdapter extends FirestoreRecyclerAdapter<Items, ScanResultAdapter.ItemsHolder> {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Activity baseactivity;


    public ScanResultAdapter(@NonNull FirestoreRecyclerOptions<Items> options, Activity activity) {
        super(options);
        //firebase user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        this.baseactivity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemsHolder holder, final int position, @NonNull final Items model) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String res = " Item : " + model.getItem() + "\n" + "Qnt : " + model.getQuantity() + "\nDetails : " + model.getDetails() + "\n" + "Date : " + simpleDateFormat.format(model.getScanat());
        holder.details.setText(res);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(baseactivity, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(baseactivity);
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // contine with delete
                                getSnapshots().getSnapshot(position).getReference().delete();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });




    }

    @NonNull
    @Override
    public ItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_result, viewGroup, false);
        return new ItemsHolder(v);
    }

    class ItemsHolder extends RecyclerView.ViewHolder {

        TextView details;

        public ItemsHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.txt_result);

        }
    }
}
