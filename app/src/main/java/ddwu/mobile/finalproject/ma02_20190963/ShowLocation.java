package ddwu.mobile.finalproject.ma02_20190963;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ShowLocation extends AppCompatActivity {

    final static String TAG = "ShowLocation";

    MapDBHelper helper;
    TextView etName;
    TextView etPhone;
    TextView etAddress;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_location);

        helper = new MapDBHelper(this);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        Long data = intent.getLongExtra("id", -1);

        Log.d(TAG, "전달받은 아이디: "+data);

        SQLiteDatabase db = helper.getReadableDatabase();

        String searchSql = "SELECT * FROM "+ MapDBHelper.TABLE_NAME + " WHERE _id = "+data+";";
        Log.d(TAG, searchSql);

        Cursor cursor = db.rawQuery(searchSql, null);

        String name ="";
        String phone ="";
        String address ="";
        String image ="";

        if(cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(MapDBHelper.NAME));
            phone = cursor.getString(cursor.getColumnIndex(MapDBHelper.PHONE));
            address = cursor.getString(cursor.getColumnIndex(MapDBHelper.ADDRESS));
            image = cursor.getString(cursor.getColumnIndex(MapDBHelper.IMAGE));

        }
        cursor.close();
        helper.close();

        etName.setText(name);
        etPhone.setText(phone);
        etAddress.setText(address);
        if(image == "null"){
            imageView.setImageResource(R.mipmap.photo);
        }else{
            imageView.setImageBitmap(StringToBitmap(image));
        }


    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnClose:
                finish();
                break;
        }
    }
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
