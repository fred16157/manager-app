package com.example.manager_client;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class LogItem implements Serializable {
    private String id;
    private String rentalAt;
    private String returnAt;
    private String userId;
    public LogItem(String id, String rentalAt, String returnAt, String userId) {
        this.id = id;
        this.returnAt = returnAt;
        this.rentalAt = rentalAt;
        this.userId = userId;
    }
    public String getId() { return id; }

    public String getRentalAt() {
        return rentalAt;
    }

    public String getReturnAt() {
        return returnAt;
    }

    public String getUserId() {
        return userId;
    }
}

public class BookItem implements Serializable {
    private String bookImg;
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String publishedAt;
    private boolean status;
    private ArrayList<String> tags = new ArrayList<>();
    private ArrayList<LogItem> rentalLog = new ArrayList<>();

    public void addTag(String tag) {tags.add(tag);}

    public void addLogItem(LogItem item) {
        rentalLog.add(item);
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBookImg(String url) {
        this.bookImg = url;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public ArrayList<LogItem> getRentalLog() {
        return rentalLog;
    }

    public String getBookImg() {
        return bookImg;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public ArrayList<String> getTags() {return tags;}

    public String getId() {
        return id;
    }

    public boolean isStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}

class Utils {
    public static Drawable drawableFromUrl(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            if(!url.startsWith("http://")) return null;
            Bitmap x;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();

            x = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(Resources.getSystem(), x);
        }
        catch(Exception Ex)
        {
            Ex.printStackTrace();
            return null;
        }
    }
}