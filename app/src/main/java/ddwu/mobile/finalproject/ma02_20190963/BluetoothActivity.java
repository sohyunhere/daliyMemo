package ddwu.mobile.finalproject.ma02_20190963;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQ_ENABLE_BT = 0;
    private static final int REQ_DISCOVER_BT = 1;

    private TextView mStatusBtTv, mPairedTv;
    ImageView mBlueTv;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mStatusBtTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairTv);
        mBlueTv = findViewById(R.id.bluetoothIv);
        mOffBtn = findViewById(R.id.offBtn);
        mOnBtn = findViewById(R.id.onBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            mStatusBtTv.setText("bluetooth 사용 불가");
        }else {
            mStatusBtTv.setText("bluetooth 사용 가능");
        }

        if(bluetoothAdapter.isEnabled()){
            mBlueTv.setImageResource(R.drawable.ic_bluetooth_on);
        }else{
            mBlueTv.setImageResource(R.drawable.ic_bluetooth_disabled);
        }

        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bluetoothAdapter.isEnabled()){
                    showToast("bluetooth 켜지는 중");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQ_ENABLE_BT);
                }else{
                    showToast("bluetooth가 이미 켜져있습니다");
                }
            }
        });

        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bluetoothAdapter.isDiscovering()){
                    showToast("making your device discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQ_DISCOVER_BT);
                }
            }
        });

        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                    showToast("bluetooth 끄는 중");
                    mBlueTv.setImageResource(R.drawable.ic_bluetooth_disabled);
                }else{
                    showToast("bluetooth가 이미 꺼져있습니다");
                }
            }
        });

        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bluetoothAdapter.isEnabled()){
                    mPairedTv.setText("페어링된 기기들");
                    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

                    for(BluetoothDevice device : devices){
                        mPairedTv.append("\n Device : " + device.getName() + ", " + device);
                    }
                }else{
                    showToast("bluetooth를 켜주세요");
                }
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bluetooth);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.memo:
                        intent = new Intent(BluetoothActivity.this, ListMemo.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.naverblog:
                        intent = new Intent(BluetoothActivity.this, SearchBlogActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mapsearch:
                        intent = new Intent(BluetoothActivity.this, MapActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.bluetooth:
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REQ_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    mBlueTv.setImageResource(R.drawable.ic_bluetooth_on);
                    showToast("bluetooth 켜짐");
                } else {
                    showToast("bluetooth 꺼짐");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}