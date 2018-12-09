package com.rag.postapp;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rag.postapp.model.Model;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Raghavendra Kallubandi on 09/12/18.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private SparseBooleanArray mSelectedItems;

    private List<Model> mModelList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Model> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mModelList = data;
        mSelectedItems = new SparseBooleanArray();
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_grid_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvFront.setText(mModelList.get(position).getText());
        holder.tvBack.setText(mModelList.get(position).getText());

        if (isSelected(position)) {
            holder.cardBack.setVisibility(View.VISIBLE);
            holder.cardFront.setVisibility(View.GONE);
        } else {
            holder.cardBack.setVisibility(View.GONE);
            holder.cardFront.setVisibility(View.VISIBLE);
        }

    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    // convenience method for getting data at click position
    Model getItem(int id) {
        return mModelList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public void switchSelectedState(int position) {

        if (mSelectedItems.get(position)) {       // item has been selected, de-select it.
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelectedState() {
        List<Integer> selection = getSelectedItems();
        mSelectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); ++i) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int selectedItemCount);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvFront, tvBack;
        RelativeLayout cardFront, cardBack;

        ViewHolder(View itemView) {
            super(itemView);
            tvFront = itemView.findViewById(R.id.tvText);
            tvBack = itemView.findViewById(R.id.tvTextBack);
            cardFront = itemView.findViewById(R.id.card_front);
            cardBack = itemView.findViewById(R.id.card_back);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if (getSelectedItemCount() == 3 && !isSelected(getAdapterPosition())) {
                    return;
                } else {
                    switchSelectedState(getAdapterPosition());
                    mClickListener.onItemClick(getSelectedItemCount());

                }

            }
        }
    }

}
