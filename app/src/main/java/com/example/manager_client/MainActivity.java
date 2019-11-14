package com.example.manager_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    ListView BookList;
    ListViewAdapter adapter;
    Toolbar toolbar;
    String userId = "";
    String queryText = "";
    String queryType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BookList = findViewById(R.id.BookList);
        adapter = new ListViewAdapter();
        BookList.setAdapter(adapter);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("ITEM", (Serializable) parent.getItemAtPosition(position));
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivityForResult(intent, 1);
        }
        else if(id == R.id.action_login)
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, 0);
        }
        else if(id == R.id.action_refresh)
        {
            if(!queryType.equals("")) search();
        }
        return super.onOptionsItemSelected(item);
    }

    public void search() {
        final ProgressDialog pd = ProgressDialog.show(MainActivity.this,"검색 진행 중", "검색이 진행중입니다...", true);
        Thread t = new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String url = "";
            if (queryText.equals("") || queryType.equals("")) {
                url = "http://13.209.89.75/api/search/";
            }
            else {
                url = "http://13.209.89.75/api/search/" + queryType + "/" + queryText;
            }
            try {
                Request req = new Request.Builder().url(url).build();
                Response res = client.newCall(req).execute();
                if(res.code() != 200) { pd.dismiss(); return;}
                runOnUiThread(() -> {
                    pd.setMessage("검색 완료 - 정보 분석중...");
                });
                adapter.clearItems();
                JsonParser parser = new JsonParser();
                JsonArray arr = (JsonArray) parser.parse(res.body().string());
                for(int i = 0; i<arr.size(); i++)
                {
                    JsonObject obj = (JsonObject) arr.get(i);
                    BookItem item = new BookItem();
                    item.setStatus(obj.get("status").getAsInt() != 0);
                    item.setTitle(obj.get("title").getAsString());
                    item.setId(obj.get("_id").getAsString());
                    item.setAuthor(obj.get("author").getAsString());
                    item.setIsbn(obj.get("isbn").getAsString());
                    item.setBookImg(obj.get("imageUrl").getAsString());
                    item.setPublishedAt(obj.get("publishedAt").getAsString());
                    JsonArray tags = (JsonArray) obj.get("tags");
                    for(int j = 0; j < tags.size(); j++)
                    {
                        item.addTag(tags.get(j).getAsString());
                    }
                    JsonArray logs = (JsonArray) obj.get("rentalLog");
                    for(int j = 0; j<logs.size(); j++)
                    {
                        JsonObject log = (JsonObject)logs.get(j);
                        item.addLogItem(new LogItem(log.get("_id").getAsString(),log.get("rentalAt").getAsString(),
                                log.get("returnAt").getAsString(),
                                log.get("userId").getAsString()));
                    }
                    runOnUiThread(() -> {
                        adapter.addItem(item);
                        adapter.notifyDataSetChanged();
                    });
                }
                runOnUiThread(() -> {
                    pd.dismiss();
                    BookList.setAdapter(adapter);
                });
            }
            catch (Exception Ex)
            {
                Ex.printStackTrace();
            }
        });
        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        System.out.println("onActivityResult Called");
        if(resultCode == 0)
        {
            switch (requestCode)
            {
                case 0:
                    try{
                        userId = data.getStringExtra("USER_ID");
                        Snackbar.make(findViewById(R.id.mainLayout), "유저 ID가 " + userId + "로 설정되었습니다.", Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    catch(Exception Ex)
                    {
                        Ex.printStackTrace();
                    }
                case 1:
                    try {
                        queryText = data.getStringExtra("QUERY_TEXT");
                        queryType = data.getStringExtra("QUERY_TYPE");
                        search();
                    }
                    catch(Exception Ex) {
                        Ex.printStackTrace();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}
