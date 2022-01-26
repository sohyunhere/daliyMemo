package ddwu.mobile.finalproject.ma02_20190963;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MapDBHelper extends SQLiteOpenHelper {

    private final static String TAG = "MapDBHelper";

    private final static String DB_NAME ="map_db";
    public final static String TABLE_NAME = "map_table";
    public final static String ID = "_id";
    public final static String NAME = "name";
    public final static String PHONE = "phone";
    public final static String ADDRESS = "address";
    public final static String IMAGE = "image";
    public final static String PLACEID = "placeId";

    public MapDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + NAME + " text, " + PHONE + " text, " + ADDRESS+ " text, "+ PLACEID+ " text, "+IMAGE + " text);";
        Log.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
