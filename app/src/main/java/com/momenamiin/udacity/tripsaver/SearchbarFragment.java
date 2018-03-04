package com.momenamiin.udacity.tripsaver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchbarFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.input_search) AutoCompleteTextView autoCompleteTextView ;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter ;
    private GeoDataClient mGeoDataClient ;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168) , new LatLng(71,136)
    );


    public SearchbarFragment() {
        // Required empty public constructor
    }


    public static SearchbarFragment newInstance(String param1, String param2) {
        SearchbarFragment fragment = new SearchbarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mGeoDataClient = Places.getGeoDataClient(getContext(), null);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext() ,mGeoDataClient, LAT_LNG_BOUNDS , null );
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_searchbar, container, false);
        ButterKnife.bind(this , rootview);
        autoCompleteTextView.setAdapter(mPlaceAutocompleteAdapter);
        return rootview ;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
