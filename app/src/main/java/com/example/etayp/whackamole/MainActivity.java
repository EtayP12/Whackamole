package com.example.etayp.whackamole;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    int score = 0;
    private boolean start = false;
    Random random = new Random();
    private ArrayList<Mole> moles = new ArrayList<>();
    private final String TAG = "game";
    ArrayList<Mole> ClickedMoles = new ArrayList<>();
    ArrayList<Mole> popedMoles = new ArrayList<>();
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            popedMoles.get(0).setVisibility(View.GONE);
            if (!ClickedMoles.contains(popedMoles.get(0)))
                ClickedMoles.add(popedMoles.remove(0));
            else
                popedMoles.remove(0);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int moleNum = random.nextInt(ClickedMoles.size());
            ClickedMoles.get(moleNum).setVisibility(View.VISIBLE);
            popedMoles.add(ClickedMoles.remove(moleNum));
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    long futureTime = System.currentTimeMillis() + random.nextInt(3000) + 1000;
                    while (System.currentTimeMillis() < futureTime) {
                        synchronized (this) {
                            try {
                                wait(futureTime - System.currentTimeMillis());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    handler2.sendEmptyMessage(0);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOnClicks(findViewById(R.id.table_layout));

        Button startBtn = (Button) findViewById(R.id.start_button);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout);
                tableLayout.setVisibility(View.VISIBLE);
                ((Button) v).setText(R.string.btn_started);
                start = true;
                clearBoard();
                for (int i = 0; i < 5; i++) {
                    int moleNum = random.nextInt(ClickedMoles.size());
                    ClickedMoles.get(moleNum).setVisibility(View.VISIBLE);
                    ClickedMoles.remove(moleNum);
                }
                score = 0;
                String s = "Score:" + score;
                TextView scoreView = (TextView) findViewById(R.id.score_text);
                scoreView.setText(s);
            }
        });


    }

    private void clearBoard() {
        for (int i = 0; i < moles.size(); i++) {
            if (moles.get(i).getVisibility()==View.VISIBLE) {
                moles.get(i).setVisibility(View.GONE);
                if(!ClickedMoles.contains(moles.get(i)))
                    ClickedMoles.add(moles.get(i));
            }
        }
        for (int i = 0; i < popedMoles.size(); i++) {
            popedMoles.remove(i);
        }
    }

    private void setOnClicks(View v) {
        if (v instanceof ViewGroup) {
            int childCount = ((ViewGroup) v).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View viewToPrint = ((ViewGroup) v).getChildAt(i);
                Log.d(MainActivity.class.getSimpleName(), viewToPrint.getClass().getSimpleName());
                if (viewToPrint instanceof ViewGroup) {
                    setOnClicks(viewToPrint);
                } else if (viewToPrint instanceof Mole) {
                    moles.add((Mole) viewToPrint);
                    ClickedMoles.add((Mole) viewToPrint);
                    viewToPrint.setOnClickListener(this);
                }
            }
        }
    }

    public int numberOfMoles(ArrayList<Mole> moles) {
        int counter = 0;
        int size = moles.size();
        for (int i = 0; i < size; i++) {
            if (moles.get(i).getVisibility() == View.VISIBLE) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public void onClick(View v) {
        ClickedMoles.add((Mole) v);
        v.setVisibility(v.GONE);
        addScore();
        Log.d(TAG, "onClick: ");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + random.nextInt(5000) + 1;
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
        if (numberOfMoles(moles) < 5 && start)
            t.start();
    }

    private void addScore() {
        score++;
        String s = "Score:" + score;
        TextView v = (TextView) findViewById(R.id.score_text);
        v.setText(s);
    }
}
