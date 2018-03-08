package com.momenamiin.udacity.tripsaver;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Momen on 08/03/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService   extends com.firebase.jobdispatcher.JobService {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRefrence;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    public ArrayList<PlaceDataFirebase> placeDataFirebases;


    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String user = firebaseUser.getDisplayName();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseRefrence = mFirebaseDatabase.getReference().child(user);
            mDatabaseRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    placeDataFirebases = new ArrayList<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        placeDataFirebases.add(userSnapshot.getValue(PlaceDataFirebase.class));
                    }
                    FavouritFragment favouritFragment = new FavouritFragment();
                    favouritFragment.updatedata(placeDataFirebases);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return false;
    }
        @Override
        public boolean onStopJob (com.firebase.jobdispatcher.JobParameters job){
            return false;
        }
    }

