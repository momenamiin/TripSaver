package com.momenamiin.udacity.tripsaver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    private static GeoDataClient mGeoDataClient;
    static String widgetTextString = null ;
    public static PlaceDataFirebase placeData ;
    public static Bitmap bitmap;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        placeData = new PlaceDataFirebase() ;
        Gson gson = new Gson();
        String json = intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        Type type = new TypeToken<PlaceDataFirebase>() {
        }.getType();
        if (json != null) {
            placeData = gson.fromJson(json, type);
           // getPhotos(placeData);
        }
        widgetTextString = placeData.getName();
        if (widgetTextString != null){

        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidget_text , widgetTextString);
        getPhotos(placeData , context);
        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, AppWidget.class),views);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        if (widgetTextString == null){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setTextViewText(R.id.appwidget_text, "Please Choose Place from app first");
            views.setViewVisibility(R.id.appwidget_text , View.GONE);
        }else {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setTextViewText(R.id.appwidget_text, widgetTextString);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            getPhotos(placeData , context);

        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setTextViewText(R.id.appwidget_text, widgetTextString);
            getPhotos(placeData , context);
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetTextString);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        getPhotos(placeData , context);

    }

    @Override
    public void onEnabled(Context context) {

        if (widgetTextString == null){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setTextViewText(R.id.appwidget_text, "Please choose Place from app first");
            views.setViewVisibility(R.id.appwidget_text , View.GONE);
        }else {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setTextViewText(R.id.appwidget_text, widgetTextString);
            // Instruct the widget manager to update the widget
            getPhotos(placeData , context);
            AppWidgetManager.getInstance(context).updateAppWidget(
            new ComponentName(context, AppWidget.class),views);

        }       }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    private static void getPhotos(final PlaceDataFirebase placeData, final Context context) {
        Log.v("Memo" , "getphotos");
        mGeoDataClient = Places.getGeoDataClient(context, null);
        if (placeData != null) {
            Log.v("Memo" , "place data != null");

            final String placeId = placeData.getId();
            if (placeId != null) {
                Log.v("Memo" , "place id != null");
                final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
                photoMetadataResponse.addOnSuccessListener(new OnSuccessListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onSuccess(PlacePhotoMetadataResponse placePhotoMetadataResponse) {
                        Log.v("Memo" , "On Success");
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
                                    Log.v("Memo" , "On Complete");
                                    photoMetadataBuffer.release();
                                    PlacePhotoResponse photo = task.getResult();
                                    bitmap = photo.getBitmap();
                                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
                                    views.setImageViewBitmap(R.id.appwidget_image, bitmap);
                                    views.setTextViewText(R.id.appwidget_text, widgetTextString);
                                    AppWidgetManager.getInstance(context).updateAppWidget(
                                            new ComponentName(context, AppWidget.class),views);
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
    }

}

