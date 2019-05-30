package com.example.furnishar;

import androidx.annotation.NonNull;

import com.example.furnishar.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataModel  {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userDatabase = database.getReference().child("users");

    private static DataModel instance = null;

    public static DataModel getInstance() {
        if(instance == null)
            instance = new DataModel();
        return instance;
    }

    public String verifyUser(String email) {
        final String[] userId = new String[1];
        userId[0] = null;

        if(userDatabase != null) {
            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot userDetails : dataSnapshot.getChildren()) {
                        if(userDetails.child("email").getValue() == email) {
                            userId[0] = userDetails.getKey();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return userId[0];
    }

    public void addUser(String userId, String firstName, String email) {
        User user = new User(firstName, email);
        userDatabase.child(userId).setValue(user);
    }

    public void addPhoto(String userId, String photoName, String photoEncoded){
        userDatabase.child(userId).child("Photos").child(photoName).setValue(photoEncoded);
    }

    public List<String> getPhotoList(String userId) {
        List<String> photoList = new ArrayList<>();

        userDatabase.child(userId).child("Photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot photo : dataSnapshot.getChildren()) {
                    photoList.add(Objects.requireNonNull(photo.getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return photoList;
    }

}
