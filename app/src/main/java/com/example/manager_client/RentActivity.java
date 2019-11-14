package com.example.manager_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RentActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener rentalDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateRentalLabel();
        }
    };
    DatePickerDialog.OnDateSetListener returnDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateReturnLabel();
        }
    };

    private void updateRentalLabel() {
        String format = "yyyy-MM-dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);

        EditText et_date = findViewById(R.id.rentalDate);
        et_date.setText(sdf.format(calendar.getTime()));
    }

    private void updateReturnLabel() {
        String format = "yyyy-MM-dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);

        EditText et_date = findViewById(R.id.returnDate);
        et_date.setText(sdf.format(calendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);
        Button rentBtn = findViewById(R.id.rentBtn);

        EditText rentalAt = findViewById(R.id.rentalDate);
        EditText returnAt = findViewById(R.id.returnDate);
        rentalAt.setOnClickListener((View v) -> {
            new DatePickerDialog(RentActivity.this, rentalDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        returnAt.setOnClickListener((View v) -> {
            new DatePickerDialog(RentActivity.this, returnDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        rentBtn.setOnClickListener((View v) -> {
            Intent intent = getIntent();
            ProgressDialog pd = ProgressDialog.show(RentActivity.this, "대출 기록 전송중", "서버의 응답을 기다리고 있습니다...", true);
            new Thread(() -> {
                OkHttpClient client = new OkHttpClient();
                try {
                    RequestBody body = new FormBody.Builder().add("rentalAt", rentalAt.getText().toString()).add("returnAt", returnAt.getText().toString()).add("userId", intent.getStringExtra("USER_ID")).build();
                    Request req = new Request.Builder().url("http://13.209.89.75/api/books/rental/" + intent.getStringExtra("BOOK_ID")).put(body).build();
                    Response res = client.newCall(req).execute();
                    if(res.isSuccessful())
                    {
                        finish();
                    }
                    pd.dismiss();
                    System.out.println(res.body().string());
                }
                catch (Exception Ex)
                {
                    Ex.printStackTrace();
                }
            }).start();
        });
    }
}
