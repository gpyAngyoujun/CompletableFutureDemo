package com.jimmy.completablefuturedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jimmy.android.executors.AndroidExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ExecutorService executor;
    private Button mBtnFuture;
    private Button mBtnCancel;
    private Button mBtnExecutor;
    private CompletableFuture<Void> mFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnFuture = findViewById(R.id.btn_future);
        mBtnFuture.setOnClickListener(v -> {
            mFuture = CompletableFuture.supplyAsync(() -> {
                Log.d(TAG, "supply:" + Thread.currentThread());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "123321";
            }).thenAcceptAsync(s -> {
                Log.d(TAG, s + " - " + Thread.currentThread() + " - " + System.currentTimeMillis());
                throw new AndroidRuntimeException("jeruowieur");
            }, AndroidExecutors.mainExecutor());
        });
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: cancel");
//            mFuture.cancel(true);
            executor.shutdown();
        });

        mBtnExecutor = findViewById(R.id.btn_executor);
        mBtnExecutor.setOnClickListener(v -> executor = AndroidExecutors.newExecutor());
    }
}
