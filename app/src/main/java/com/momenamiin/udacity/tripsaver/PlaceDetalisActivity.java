package com.momenamiin.udacity.tripsaver;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceDetalisActivity extends AppCompatActivity {
    @BindView(R.id.article_title)
    TextView titleView;
    @BindView(R.id.article_byline)
    TextView bylineView;
    @BindView(R.id.text_phone_num)
    TextView phonenum;
    @BindView(R.id.text_website)
    TextView website;
    @BindView(R.id.text_adress)
    TextView adress;
    @BindView(R.id.text_attribution)
    TextView attribution;
    @BindView(R.id.share_fab)
    FloatingActionButton Fab;
    @BindView(R.id.photo)
    ImageView imageView;
    @BindView(R.id.action_up)
    ImageView mUpButton;

    private PlaceData placeData;
    private GeoDataClient mGeoDataClient;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRefrence;
    private StorageReference mStorageRef;
    public PlaceDataFirebase placeDataFirebase;
    public Bitmap bitmap;
    public boolean Isfavourit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detalis);
        ButterKnife.bind(this);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() == Intent.ACTION_ANSWER) {
                Gson gson = new Gson();
                String json = intent.getStringExtra("json");
                Type type = new TypeToken<PlaceDataFirebase>() {
                }.getType();
                if (json != null) {
                    placeDataFirebase = gson.fromJson(json, type);
                }
                Isfavourit = true;

                Fab.setImageResource(R.drawable.ic_favorite_black_24dp);

                byte[] imageInByte = intent.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);

                titleView.setText(placeDataFirebase.getName());
                bylineView.setText(placeDataFirebase.getAdress());
                if (placeDataFirebase.getPhoneNumber() != null){
                    phonenum.setText(placeDataFirebase.getPhoneNumber() );
                }else {
                    phonenum.setText("Not exsist");
                }

                if (placeDataFirebase.getAdress() != null){
                    adress.setText(placeDataFirebase.getPhoneNumber() );
                }else {
                    adress.setText("Not exsist");
                }

                if (placeDataFirebase.getWebsiteUri() != null){
                    website.setText((CharSequence) placeDataFirebase.getWebsiteUri());
                }else {
                    website.setText("Not exsist");
                }
                if (placeDataFirebase.getAttributions() != null){
                    attribution.setText(placeDataFirebase.getAttributions() );
                }else {
                    attribution.setText("Not exsist");
                }
                Gson gson2 = new Gson() ;
                Type type2 = new TypeToken<PlaceDataFirebase>(){}.getType();
                String json2  = gson2.toJson(placeDataFirebase , type2);
                Intent widgetintent = new Intent(PlaceDetalisActivity.this, AppWidget.class);
                widgetintent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                widgetintent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, json2);
                sendBroadcast(widgetintent);
                imageView.setImageBitmap(bitmap);


            } else {
                FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                String user = firebaseUser.getDisplayName();
                Gson gson = new Gson();
                String json = intent.getStringExtra("json");
                Type type = new TypeToken<PlaceData>() {
                }.getType();
                if (json != null) {
                    placeData = gson.fromJson(json, type);
                    getPhotos(placeData);
                }
                mStorageRef = FirebaseStorage.getInstance().getReference();
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                mDatabaseRefrence = mFirebaseDatabase.getReference().child(user);
                placeDataFirebase = turnPlacedatatofirebase(placeData, null);
                Gson gson2 = new Gson() ;
                Type type2 = new TypeToken<PlaceDataFirebase>(){}.getType();
                String json2  = gson2.toJson(placeDataFirebase , type2);
                Intent widgetintent = new Intent(PlaceDetalisActivity.this, AppWidget.class);
                widgetintent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                widgetintent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, json2);
                sendBroadcast(widgetintent);
                mDatabaseRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            PlaceDataFirebase placeDataFirebase5 = userSnapshot.getValue(PlaceDataFirebase.class);
                            if (placeDataFirebase5.getName().equals(placeData.getName())) {
                                Isfavourit = true;
                                Fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                titleView.setText(placeData.getName());
                bylineView.setText(placeData.getAdress());

                if (placeData.getPhoneNumber() != null){
                    phonenum.setText(placeData.getPhoneNumber() );
                }else {
                    phonenum.setText("Not exsist");
                }

                if (placeData.getAdress() != null){
                    adress.setText(placeData.getAdress() );
                }else {
                    adress.setText("Not exsist");
                }

                if (placeData.getWebsiteUri() != null){
                    website.setText((CharSequence) placeData.getWebsiteUri());
                }else {
                    website.setText("Not exsist");
                }
                if (placeData.getAttributions() != null){
                    attribution.setText(placeData.getAttributions() );
                }else {
                    attribution.setText("Not exsist");
                }

                Fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Isfavourit){
                            mDatabaseRefrence.child(placeData.getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(PlaceDetalisActivity.this, "Place Removed",
                                            Toast.LENGTH_LONG).show();
                                    Fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                                }
                            });

                        }else {
                            Toast.makeText(PlaceDetalisActivity.this, "Please wait while adding place to favourit",
                                    Toast.LENGTH_LONG).show();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            if (bitmap != null) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            }
                            byte[] data = baos.toByteArray();
                            StorageReference mountainsRef = mStorageRef.child(placeData.getName() + ".jpg");
                            mountainsRef.putBytes(data)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Get a URL to the uploaded content
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            placeDataFirebase = turnPlacedatatofirebase(placeData, downloadUrl.toString());
                                            mDatabaseRefrence.child(placeData.getName()).setValue(placeDataFirebase).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                                                    Toast.makeText(PlaceDetalisActivity.this, "Place Added to Favourit",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            // ...
                                        }
                                    });
                        }


                    }
                });

            }

        }
    }

    private void getPhotos(final PlaceData placeData) {
        mGeoDataClient = Places.getGeoDataClient(this, null);
        final String placeId = placeData.getId();
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnSuccessListener(new OnSuccessListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onSuccess(PlacePhotoMetadataResponse placePhotoMetadataResponse) {
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = placePhotoMetadataResponse.getPhotoMetadata();
                // Get the first photo in the list.
                try {
                    final PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            photoMetadataBuffer.release();
                            PlacePhotoResponse photo = task.getResult();
                            bitmap = photo.getBitmap();
                            placeData.setBitmap(bitmap);
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {

                }

            }
        }) ;
    }

    public PlaceDataFirebase turnPlacedatatofirebase(PlaceData placeData, String downloadUrl) {
        PlaceDataFirebase placeDataFirebase = new PlaceDataFirebase();
        placeDataFirebase.setName(placeData.getName());
        placeDataFirebase.setAdress(placeData.getAdress());
        placeDataFirebase.setAttributions(placeData.getAttributions());
        placeDataFirebase.setLat(String.valueOf(placeData.getLatLng().latitude));
        placeDataFirebase.setLng(String.valueOf(placeData.getLatLng().longitude));
        placeDataFirebase.setWebsiteUri(placeData.getWebsiteUri());
        placeDataFirebase.setPhoneNumber(placeData.getPhoneNumber());
        placeDataFirebase.setId(placeData.getId());
        placeDataFirebase.setBitmap(downloadUrl);
        return placeDataFirebase;
    }
}