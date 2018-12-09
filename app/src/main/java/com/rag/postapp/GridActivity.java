package com.rag.postapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rag.postapp.model.Model;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class GridActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, View.OnClickListener {

    private MyRecyclerViewAdapter adapter;
    private ArrayList<Model> modelList;
    private int count = 0;
    private TextView tvDefault, tvSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        // data to populate the RecyclerView with
        String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};

        modelList = new ArrayList<Model>();
        for (String s : data) {
            Model m = new Model(s);
            modelList.add(m);
        }


        TextView tvSelect = findViewById(R.id.select);
        tvDefault = findViewById(R.id.tvDefault);
        tvSelected = findViewById(R.id.tvSelected);

        tvSelect.setOnClickListener(this);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvNumbers);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter(this, modelList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(int selectedItemCount) {
        count = selectedItemCount;
        tvDefault.setText("Maximum 3 categories");

        switch (count) {
            case 0:
                tvSelected.setVisibility(View.GONE);
                tvDefault.setText("Select upto 3 categories");

                break;
            case 1:
                tvSelected.setVisibility(View.VISIBLE);
                tvSelected.setText("1 category selected");
                break;
            case 2:
                tvSelected.setVisibility(View.VISIBLE);
                tvSelected.setText("2 categories selected");
                break;

            case 3:
                tvSelected.setVisibility(View.VISIBLE);
                tvSelected.setText("3 categories selected");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", count);
                setResult(RESULT_OK, returnIntent);
                finish();
                break;

        }
    }
}


