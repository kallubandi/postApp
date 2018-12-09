package com.rag.postapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rag.postapp.model.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements View.OnClickListener {


    public static final int RC_PHOTO_PICKER_PERM = 123;
    private static final int CUSTOM_REQUEST_CODE = 532;
    private static final int IMAGE_PICKER_SELECT = 767;
    private AlertDialog alertDialog;
    private EditText editTextBudget, editTextCat, editTextloc, editTextDate;
    private DatePickerDialog fromDatePickerDialog;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private RecyclerView rvImages;
    private int MAX_ATTACHMENT_COUNT = 10;
    private int year;
    private int month;
    private int day;
    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            editTextDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView ivPostCat = findViewById(R.id.ivCat);
        ImageView ivPostLoc = findViewById(R.id.ivLoc);
        ImageView ivPostBudget = findViewById(R.id.ivBudget);
        ImageView ivPostStart = findViewById(R.id.ivStart);
        ImageView ivAdd = findViewById(R.id.iv_add);
        rvImages = findViewById(R.id.rv_images);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvImages.setLayoutManager(layoutManager);

        editTextBudget = findViewById(R.id.etPostBudget);
        editTextCat = findViewById(R.id.etCat);
        editTextloc = findViewById(R.id.etLoc);
        editTextDate = findViewById(R.id.etDate);


        ivPostCat.setOnClickListener(this);
        ivPostLoc.setOnClickListener(this);
        ivPostBudget.setOnClickListener(this);
        ivPostStart.setOnClickListener(this);
        ivAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCat:
                Intent myIntent = new Intent(getBaseContext(), GridActivity.class);
                startActivityForResult(myIntent, 999);
                break;
            case R.id.ivLoc:
                Intent searchIntent = new Intent(getBaseContext(), SearchActivity.class);
                startActivityForResult(searchIntent, 888);

                break;
            case R.id.ivBudget:
                CharSequence[] values = {" First Item ", " Second Item ", " Third Item "};
                createAlertDialogWithRadioButtonGroup(values, "Rate");
                break;
            case R.id.ivStart:
                initDate();
                break;
            case R.id.iv_add:
                pickPhotoClicked();
                break;
        }
    }

    @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
    public void pickPhotoClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickPhoto();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    public void onPickPhoto() {
        if (photoPaths.size() == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {

            if (Build.VERSION.SDK_INT < 19) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/* video/*");
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.app_name)), IMAGE_PICKER_SELECT);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
                startActivityForResult(intent, IMAGE_PICKER_SELECT);
            }
        }
    }

    private void initDate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        editTextDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case 999:
                if (resultCode == RESULT_OK) {
                    int result = data.getIntExtra("result", 0);
                    editTextCat.setText(result + "Categories selected");

                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;

            case 888:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("result");
                    editTextloc.setText(result);
                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;

            case IMAGE_PICKER_SELECT:
                if (resultCode == Activity.RESULT_OK && data != null) {

                    Uri selectedImageUri = data.getData();
                    // MEDIA GALLERY
                    String selectedImagePath = getRealPathFromURI(selectedImageUri);
                    photoPaths.add(selectedImagePath);
                    Picture s;
                    ArrayList<Picture> spacecrafts = new ArrayList<>();

                    try {
                        for (String path : photoPaths) {
                            s = new Picture();
                            s.setName(path.substring(path.lastIndexOf("/") + 1));

                            s.setUri(Uri.fromFile(new File(path)));
                            spacecrafts.add(s);
                        }

                        rvImages.setAdapter(new ImageAdapter(this, spacecrafts));
                    } catch (Exception e) {

                    }
                }
                break;


        }

    }//onActivityResult


    public void createAlertDialogWithRadioButtonGroup(CharSequence[] values, String title) {


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(title);

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        editTextBudget.setText("First Item");
                        Toast.makeText(MainActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        editTextBudget.setText("Second Item");
                        Toast.makeText(MainActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        editTextBudget.setText("Third Item");
                        Toast.makeText(MainActivity.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

}
