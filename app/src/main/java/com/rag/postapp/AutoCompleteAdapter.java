package com.rag.postapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Raghavendra Kallubandi on 09/12/18.
 */
public class AutoCompleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = AutoCompleteAdapter.class.getSimpleName();
    private ArrayList<PlaceAutoComplete> mResultList;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private PlaceAutoCompleteInterface mPlaceClickInterface;

    public AutoCompleteAdapter(Context mContext, GoogleApiClient mGoogleApiClient, LatLngBounds mBounds, AutocompleteFilter mPlaceFilter, PlaceAutoCompleteInterface mPlaceClickInterface) {
        this.mContext = mContext;
        this.mGoogleApiClient = mGoogleApiClient;
        this.mPlaceFilter = mPlaceFilter;
        setmBounds(mBounds);
        this.mPlaceClickInterface = mPlaceClickInterface;
    }

    /**
     * Setting Bounds for subsequent queries
     */
    public void setmBounds(LatLngBounds mBounds) {
        this.mBounds = mBounds;
    }

    /*
   Clear List items
    */
    public void clearList() {
        if (mResultList != null && mResultList.size() > 0) {
            mResultList.clear();
        }
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<PlaceAutoComplete> queryResults;
                if (constraint != null && constraint.length() > 0) {
                    queryResults = getAutoComplete(constraint);
                    if (queryResults != null) {
                        results.values = queryResults;
                        results.count = queryResults.size();
                    }
                }
                // The API successfully returned results.
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    mResultList = (ArrayList<PlaceAutoComplete>) results.values;
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    clearList();
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    /**
     * Method to call API for each user input
     *
     * @param constraint User input character string
     * @return ArrayList containing suggestion results
     */
    private ArrayList<PlaceAutoComplete> getAutoComplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            //Making a query and fetching result in a pendingResult

            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi
                    .getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter);

            //Block and wait for 60s for a result
            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();

            // Confirm that the query completed successfully, otherwise return null
            if (!status.isSuccess()) {
                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlaceAutoComplete(prediction.getPlaceId(), prediction.getPrimaryText(null), prediction.getSecondaryText(null)));
            }
            autocompletePredictions.release();
            return resultList;
        } else {
            Log.e(TAG, "GoogleApiClient Not Connected");
            return null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == 0) {
            View convertView = mLayoutInflater.inflate(R.layout.recycler_view_search_location_item, parent, false);
            PredictionHolder predictionHolder = new PredictionHolder(convertView);
            return predictionHolder;
        } else {
            View convertView = mLayoutInflater.inflate(R.layout.recycler_view_powered_by_google, parent, false);
            PoweredByGoogle poweredByGoogle = new PoweredByGoogle(convertView);
            return poweredByGoogle;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof PredictionHolder) {
            final PredictionHolder holder = (PredictionHolder) viewHolder;
            holder.addressTitle.setText(mResultList.get(position).getPlaceAddress1());
            holder.addressLine.setText(mResultList.get(position).getPlaceAddress2());
        } else if (viewHolder instanceof PoweredByGoogle) {
            final PoweredByGoogle holder = (PoweredByGoogle) viewHolder;
            if (mResultList.size() > 0) {
                holder.mErrorLayout.setVisibility(View.GONE);
            } else {
                holder.mErrorLayout.setVisibility(View.VISIBLE);

            }

        }

    }

    @Override
    public int getItemCount() {
        if (mResultList != null) {
            return mResultList.size() + 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mResultList.size()) {
            return 0;
        } else {
            return 1;
        }
    }

    public PlaceAutoComplete getItem(int position) {
        return mResultList.get(position);
    }

    public interface PlaceAutoCompleteInterface {
        void onPlaceClick(ArrayList<PlaceAutoComplete> mResultList, int position);
    }

    public static class PoweredByGoogle extends RecyclerView.ViewHolder {
        private RelativeLayout mErrorLayout;

        public PoweredByGoogle(View holder) {
            super(holder);
            mErrorLayout = holder.findViewById(R.id.rl_error);

        }
    }

    public class PredictionHolder extends RecyclerView.ViewHolder {
        private TextView addressTitle;
        private TextView addressLine;
        private RelativeLayout mPredictionLayout;

        public PredictionHolder(View holder) {
            super(holder);
            addressTitle = holder.findViewById(R.id.address_title);
            addressLine = holder.findViewById(R.id.full_address);
            mPredictionLayout = holder.findViewById(R.id.rl_address);

            mPredictionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPredictionLayout.setClickable(false);
                    mPredictionLayout.setFocusable(false);
                    mPredictionLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPredictionLayout.setFocusable(true);
                            mPredictionLayout.setClickable(true);
                        }
                    }, 1000);

                    mPlaceClickInterface.onPlaceClick(mResultList, getAdapterPosition());
                }
            });

        }
    }

    /**
     * Holder class for query result
     */
    public class PlaceAutoComplete {
        private CharSequence placeId;
        private CharSequence placeAddress1, placeAddress2;

        public PlaceAutoComplete(CharSequence placeId, CharSequence placeAddress1, CharSequence placeAddress2) {
            this.placeId = placeId;
            this.placeAddress1 = placeAddress1;
            this.placeAddress2 = placeAddress2;
        }

        public String getPlaceAddress1() {
            return placeAddress1.toString();
        }

        public String getPlaceAddress2() {
            return placeAddress2.toString();
        }

        public String getPlaceId() {
            return placeId.toString();
        }

    }

}