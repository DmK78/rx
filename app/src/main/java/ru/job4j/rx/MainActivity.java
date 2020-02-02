package ru.job4j.rx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private Disposable sbr;
    private ProgressBar bar;
    private TextView info;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        bar = findViewById(R.id.load);
        info = findViewById(R.id.info);
        boolean recreated = bundle != null;
        final int startAt = recreated ? bundle.getInt("progress", 0) : 0;
        info.setText(startAt + "%");
        Button btn = findViewById(R.id.start);
        btn.setOnClickListener(v -> {
            if (null == sbr || sbr.isDisposed()) {
                MainActivity.this.start(startAt);
            }
        });
        if (recreated) {
            start(startAt);
        }
    }

    public void start(int startAt) {
        this.sbr = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long v) throws Exception {
                        info.setText(startAt + v.intValue() + "%");
                        bar.setProgress(startAt + v.intValue());
                        Log.i("MyApp", String.valueOf(sbr.hashCode()));
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("progress", bar.getProgress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.sbr != null) {
            this.sbr.dispose();
        }
    }
}