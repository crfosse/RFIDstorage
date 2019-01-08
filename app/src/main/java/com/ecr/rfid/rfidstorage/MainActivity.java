package com.ecr.rfid.rfidstorage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private StorageReference fireStorage;
    private DatabaseReference fireRealTime;

    private int iterator = 0;
    private ImageButton currentBtn;
    private String[] UUIDs = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Picasso.get().setLoggingEnabled(true);

        fireStorage = FirebaseStorage.getInstance().getReference();
        fireRealTime = FirebaseDatabase.getInstance().getReference();

        configureAddItemBtn();
        loadSlotBtns();
        configureSlotBtnListners();
    }

    private void configureAddItemBtn() {
        Button addItemBtn = findViewById(R.id.addItemBtn);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddItemActivity.class));
                finish();
            }
        });
    }

    private void loadSlotBtns() {
        final ImageButton[] slotBtns = {
            findViewById(R.id.imageBtnSlot1),
            findViewById(R.id.imageBtnSlot2),
            findViewById(R.id.imageBtnSlot3),
            findViewById(R.id.imageBtnSlot4)
        };

        final DatabaseReference items = fireRealTime.child("items");

            items.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    iterator = 0;
                    currentBtn = slotBtns[0];
                    for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                        if(iterator > 3) break;

                        UUIDs[iterator] = uniqueKeySnapshot.getKey();
                        currentBtn = slotBtns[iterator];

                        if (uniqueKeySnapshot.hasChild("download_url")) {
                            String urlString = uniqueKeySnapshot.child("download_url").getValue(String.class);
                            Uri downloadUri = Uri.parse(urlString);
                            Picasso.get().load(downloadUri).rotate(90f).fit().centerCrop().into(currentBtn);
                        }

                        iterator++;
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    public void configureSlotBtnListners() {

        final ImageButton[] slotBtns = {
                findViewById(R.id.imageBtnSlot1),
                findViewById(R.id.imageBtnSlot2),
                findViewById(R.id.imageBtnSlot3),
                findViewById(R.id.imageBtnSlot4)
        };

        final DatabaseReference items = fireRealTime.child("items");
        for(int i = 0; i<slotBtns.length; i++) {
            currentBtn = slotBtns[i];
            currentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    items.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int currentPosition;
                            switch (v.getId()) {
                                case R.id.imageBtnSlot1:
                                    if(UUIDs[0] == null) {
                                        toaster("No item.");
                                    }
                                    else if (dataSnapshot.child(UUIDs[0]).child("storage position").exists()) {
                                        currentPosition = dataSnapshot.child(UUIDs[0]).child("storage position").getValue(Integer.class);
                                        fireRealTime.child("rotation position").setValue(currentPosition);
                                        startActivity(new Intent(MainActivity.this, RemoveItemActivity.class));
                                        finish();
                                    } else {
                                        toaster("Not stored.");
                                    }
                                    break;
                                case R.id.imageBtnSlot2:
                                    if(UUIDs[1] == null) {
                                        toaster("No item.");
                                    }
                                    else if (dataSnapshot.child(UUIDs[1]).child("storage position").exists()) {
                                        currentPosition = dataSnapshot.child(UUIDs[1]).child("storage position").getValue(Integer.class);
                                        fireRealTime.child("rotation position").setValue(currentPosition);
                                        startActivity(new Intent(MainActivity.this, RemoveItemActivity.class));
                                        finish();
                                    } else {
                                        toaster("Not stored.");
                                    }
                                    break;
                                case R.id.imageBtnSlot3:
                                    if(UUIDs[2] == null) {
                                        toaster("No item.");
                                    }
                                    else if (dataSnapshot.child(UUIDs[2]).child("storage position").exists()) {
                                        currentPosition = dataSnapshot.child(UUIDs[2]).child("storage position").getValue(Integer.class);
                                        fireRealTime.child("rotation position").setValue(currentPosition);
                                        startActivity(new Intent(MainActivity.this, RemoveItemActivity.class));
                                        finish();
                                    } else {
                                        toaster("Not stored.");
                                    }
                                    break;
                                case R.id.imageBtnSlot4:
                                    if(UUIDs[3] == null) {
                                        toaster("No item.");
                                    }
                                    else if (dataSnapshot.child(UUIDs[3]).child("storage position").exists()) {
                                        currentPosition = dataSnapshot.child(UUIDs[3]).child("storage position").getValue(Integer.class);
                                        fireRealTime.child("rotation position").setValue(currentPosition);
                                        startActivity(new Intent(MainActivity.this, RemoveItemActivity.class));
                                        finish();
                                    } else {
                                        toaster("Not stored.");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }

    }


    private void toaster(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
    }
}

