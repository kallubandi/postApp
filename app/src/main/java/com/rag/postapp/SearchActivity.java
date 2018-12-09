package com.rag.postapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SearchActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AutoCompleteAdapter.PlaceAutoCompleteInterface {

    protected GoogleApiClient mGoogleApiClient;
    private TextInputEditText editTextSearch;
    private RecyclerView searchList;
    private AutoCompleteAdapter mAdapter;
    private TextView tvMap;

    public static LatLngBounds setBounds() {

        return new LatLngBounds(new LatLng(9.493762638264792, 70.46630859375), new LatLng(30.790216480462256, 83.91357421875));

    }

    /**
     * Method to check if google play services are enabled or not
     *
     * @return boolean status
     */
    public static boolean isGooglePlayServicesAvailable(SearchActivity context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(context, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextSearch = findViewById(R.id.etTitle);
        searchList = findViewById(R.id.search_list);
        tvMap = findViewById(R.id.tv_map);

        editTextSearch.setFocusableInTouchMode(true);
        editTextSearch.setFocusable(true);
        editTextSearch.requestFocus();

        if (isGooglePlayServicesAvailable(this)) {
            buildGoogleAPiClient();
        } else {
            Log.e("", "Google Play Services not available");
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchList.setLayoutManager(layoutManager);
        mAdapter = new AutoCompleteAdapter(this, mGoogleApiClient, setBounds(), null, this);


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {

                } else if (!s.equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                    searchList.setAdapter(mAdapter);
                } else if (!mGoogleApiClient.isConnected()) {
                    Log.e("", "API  NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    mAdapter.getFilter().filter(null);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onPlaceClick(ArrayList<AutoCompleteAdapter.PlaceAutoComplete> mResultList, int position) {
        AutoCompleteAdapter.PlaceAutoComplete placeAutoComplete = mResultList.get(position);
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeAutoComplete.getPlaceId())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", myPlace.getName());
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        } else {
                        }
                        places.release();
                    }
                });


    }

    // Function to build the Google Api Client..
    protected synchronized void buildGoogleAPiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

}
