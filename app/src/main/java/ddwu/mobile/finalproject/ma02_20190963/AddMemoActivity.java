package ddwu.mobile.finalproject.ma02_20190963;

import static ddwu.mobile.finalproject.ma02_20190963.GalleryAdapter.INTENT_PATH;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMemoActivity extends AppCompatActivity {
    final static String TAG = "AddActivity";
    private static final int REQUEST_TAKE_PHOTO = 200;
    private static final int REQ_GALLERY = 300;

    private String mCurrentPhotoPath;

    final Calendar myCalendar = Calendar.getInstance();
    ImageView ivPhoto;
    EditText etMemo;
    EditText etTitle;
    EditText etlocation;
    EditText etDate;
    EditText etScore;
    private RelativeLayout btnsBackgroundLayout;

    MemoDBHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post);

        helper = new MemoDBHelper(this);

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        etMemo = (EditText) findViewById(R.id.etMemo);
        etTitle = findViewById(R.id.etTitle);
        etlocation = findViewById(R.id.location);
        etDate = findViewById(R.id.visitDate);
        etScore = findViewById(R.id.ratingscore);

        btnsBackgroundLayout = findViewById(R.id.btnsBackgroundLayout);
        btnsBackgroundLayout.setVisibility(View.GONE);

        ivPhoto.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    외부 카메라 호출
//                    dispatchTakePictureIntent();
                    Log.d(TAG, "gkgk");
                    btnsBackgroundLayout.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddMemoActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:
//                DB에 촬영한 사진의 파일 경로 및 메모 저장
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(MemoDBHelper.MEMO, etMemo.getText().toString());
                row.put(MemoDBHelper.PATH, mCurrentPhotoPath);
                row.put(MemoDBHelper.TITLE, etTitle.getText().toString());
                row.put(MemoDBHelper.LOCATION, etlocation.getText().toString());
                row.put(MemoDBHelper.VISITDATE, etDate.getText().toString());
                row.put(MemoDBHelper.SCORE, etScore.getText().toString());

                Log.d(TAG, "이미지 경로: " + mCurrentPhotoPath);

                long result = db.insert(MemoDBHelper.TABLE_NAME, null, row);
                Toast.makeText(this, "Save!", Toast.LENGTH_SHORT).show();
                helper.close();

            case R.id.btnCancel:
                finish();
                break;

            case R.id.btnsBackgroundLayout:
                btnsBackgroundLayout.setVisibility(View.GONE);
                break;

            case R.id.imageModify:
                dispatchTakePictureIntent();
                break;
            case R.id.videoModify:
                Intent intent = new Intent(this, GalleryActivity.class);
                startActivityForResult(intent, REQ_GALLERY);
                break;
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.KOREA);
        etDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ddwu.mobile.finalproject.ma02_20190963.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                setPic();
            } else {
                new File(mCurrentPhotoPath).delete();
            }
        }

        if (requestCode == REQ_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                mCurrentPhotoPath = data.getStringExtra(INTENT_PATH);
                Glide.with(this).load(mCurrentPhotoPath).centerCrop().override(500).into(ivPhoto);
            }

        }
    }
        private void setPic () {
            // Get the dimensions of the View
            int targetW = ivPhoto.getWidth();
            int targetH = ivPhoto.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            ivPhoto.setImageBitmap(bitmap);
        }
}
