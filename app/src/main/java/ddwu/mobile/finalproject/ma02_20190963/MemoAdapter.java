package ddwu.mobile.finalproject.ma02_20190963;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MemoAdapter extends BaseAdapter {

        public static final String TAG = "MemoAdapter";

        private LayoutInflater inflater;
        private Context context;
        private int layout;
        private ArrayList<MemoDto> list;

    public MemoAdapter(Context context, int layout, ArrayList<MemoDto> list) {
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
        public MemoDto getItem(int position) {
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
                viewHolder = new ViewHolder();
                viewHolder.title = view.findViewById(R.id.memoTitle);
                viewHolder.content = view.findViewById(R.id.content);
                viewHolder.date = view.findViewById(R.id.memoDate);
                viewHolder.image = view.findViewById(R.id.image);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)view.getTag();
            }
            MemoDto dto = list.get(position);

            viewHolder.title.setText(dto.getTitle());
            viewHolder.content.setText(dto.getMemo());
            viewHolder.date.setText(dto.getVisitDate());
            viewHolder.image.setImageURI(Uri.fromFile(new File(dto.getPhotoPath())));

            return view;
        }

        public void setList(ArrayList<MemoDto> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        //    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
        static class ViewHolder {
            public TextView title = null;
            public TextView content = null;
            public TextView date = null;
            public ImageView image = null;

        }
}
