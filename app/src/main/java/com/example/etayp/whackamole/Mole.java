package com.example.etayp.whackamole;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.Random;

/**
 * Created by etayp on 07-Apr-17.
 */

public class Mole extends AppCompatButton implements View.OnClickListener {
    private final String TAG = "game";
    Mole mole;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mole.setVisibility(VISIBLE);
        }
    };
    public Mole(Context context) {
        super(context);
        init(context, null, 0);
    }

    public Mole(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Mole(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }


    @Override
    public void onClick(View v) {
        mole = (Mole)v;
        final Random random = new Random();
        v.setVisibility(v.GONE);
        Log.d(TAG, "onClick: ");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + random.nextInt(50000) + 1;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                    handler.sendEmptyMessage(0);

            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}
