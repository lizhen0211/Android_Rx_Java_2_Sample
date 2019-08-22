package com.lz.android_rxjava2_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class OperatorActivity extends AppCompatActivity {

    private static String TAG = OperatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);
    }

    /**
     * 对上游发送的每一个事件应用一个函数
     * 将上游接口数据，转换成下游接口需要的数据
     *
     * @param view
     */
    public void onMapOperatorClick(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {//Integer 源类型；String 目标类型
            @Override
            public String apply(Integer integer) throws Exception {
                //这里将Integer 转换成了 String
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, s);
            }
        });
    }
}
