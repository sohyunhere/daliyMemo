package ddwu.mobile.finalproject.ma02_20190963;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchBlogActivity extends AppCompatActivity {

    private final static String TAG = "SearchBlogActivity";

    EditText location;
    String apiAddress;
    ListView lvList;

    BlogAdapter adapter;
    String query;
    ArrayList<NaverBlogDto> resultList;
    NaverNetworkManager networkManager;
    NaverBlogXmlParser parser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_bylocation);

        location = findViewById(R.id.where);
        lvList = findViewById(R.id.lvList);

        resultList = new ArrayList();
        adapter = new BlogAdapter(this, R.layout.listview_blog, resultList);
        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.api_url);
        parser = new NaverBlogXmlParser();
        networkManager = new NaverNetworkManager(this);
        networkManager.setClientId(getResources().getString(R.string.client_id));
        networkManager.setClientSecret(getResources().getString(R.string.client_secret));

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String url = adapter.getItem(i).getLink();
                Log.d(TAG, "주소: "+ url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.naverblog);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.memo:
                        intent = new Intent(SearchBlogActivity.this, ListMemo.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.naverblog:
                        return true;

                    case R.id.mapsearch:
                        intent = new Intent(SearchBlogActivity.this, MapActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.bluetooth:
                        intent = new Intent(SearchBlogActivity.this, BluetoothActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.search_btn:

                query = location.getText().toString() + " 박물관";  // UTF-8 인코딩 필요

                // OpenAPI 주소와 query 조합 후 서버에서 데이터를 가져옴
                // 가져온 데이터는 파싱 수행 후 어댑터에 설정
                try{
                    new NetworkAsyncTask().execute(apiAddress+ URLEncoder.encode(query, "UTF-8"));
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }


                break;
        }
    }


    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SearchBlogActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = null;
            // networking
            result = networkManager.downloadContents(address);
//            Log.d(TAG, "결과: " + result);
            if(result == null)
                return "error!";
            // parsing - 수행시간이 많이 걸릴 경우 이곳(스레드 내부)에서 수행하는 것을 고려
            resultList = parser.parse(result);

            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            // parsing - 수행시간이 짧을 경우 이 부분에서 수행하는 것을 고
            adapter.setList(resultList);    // Adapter 에 결과 List 를 설정 후 notify
            progressDlg.dismiss();
        }

    }
}
