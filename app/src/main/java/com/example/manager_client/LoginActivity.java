package com.example.manager_client;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn = findViewById(R.id.login);
        btn.setOnClickListener((View v) -> {
            EditText username = findViewById(R.id.username);
            EditText password = findViewById(R.id.password);
            ProgressDialog pd = ProgressDialog.show(LoginActivity.this, "로그인 중", "서버의 응답을 대기중입니다...", true);
            new Thread(() -> {
                OkHttpClient client = new OkHttpClient();
                try {
                    RequestBody body = new FormBody.Builder().add("username", username.getText().toString()).add("password", password.getText().toString()).build();
                    Request req = new Request.Builder().url("http://13.209.89.75/api/user/login").post(body).build();
                    Response res = client.newCall(req).execute();
                    JsonParser parser = new JsonParser();
                    JsonObject obj = (JsonObject) parser.parse(res.body().string());
                    obj.get("id").getAsString();
                }
                catch (Exception Ex)
                {
                    Ex.printStackTrace();
                }
            }).start();
        });

    }
}
