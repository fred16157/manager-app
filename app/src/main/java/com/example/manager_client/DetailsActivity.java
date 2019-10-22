package com.example.manager_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    }
}
