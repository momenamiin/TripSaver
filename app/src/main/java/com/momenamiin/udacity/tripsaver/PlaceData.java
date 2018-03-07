package com.momenamiin.udacity.tripsaver;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;


/**
 * Created by momenamiin on 3/4/18.
 */

public class PlaceData {
    @SerializedName("name") private String name ;
    @SerializedName("adress") private String adress ;
    @SerializedName("phoneNumber") private String phoneNumber ;
    @SerializedName("id") private String id ;
    @SerializedName("websiteUri") private Uri websiteUri;
    @SerializedName("latLng") private LatLng latLng ;
    @SerializedName("attributions") private String attributions ;
    @SerializedName("bitmap") private Bitmap bitmap ;
    public PlaceData(){
    }


    public PlaceData(String name, String adress, String phoneNumber, String id, Uri websiteUri, LatLng latLng, String attributions , Bitmap bitmap) {
        this.name = name;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.attributions = attributions;
        this.bitmap = bitmap ;
    }


    public String getName() {
        return name;
    }

    public String getAdress() {
        return adress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getAttributions() {
        return attributions;
    }
    public Bitmap getBitmap(){
        return bitmap ;
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap ;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }
}
