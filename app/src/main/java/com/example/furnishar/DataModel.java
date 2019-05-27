package com.example.furnishar;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.furnishar.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class DataModel  {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userDatabase = database.getReference().child("users");

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference photoStorage = storage.getReference();

    private static DataModel instance = null;

    public static DataModel getInstance() {
        if(instance == null)
            instance = new DataModel();
        return instance;
    }

    public boolean verifyUser(String email) {
        final boolean[] emailExists = new boolean[1];

        if(userDatabase != null) {
            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot userDetails : dataSnapshot.getChildren()) {
                        if(userDetails.child("email").getValue() == email) {
                            emailExists[0] = true;
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return emailExists[0];
    }

    public void addUser(String userId, String firstName, String email) {
        User user = new User(firstName, email);

        //TODO change first name to smth more reliable => some key to be saved in shared preferences so it can be used when saving photos
        userDatabase.child(firstName).setValue(user);
    }

    public void addPhoto(String userId, String photoNamee){
        StorageReference imagesRef = photoStorage.child(userId);

    }

    private byte[] converteImageToByte(ImageView img) {
        img.setDrawingCacheEnabled(true);
        img.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        return baos.toByteArray();
    }

}
