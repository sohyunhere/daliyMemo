package ddwu.mobile.finalproject.ma02_20190963;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ListMemo extends AppCompatActivity {

    final static String TAG = "ListMemo";

    private ArrayList<MemoDto> memoArrayList = null;
    MemoAdapter memoAdapter;
    Cursor cursor;
    Cursor cursor1;
    MemoDBHelper helper;
    ListView lvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_memo);

        helper = new MemoDBHelper(this);
        memoArrayList = new ArrayList<>();

//        어댑터에 SimpleCursorAdapter 연결
        memoAdapter = new MemoAdapter(this, R.layout.memo_adapter, memoArrayList);

        lvMemo = (ListView)findViewById(R.id.lv_memo);
        lvMemo.setAdapter(memoAdapter);

        lvMemo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long _id) {
//                ShowMemoActivity 호출
                Intent intent = new Intent(ListMemo.this, ShowMemo.class);
                intent.putExtra("id", _id);
                Log.d(TAG, String.valueOf(_id));

                startActivity(intent);
            }
        });

        lvMemo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final long pos = l;
                SQLiteDatabase db = helper.getReadableDatabase();
                cursor1 = db.rawQuery("select "+MemoDBHelper.TITLE+" from " + MemoDBHelper.TABLE_NAME + " where _id=?;", new String[] {String.valueOf(pos)});

                String title="";
                if(cursor1.moveToNext()){
                    title = cursor1.getString(cursor1.getColumnIndex(MemoDBHelper.TITLE));
                }
                String msg = title+ "을(를) 삭제하시겠습니까?";
                helper.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(ListMemo.this);
                builder.setTitle("메모 삭제");
                builder.setMessage(msg);
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.execSQL("delete from "+ MemoDBHelper.TABLE_NAME + " where _id="+pos+";");

                        helper.close();
                        onResume();
                    }

                });
                builder.setNegativeButton("취소", null);
                builder.setCancelable(false);
                builder.show();
                cursor1.close();
                return true;
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.memo:
                        return true;
                    case R.id.naverblog:
                        intent = new Intent(ListMemo.this, SearchBlogActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mapsearch:
                        intent = new Intent(ListMemo.this, MapActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.bluetooth:
                        intent = new Intent(ListMemo.this, BluetoothActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.addBtn:
                Intent intent = new Intent(ListMemo.this, AddMemoActivity.class);

                startActivity(intent);
                break;

        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        memoArrayList.clear();
//        DB 에서 모든 레코드를 가져와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();

        cursor = db.rawQuery("select * from "+ MemoDBHelper.TABLE_NAME, null);

        while(cursor.moveToNext()){
            MemoDto dto = new MemoDto();
            dto.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            dto.setMemo(cursor.getString(cursor.getColumnIndex(MemoDBHelper.MEMO)));
            dto.setPhotoPath(cursor.getString(cursor.getColumnIndex(MemoDBHelper.PATH)));
            dto.setTitle(cursor.getString(cursor.getColumnIndex(MemoDBHelper.TITLE)));
            dto.setLocation(cursor.getString(cursor.getColumnIndex(MemoDBHelper.LOCATION)));
            dto.setVisitDate(cursor.getString(cursor.getColumnIndex(MemoDBHelper.VISITDATE)));
            dto.setScore(cursor.getDouble(cursor.getColumnIndex(MemoDBHelper.SCORE)));

            memoArrayList.add(dto);
        }
        memoAdapter.notifyDataSetChanged();
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}
