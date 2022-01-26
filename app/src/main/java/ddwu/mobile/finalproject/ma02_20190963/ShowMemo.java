package ddwu.mobile.finalproject.ma02_20190963;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ShowMemo extends AppCompatActivity {
    final static String TAG = "ShowMemo";
    final int REQ_CODE= 100;

    MemoDBHelper helper;
    ImageView ivPhoto;
    TextView tvMemo;
    Long data;

    TextView etTitle;
    TextView etlocation;
    TextView etDate;
    TextView etScore;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_memo);

        helper = new MemoDBHelper(this);
        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        tvMemo = findViewById(R.id.etMemo);
        etTitle = findViewById(R.id.etTitle);
        etlocation = findViewById(R.id.location);
        etDate = findViewById(R.id.visitDate);
        etScore = findViewById(R.id.ratingscore);

//        MainActivity 에서 전달 받은 _id 값을 사용하여 DB 레코드를 가져온 후 ImageView 와 TextView 설정
        Intent intent = getIntent();
        data = intent.getLongExtra("id", -1);

        Log.d(TAG, "전달받은 아이디: "+data);

        SQLiteDatabase db = helper.getReadableDatabase();

        String searchSql = "SELECT * FROM "+ MemoDBHelper.TABLE_NAME + " WHERE _id = "+data+";";
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
    }
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnUpdate:
                Intent intent = new Intent(ShowMemo.this, UpdateMemo.class);
                intent.putExtra("id", data);
                Log.d(TAG, String.valueOf(data));

                startActivityForResult(intent, REQ_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_CODE:
                if(resultCode == RESULT_OK){
                    finish();
                }
                break;
        }
    }

    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
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

