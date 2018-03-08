package com.momenamiin.udacity.tripsaver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavouritFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseDatabase mFirebaseDatabase ;
    private DatabaseReference mDatabaseRefrence ;
    private FirebaseAuth mFirebaseAuth ;
    private FirebaseUser firebaseUser ;
    private ChildEventListener mChildEventListener ;
    public ArrayList<PlaceDataFirebase> placeDataFirebases ;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView ;


    public FavouritFragment() {
        // Required empty public constructor
    }

    public static FavouritFragment newInstance(String param1, String param2) {
        FavouritFragment fragment = new FavouritFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          // get Arg
        }
    }
    public void updatedata(ArrayList<PlaceDataFirebase> placeDataFirebases2){
        Adapter adapter = new Adapter(placeDataFirebases2);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-unique-tag")
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(1000, 1100))
                .build();
        dispatcher.mustSchedule(myJob);

        // Inflate the layout for this fragment
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        View rootview = inflater.inflate(R.layout.fragment_favourit, container, false);
        ButterKnife.bind(this , rootview);

        if(firebaseUser != null){
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
                    Adapter adapter = new Adapter(placeDataFirebases);
                    adapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(adapter);
                    int columnCount = getResources().getInteger(R.integer.list_column_count);
                    StaggeredGridLayoutManager sglm =
                            new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(sglm);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        };
            mDatabaseRefrence.addChildEventListener(mChildEventListener);
        }
        return rootview ;

    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<PlaceDataFirebase> mPlaceDataFirebase;

        public Adapter(ArrayList<PlaceDataFirebase> placeDataFirebase) {
            mPlaceDataFirebase = placeDataFirebase;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.listitem, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            return vh;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final PlaceDataFirebase placeDataFirebase = mPlaceDataFirebase.get(position);
            holder.titleView.setText(placeDataFirebase.getName());
            holder.subtitleView.setText(placeDataFirebase.getAdress());
            Picasso.with(getActivity())
                    .load(placeDataFirebase.getBitmap())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.thumbnailView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Picasso.with(getActivity())
                                    .load(placeDataFirebase.getBitmap())
                                    .into(holder.thumbnailView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }
                                        @Override
                                        public void onError() {
                                            Log.v("Picasso","Could not fetch image");
                                        }
                                    });
                        }
                    });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity() , PlaceDetalisActivity.class);
                    Drawable drawable = holder.thumbnailView.getDrawable();
                    BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                    Bitmap bitmap = bitmapDrawable .getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
                    Gson gson = new Gson() ;
                    Type type = new TypeToken<PlaceDataFirebase>(){}.getType();
                    String json  = gson.toJson(mPlaceDataFirebase.get(position), type);
                    intent.putExtra("json", json);
                    intent.putExtra("bitmap" , imageInByte);
                    intent.setAction(Intent.ACTION_ANSWER);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            if (mPlaceDataFirebase != null){
                return mPlaceDataFirebase.size();
            }else return 0 ;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;
        public CardView cardView ;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
            cardView = (CardView) view.findViewById(R.id.parant_view);
        }
    }


}
