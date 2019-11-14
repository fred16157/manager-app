package com.example.manager_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        BookItem item = (BookItem) intent.getSerializableExtra("ITEM");
        TextView titleView = findViewById(R.id.titleView);
        TextView authorView = findViewById(R.id.authorView);
        TextView publishedView = findViewById(R.id.publishedView);
        TextView isbnView = findViewById(R.id.isbnView);
        TextView statusView = findViewById(R.id.statusView);
        TextView tagView = findViewById(R.id.tagView);
        TextView logView = findViewById(R.id.logView);
        ImageView thumbView = findViewById(R.id.ThumbView);
            thumbView.setImageDrawable(Utils.drawableFromUrl(item.getBookImg()));
        titleView.setText(item.getTitle());
        authorView.setText(item.getAuthor() + " 저");
        publishedView.setText(item.getPublishedAt() + "에 출판됨");
        isbnView.setText("ISBN-13:" + item.getIsbn());
        if(!item.isStatus())
        {
            LogItem lastLog = item.getRentalLog().get(item.getRentalLog().size() - 1);
            statusView.setText(lastLog.getUserId() + "님이 " + lastLog.getReturnAt() + " 까지 대여중");
        }
        else statusView.setText("대출되지 않음");
        StringBuilder tagStr = new StringBuilder();
        for(int i = 0; i < item.getTags().size(); i++)
        {
            if(i != 0)
            {
                tagStr.append(" / ");
            }
            tagStr.append(item.getTags().get(i));
        }
        tagView.setText(tagStr.toString());
        StringBuilder logStr = new StringBuilder();
        for(int i = 0; i < item.getRentalLog().size(); i++)
        {
            LogItem log = item.getRentalLog().get(i);
            if(i != 0)
            {
                logStr.append("\n");
            }
            logStr.append(log.getUserId() + "님이 " + log.getRentalAt() + " 부터 " + log.getReturnAt() + "까지 대출함");
        }
        logView.setText(logStr.toString());
        Button rentBtn = findViewById(R.id.rentBtn_);
        Button returnBtn = findViewById(R.id.returnBtn);
        if(!item.isStatus())
        {
            rentBtn.setEnabled(false);
            if(item.getRentalLog().get(item.getRentalLog().size()-1).getUserId().equals(intent.getStringExtra("USER_ID")))
            {
                returnBtn.setEnabled(true);
            }
            else returnBtn.setEnabled(false);
        }
        else
        {
            if(!intent.getStringExtra("USER_ID").equals(""))
            {
                rentBtn.setEnabled(true);
            }
            else rentBtn.setEnabled(false);
            returnBtn.setEnabled(false);
        }
        rentBtn.setOnClickListener((View v) -> {
            Intent rentIntent = new Intent(getApplicationContext(), RentActivity.class);
            rentIntent.putExtra("BOOK_ID", item.getId());
            rentIntent.putExtra("USER_ID", intent.getStringExtra("USER_ID"));
            startActivity(rentIntent);
        });
        returnBtn.setOnClickListener((View v) -> {
            OkHttpClient client = new OkHttpClient();
            new Thread(() -> {
                try {
                    RequestBody body = new FormBody.Builder().add("bookId", item.getId()).add("logId", item.getRentalLog().get(item.getRentalLog().size()-1).getId()).add("userId", intent.getStringExtra("USER_ID")).build();
                    Request req = new Request.Builder().url("http://13.209.89.75/api/books/return").post(body).build();
                    Response res = client.newCall(req).execute();
                    JsonParser parser = new JsonParser();
                    JsonObject obj = (JsonObject) parser.parse(res.body().string());
                    Toast.makeText(getApplicationContext(), "신청이 완료되었습니다. 티켓 ID - " + obj.get("ticketId").getAsString(), Toast.LENGTH_LONG).show();
                }
                catch (Exception Ex)
                {
                    Ex.printStackTrace();
                }
            }).start();
        });
    }


}
