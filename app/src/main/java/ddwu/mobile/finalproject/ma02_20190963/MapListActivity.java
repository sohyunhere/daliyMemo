package ddwu.mobile.finalproject.ma02_20190963;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MapListActivity extends AppCompatActivity {

    final static String TAG = "MapListActivity";

    private ArrayList<MapDto> mapArrayList = null;
    MapAdapter adapter;
    Cursor cursor;
    MapDBHelper helper;
    ListView mapList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_list);

        helper = new MapDBHelper(this);
        mapArrayList = new ArrayList<>();

        adapter = new MapAdapter(this, R.layout.maplist_adapter, mapArrayList);

        mapList = findViewById(R.id.mapList);
        mapList.setAdapter(adapter);

        mapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long _id) {
//                ShowMemoActivity 호출
                Intent intent = new Intent(MapListActivity.this, ShowLocation.class);
                intent.putExtra("id", _id);
                Log.d(TAG, String.valueOf(_id));

                startActivity(intent);
            }
        });

        mapList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final long pos = l;

                SQLiteDatabase db = helper.getReadableDatabase();

                cursor = db.rawQuery("select "+MapDBHelper.NAME+" from " + MapDBHelper.TABLE_NAME + " where _id=?;", new String[] {String.valueOf(pos)});

                String title="";
                if(cursor.moveToNext()){
                    title = cursor.getString(cursor.getColumnIndex(MapDBHelper.NAME));
                }
                String msg = title+ "을(를) 삭제하시겠습니까?";
                helper.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(MapListActivity.this);
                builder.setTitle("메모 삭제");
                builder.setMessage(msg);
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.execSQL("delete from "+ MapDBHelper.TABLE_NAME + " where _id="+pos+";");

                        helper.close();
                        onResume();
                    }

                });
                builder.setNegativeButton("취소", null);
                builder.setCancelable(false);
                builder.show();
                cursor.close();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapArrayList.clear();

        SQLiteDatabase db = helper.getReadableDatabase();

        cursor = db.rawQuery("select * from "+ MapDBHelper.TABLE_NAME, null);

        while(cursor.moveToNext()){
            MapDto dto = new MapDto();
            dto.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            dto.setName(cursor.getString(cursor.getColumnIndex(MapDBHelper.NAME)));
            dto.setPhone(cursor.getString(cursor.getColumnIndex(MapDBHelper.PHONE)));
            dto.setAddress(cursor.getString(cursor.getColumnIndex(MapDBHelper.ADDRESS)));
            dto.setImage(cursor.getString(cursor.getColumnIndex(MapDBHelper.IMAGE)));
            dto.setPlaceId(cursor.getString(cursor.getColumnIndex(MapDBHelper.PLACEID)));

            Log.d(TAG, "이미지: "+cursor.getInt(cursor.getColumnIndex(MapDBHelper.IMAGE)));
            mapArrayList.add(dto);
        }
        helper.close();
        cursor.close();

        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}
