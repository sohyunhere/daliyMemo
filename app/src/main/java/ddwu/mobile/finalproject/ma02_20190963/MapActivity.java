package ddwu.mobile.finalproject.ma02_20190963;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    final static String TAG = "MapActivity";

    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;
    final static int PERMISSION_REQ_CODE = 100;

    private PlacesClient placesClient;
    private Geocoder geocoder;
    ArrayList<Marker> markers_list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapLoad();

        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.mapsearch);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.memo:
                        intent = new Intent(MapActivity.this, ListMemo.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.naverblog:
                        intent = new Intent(MapActivity.this, SearchBlogActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mapsearch:
                        return true;

                    case R.id.bluetooth:
                        intent = new Intent(MapActivity.this, BluetoothActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
    public  void onClick(View v){
        switch (v.getId()){
            case R.id.btnSearch:
                searchStart(PlaceType.MUSEUM);

                break;

            case R.id.myMuseum:
                Intent intent = new Intent(MapActivity.this, MapListActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void getPlaceDetail(String placeId) {
        List<Place.Field> placeFields       // ??????????????? ????????? ????????? ?????? ??????
                = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();    // ?????? ??????

        // ?????? ?????? ??? ?????? ??????/?????? ????????? ??????
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override                    // ?????? ?????? ??? ?????? ????????? ??????
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {  // ?????? ?????? ???
                final Place place = fetchPlaceResponse.getPlace();
                Log.i(TAG, "Place found: " + place.getName());  // ?????? ??? ?????? ???
                Log.i(TAG, "Phone: " + place.getPhoneNumber());
                Log.i(TAG, "Address: " + place.getAddress());
                Log.i(TAG, "ID: " + place.getId());
                callDetailActivity(place);
            }
        }).addOnFailureListener(new OnFailureListener() {   // ?????? ?????? ??? ?????? ????????? ??????
            @Override
            public void onFailure(@NonNull Exception exception) {   // ?????? ?????? ???
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();  // ?????? ??? ??????
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            }
        });

    }
    private void callDetailActivity(Place place) {
        final Intent intent = new Intent(MapActivity.this, DetailActivity.class);

        intent.putExtra("name",place.getName());
        intent.putExtra("phone",place.getPhoneNumber());
        intent.putExtra("address",place.getAddress());
        intent.putExtra("place_id", place.getId());

        startActivity(intent);
    }

    private void searchStart(String type) {
        new NRPlaces.Builder().listener(placesListener)
                .key(getString(R.string.api_key))
//                .latlng(Double.valueOf(getResources().getString(R.string.init_lat)), Double.valueOf(getResources().getString(R.string.init_lng)))   // ?????? ????????? ?????? ??????
                .latlng(mGoogleMap.getMyLocation().getLatitude(), mGoogleMap.getMyLocation().getLongitude())
                .radius(3000)        // ?????? ?????? ??????
                .type(type)
                .build()
                .execute();

        mGoogleMap.clear();
    }
    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Adding markers");

                    for (noman.googleplaces.Place nPlace : places) {
                        markerOptions.title(nPlace.getName());
                        markerOptions.position(new LatLng(nPlace.getLatitude(), nPlace.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        newMarker.setTag(nPlace.getPlaceId());
                        Log.d(TAG, "ID: " + nPlace);
                    }
                }
            });
        }
        @Override
        public void onPlacesFailure(PlacesException e) {
            e.printStackTrace();
            Log.d(TAG, "error here");
        }
        @Override
        public void onPlacesStart() {}
        @Override
        public void onPlacesFinished() {}
    };

    /* ?????? permission ?????? */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ???????????? ??????????????? ?????? ??? ?????? ??????
                mapLoad();
            } else {
                // ????????? ????????? ??? ???????????? ??????
                Toast.makeText(this, "??? ????????? ?????? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*???????????? ??????????????? ??????*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.d(TAG, "Map ready");

        if (checkPermission())
            mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(MapActivity.this, "??? ????????? ??????", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mGoogleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                List<String> address = getAddress(location.getLatitude(), location.getLongitude());
                Toast.makeText(MapActivity.this, address.get(0), Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: ??? ?????? ??? ????????? ?????? ??? ?????? ??????
        markerOptions = new MarkerOptions();
//        mGeoDataClient = Places.getGeoDataClient(MainActivity.this);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                String placeId = marker.getTag().toString();    // ????????? setTag() ??? ????????? Place ID ??????
                getPlaceDetail(placeId);
            }
        });

        //????????? ???????????? ????????? ?????? ??????
        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(String.format("??????:%f, ??????:%f", latLng.latitude, latLng.longitude));
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                markers_list.add(mGoogleMap.addMarker(options));
                mGoogleMap.addMarker(options).showInfoWindow();

            }
        });
    }
    //    Geocoding
    private List<String> getAddress(double latitude, double longitude) {

        List<Address> addresses = null;
        ArrayList<String> addressFragments = null;

//        ??????/????????? ???????????? ?????? ????????? Geocoder ?????? ??????
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size()  == 0) {
            return null;
        } else {
            Address addressList = addresses.get(0);
            addressFragments = new ArrayList<String>();

            for(int i = 0; i <= addressList.getMaxAddressLineIndex(); i++) {
                addressFragments.add(addressList.getAddressLine(i));
            }
        }

        return addressFragments;
    }
}
