package com.lz.android_rxjava2_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SimpleDemoActivity extends AppCompatActivity {

    private static final String TAG = SimpleDemoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_demo);
    }

    public void onBaseDemoClick(View view) {
        //创建一个上游 Observable：
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });

        //创建一个下游 Observer
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "observer subscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "complete");
            }
        };

        //订阅
        observable.subscribe(observer);
    }

    public void onChainDemoClick(View view) {
        //链式结构
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "observer subscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "complete");
            }
        });
    }

    /**
     * ObservableEmitter 发射器
     * <p>
     * ObservableEmitter可以发射 onNext(T value)、onComplete()和onError(Throwable error)
     * <p>
     * 上游可以发送无限个onNext；下游也可以接收无限个onNext
     * <p>
     */
    public void onObservableEmitterClick(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
                //上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送
                //而下游收到onComplete事件之后将不再继续接收事件
                //这里得到的结果将是 1，2，3 complete
                emitter.onNext(4);

                try {
                    JSONObject jo = new JSONObject();
                    jo.get("");
                } catch (JSONException e) {
                    //上游发送了一个onError后, 上游onError之后的事件将继续发送
                    //而下游收到onError事件之后将不再继续接收事件
                    emitter.onError(e);
                    e.printStackTrace();
                }
                //上游可以不发送onComplete或onError.
                //onComplete和onError必须唯一并且互斥
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "observer subscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "complete");
            }
        });
    }
}
