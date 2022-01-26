package ddwu.mobile.finalproject.ma02_20190963;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MapAdapter extends BaseAdapter {

    public static final String TAG = "MapAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<MapDto> list;

    public MapAdapter(Context context, int layout, ArrayList<MapDto> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MapDto getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new MapAdapter.ViewHolder();
            viewHolder.tvName = view.findViewById(R.id.memoTitle);
            viewHolder.tvAddress = view.findViewById(R.id.content);
            viewHolder.tvImage = view.findViewById(R.id.image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        MapDto dto = list.get(position);

        viewHolder.tvName.setText(dto.getName());
        viewHolder.tvAddress.setText(dto.getAddress());
        if(dto.getImage() == "null"){
            viewHolder.tvImage.setImageResource(R.mipmap.photo);
        }else{
            viewHolder.tvImage.setImageBitmap(StringToBitmap(dto.getImage()));
        }


        return view;
    }

    public void setList(ArrayList<MapDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    //    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView tvName = null;
        public TextView tvAddress = null;
        public ImageView tvImage = null;

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
