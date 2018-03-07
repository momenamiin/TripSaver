package com.momenamiin.udacity.tripsaver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchbarFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.input_search) AutoCompleteTextView autoCompleteTextView ;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter ;
    private GoogleApiClient mGoogleApiClint ;
    private PlaceData placeData ;
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

        mGoogleApiClint = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity() , this)
                .build();

        mGeoDataClient = Places.getGeoDataClient(getContext(), null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext() ,mGeoDataClient, LAT_LNG_BOUNDS , null );
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_searchbar, container, false);
        ButterKnife.bind(this , rootview);
        autoCompleteTextView.setAdapter(mPlaceAutocompleteAdapter);
        autoCompleteTextView.setOnItemClickListener(onAutoCompleteClickListener);
        return rootview ;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//-------------------------- auto complete Listner Part ---------------------------

    private AdapterView.OnItemClickListener onAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        for (int t =0 ; t <= l ; t++){

        }
        final AutocompletePrediction autocompletePrediction = mPlaceAutocompleteAdapter.getItem(i);
        final String PlaceId = autocompletePrediction.getPlaceId();
        PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClint ,  PlaceId);
        placeBufferPendingResult.setResultCallback(mUpdatePlaceDataCallback);
    }
};
    private ResultCallback<PlaceBuffer> mUpdatePlaceDataCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (places.getStatus().isSuccess()){
                final Place place = places.get(0);
                placeData = new PlaceData();
                    placeData.setName(place.getName().toString());
                    placeData.setAdress(place.getAddress().toString());
                    placeData.setAttributions((String) place.getAttributions());
                    placeData.setId(place.getId().toString());
                    placeData.setLatLng(place.getLatLng());
                    placeData.setWebsiteUri(place.getWebsiteUri());
                    placeData.setPhoneNumber((String) place.getPhoneNumber());
                places.release();
                Gson gson = new Gson() ;
                Type type = new TypeToken<PlaceData>(){}.getType();
                String json  = gson.toJson(placeData , type);
                Intent intent = new Intent(getActivity() , PlaceDetalisActivity.class);
                intent.putExtra("json", json);
                startActivity(intent);
            }else {
                places.release();
                return;
            }
        }
    };
}