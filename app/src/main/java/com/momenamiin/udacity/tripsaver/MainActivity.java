package com.momenamiin.udacity.tripsaver;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser ;
    private static final int Error_Dialog_Request = 9001 ;
    private FirebaseAuth mFirebaseAuth ;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesok()) {
            mFirebaseAuth = FirebaseAuth.getInstance();
            ViewPager viewPager = findViewById(R.id.viewpager);
            SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);


            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        //User Logedin
                    } else {
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };
        }else {
            // error with the Service

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Signout){
            mFirebaseAuth.signOut();
            firebaseUser = null ;
            return true ;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }




    public boolean isServicesok(){
        int avalable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (avalable == ConnectionResult.SUCCESS){
            return true ;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(avalable)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this , avalable , Error_Dialog_Request);
            dialog.show();
        }else {
        }
        return false;

    }
}
