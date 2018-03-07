package com.momenamiin.udacity.tripsaver;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by momenamiin on 3/6/18.
 */

public class PlaceDataFirebase {
    @SerializedName("name") private String name ;
    @SerializedName("adress") private String adress ;
    @SerializedName("phoneNumber") private String phoneNumber ;
    @SerializedName("id") private String id ;
    @SerializedName("websiteUri") private Uri websiteUri;
    @SerializedName("lat") private String lat ;

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getLng() {

        return Lng;
    }

    @SerializedName("Lng") private String Lng ;
    @SerializedName("attributions") private String attributions ;
    @SerializedName("bitmap") private String bitmap ;

    public PlaceDataFirebase(){
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

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {

        return lat;
    }

    public String getAttributions() {
        return attributions;
    }
    public String getBitmap(){
        return bitmap ;
    }
    public void setBitmap(String bitmap){
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


    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

}
