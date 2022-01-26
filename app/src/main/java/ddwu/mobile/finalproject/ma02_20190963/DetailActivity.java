package ddwu.mobile.finalproject.ma02_20190963;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    final static String TAG = "DetailActivity";

    EditText etName;
    EditText etPhone;
    EditText etAddress;
    ImageView imageView;
    Bitmap imgBitmap;
    boolean isExistPhoto = true;

    String placeId;

    MapDBHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        helper = new MapDBHelper(this);

        placesClient = Places.createClient(this);

        Intent intent = getIntent();

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        imageView = findViewById(R.id.imageView);

        etName.setText(intent.getStringExtra("name"));
        etPhone.setText(intent.getStringExtra("phone") == null ? "정보 없음" : intent.getStringExtra("phone"));
        etAddress.setText(intent.getStringExtra("address"));
        placeId = intent.getStringExtra("place_id");

        setImage();

    }

    private PlacesClient placesClient;


//  Place ID 에 해당하는 사진이 있을 경우 가져옴
    private void setImage() {

        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                final Place place = response.getPlace();

                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    isExistPhoto = false;
                    return;
                }

                final PhotoMetadata photoMetadata = metadata.get(0);

                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                    @Override
                    public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                        imgBitmap = fetchPhotoResponse.getBitmap();
                        imageView.setImageBitmap(imgBitmap);
                        Log.d(TAG, "photo!!!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "no photo");
                    }
                });
            }
        });
    }
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:

                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues row = new ContentValues();
                row.put(MapDBHelper.NAME, etName.getText().toString());
                row.put(MapDBHelper.PHONE, etPhone.getText().toString());
                row.put(MapDBHelper.ADDRESS, etAddress.getText().toString());
                if(isExistPhoto == true){
                    row.put(MapDBHelper.IMAGE, BitmapToString(imgBitmap));
                }else{
                    row.put(MapDBHelper.IMAGE, "null");
                }

                row.put(MapDBHelper.PLACEID, placeId);

                Toast.makeText(this, "Save Place information", Toast.LENGTH_SHORT).show();

                db.insert(MapDBHelper.TABLE_NAME,null, row);
                Log.d(TAG, "dd");
                helper.close();

                Intent intent = new Intent(DetailActivity.this, MapListActivity.class);
                startActivity(intent);
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }
}
