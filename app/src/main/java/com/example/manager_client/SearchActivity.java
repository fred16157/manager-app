package com.example.manager_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    String queryType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EditText queryText = findViewById(R.id.queryText);
        Button searchBtn = findViewById(R.id.search_btn);
        Button cancelBtn = findViewById(R.id.cancel_btn);
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request req = new Request.Builder().url("http://13.209.89.75/api/tags").build();
                Response res = client.newCall(req).execute();

                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(res.body().string());
                final JsonArray arr = (JsonArray) obj.get("list");

                final ChipGroup tagGroup = findViewById(R.id.tag_group);
                for(int i = 0; i < arr.size(); i++)
                {
                    final String tagText = arr.get(i).getAsString();
                    runOnUiThread(() -> {
                        Chip tagChip = new Chip(this);
                        tagChip.setLayoutParams(new ChipGroup.LayoutParams(ChipGroup.LayoutParams.WRAP_CONTENT, ChipGroup.LayoutParams.WRAP_CONTENT));
                        tagChip.setText(tagText);
                        tagChip.setOnClickListener(v -> {
                            if(queryText.getText().toString().equals("")) queryText.setText(queryText.getText().toString() + tagText);
                            else queryText.setText(queryText.getText().toString() + ","  + tagText);
                        });
                        Random r = new Random();
                        tagChip.setId(r.nextInt());
                        tagGroup.addView(tagChip);
                    });
                }
            }
            catch (Exception Ex) {
                Ex.printStackTrace();
            }
        }).start();
        RadioGroup typeGroup = findViewById(R.id.typeGroup);
        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i)
                {
                    case R.id.titleBtn:
                        queryType = "title";
                        break;
                    case R.id.authorBtn:
                        queryType = "author";
                        break;
                    case R.id.tagBtn:
                        queryType = "tag";
                        break;
                    case R.id.isbnBtn:
                        queryType = "isbn";
                        break;
                }
            }
        });

        searchBtn.setOnClickListener((View v) -> {
            Intent intent = new Intent();
            intent.putExtra("QUERY_TEXT", queryText.getText().toString());
            intent.putExtra("QUERY_TYPE", queryType);
            setResult(0, intent);
            finish();
        });

        cancelBtn.setOnClickListener((View v) -> {
            setResult(1);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        setResult(1);
        super.onDestroy();
    }
}