package com.linhard.martin.remotemuteclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageButton toggleBtn;

    private ImageButton refreshBtn;

    private ToggleState toggleState;

    private APIHandler handler;

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toggleBtn = this.findViewById(R.id.muteBtn);
        this.refreshBtn = this.findViewById(R.id.refreshBtn);
        EditText serverAddr = this.findViewById(R.id.serverAddr);

        this.ctx = this.getApplicationContext();

        serverAddr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.rebuild(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.handler = new APIHandler("192.168.0.234");

        toggleBtn.setOnClickListener(v -> this.toggle());

        refreshBtn.setOnClickListener( v -> this.fetchStatus());

        this.fetchStatus();
    }

    private void updateButton() {
        if (this.toggleState == ToggleState.MUTED) {
            this.toggleBtn.setImageResource(R.drawable.mic_mute);
        }
        else {
            this.toggleBtn.setImageResource(R.drawable.mic_unmute);
        }
    }

    private void fetchStatus() {
        this.handler.fetchStatus(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                toggleState = ToggleState.fromStatusReponse(response.body());
                updateButton();
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(ctx, "Error fetching status.", Toast.LENGTH_LONG).show();
                Log.i(MainActivity.class.getSimpleName(), t.getMessage());
            }
        });
    }

    private void toggle() {
        this.handler.toggle(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(ctx, "Success.", Toast.LENGTH_SHORT).show();
                fetchStatus();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ctx, "Error toggling MIC.", Toast.LENGTH_LONG).show();
            }
        });
    }
}