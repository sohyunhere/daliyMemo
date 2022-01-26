package ddwu.mobile.finalproject.ma02_20190963;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateMemo extends AppCompatActivity {
    final static String TAG = "UpdateMemo";
    private static final int REQUEST_TAKE_PHOTO = 200;
    final Calendar myCalendar= Calendar.getInstance();

    MemoDBHelper helper;
    Long dataId;

    ImageView ivPhoto;
    EditText tvMemo;
    EditText etTitle;
    TextView etlocation;
    TextView etDate;
    TextView etScore;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_memo);

        helper = new MemoDBHelper(this);
        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        tvMemo = findViewById(R.id.etMemo);
        etTitle = findViewById(R.id.etTitle);
        etlocation = findViewById(R.id.location);
        etDate = findViewById(R.id.visitDate);
        etScore = findViewById(R.id.ratingscore);

        Intent intent = getIntent();
        dataId = intent.getLongExtra("id", -1);

        Log.d(TAG, "전달받은 아이디: "+dataId);

        SQLiteDatabase db = helper.getReadableDatabase();

        String searchSql = "SELECT * FROM "+ MemoDBHelper.TABLE_NAME + " WHERE _id = "+dataId+";";
        Log.d(TAG, searchSql);

        Cursor cursor = db.rawQuery(searchSql, null);

        String memo ="";
        String title ="";
        String location ="";
        String date ="";
        double score = 0;
        if(cursor.moveToNext()){
            memo = cursor.getString(cursor.getColumnIndex(MemoDBHelper.MEMO));
            mCurrentPhotoPath = cursor.getString(cursor.getColumnIndex(MemoDBHelper.PATH));
            title = cursor.getString(cursor.getColumnIndex(MemoDBHelper.TITLE));
            location = cursor.getString(cursor.getColumnIndex(MemoDBHelper.LOCATION));
            date = cursor.getString(cursor.getColumnIndex(MemoDBHelper.VISITDATE));
            score = cursor.getDouble(cursor.getColumnIndex(MemoDBHelper.SCORE));
        }

        Log.d(TAG, "경로: "+mCurrentPhotoPath);
        cursor.close();
        helper.close();

        tvMemo.setText(memo);
        etTitle.setText(title);
        etlocation.setText(location);
        etDate.setText(date);
        etScore.setText(String.valueOf(score));

        ivPhoto.setImageURI(Uri.fromFile(new File(mCurrentPhotoPath)));

        ivPhoto.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    외부 카메라 호출
                    dispatchTakePictureIntent();
                    return true;
                }
                return false;
            }
        });

        DatePickerDialog.OnDateSetListener dated =new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UpdateMemo.this,dated,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnUpdate:
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(MemoDBHelper.MEMO, tvMemo.getText().toString());
                row.put(MemoDBHelper.PATH, mCurrentPhotoPath);
                row.put(MemoDBHelper.TITLE, etTitle.getText().toString());
                row.put(MemoDBHelper.LOCATION, etlocation.getText().toString());
                row.put(MemoDBHelper.VISITDATE, etDate.getText().toString());
                row.put(MemoDBHelper.SCORE, etScore.getText().toString());

                Log.d(TAG, "이미지 경로: "+ mCurrentPhotoPath);
                String whereClause = "_id=?";
                String[] whereArgs = new String[]{String.valueOf(dataId)};

                long result = db.update(MemoDBHelper.TABLE_NAME, row, whereClause, whereArgs);
                Toast.makeText(this, "Save!", Toast.LENGTH_SHORT).show();
                helper.close();

                setResult(RESULT_OK);
                break;
            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                break;
        }
        finish();
    }
    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.KOREA);
        etDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile = null;

            try{
                photoFile = createImageFile();
            }catch (IOException ex){
                ex.printStackTrace();
            }
            if (photoFile != null){
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

    private void setPic() {
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
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivPhoto.setImageBitmap(bitmap);
    }
}
