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

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    ListView BookList;
    ListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BookList = findViewById(R.id.BookList);
        adapter = new ListViewAdapter();
        BookList.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("ITEM", (Serializable) parent.getItemAtPosition(position));
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("검색 옵션");
            FrameLayout container = new FrameLayout(MainActivity.this);
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            final EditText queryText = new EditText(MainActivity.this);
            queryText.setHint("검색...");
            queryText.setLayoutParams(params);
            container.addView(queryText);
            String[] queryTypes = {"제목", "저자", "태그", "ISBN"};
            final int[] selected = {0};
            builder.setView(container);
            builder.setSingleChoiceItems(queryTypes, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selected[0] = which;
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final ProgressDialog pd = ProgressDialog.show(MainActivity.this,"검색 진행 중", "검색이 진행중입니다...", true);
                    Thread t = new Thread(() -> {
                            OkHttpClient client = new OkHttpClient();

                            String url = "";
                            if (queryText.getText().toString().equals("")) {
                                url = "http://13.209.89.75/api/search/";
                            }
                            else {
                                String type = "";
                                switch (selected[0])
                                {
                                    case 0:
                                        type = "title";
                                        break;
                                    case 1:
                                        type="author";
                                        break;
                                    case 2:
                                        type="tag";
                                        break;
                                    case 3:
                                        type="isbn";
                                        break;
                                }
                                url = "http://13.209.89.75/api/search/" + type + "/" + queryText.getText().toString();
                            }
                            try {
                                Request req = new Request.Builder().url(url).build();
                                Response res = client.newCall(req).execute();
                                if(res.code() != 200) return;
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
                                        item.addLogItem(new LogItem(log.get("rentalAt").getAsString(),
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
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
