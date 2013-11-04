package com.example.kktoolkitdemo.eventqueue;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.utils.KKEventQueue;
import com.kkbox.toolkit.utils.KKEventQueueListener;

import java.util.ArrayList;

public class EventQueueActivity extends KKActivity {
    private KKEventQueue eventQueue;
    private int mID = 1;
    private ArrayList<Integer> mLockIDs;
    private ArrayList<Integer> mEventWithLockID;
    private EventStatus mStatus = EventStatus.PREPARE;

    private TextView labelEventStatus;
    private TextView labelLockID;
    private TextView labelPending;
    private Button btnAddEvent;
    private Button btnClearEvent;
    private Button btnStartEvent;
    private Button btnLockEvent;
    private Button btnUnlockEvent;
    private Button btnUnlockAllEvent;

    private final KKEventQueueListener eventQueueListener = new KKEventQueueListener() {
        @Override
        public void onQueueCompleted() {
            if (mEventWithLockID.isEmpty()) {
                printStatus(EventStatus.FINISHED);
            } else {
                printStatus(EventStatus.PREPARE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_eventqueue);

        initUI();
        setEventQueueUsage();
    }

    private void initUI() {

        labelLockID = (TextView) findViewById(R.id.lock_id);
        labelEventStatus = (TextView) findViewById(R.id.job_status);
        labelPending = (TextView) findViewById(R.id.label_pending);

        btnAddEvent = (Button) findViewById(R.id.add__event);
        btnStartEvent = (Button) findViewById(R.id.start_event);
        btnClearEvent = (Button) findViewById(R.id.clear_event);
        btnLockEvent = (Button) findViewById(R.id.lock_event);
        btnUnlockEvent = (Button) findViewById(R.id.unlock_event);
        btnUnlockAllEvent = (Button) findViewById(R.id.unlock_all_event);

        resetParameter();
        printPendingNumber();
        printStatus(EventStatus.PREPARE);
    }

    private void resetParameter() {
        mID = 1;
        if (mLockIDs != null) {
            mLockIDs.clear();
        }
        if (mEventWithLockID != null) {
            mEventWithLockID.clear();
        }
    }

    private void setEventQueueUsage() {

        mLockIDs = new ArrayList<Integer>();

        eventQueue = new KKEventQueue();
        eventQueue.setListener(eventQueueListener);

        mEventWithLockID = new ArrayList<Integer>();

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEventWithLockID.add(-1);
                printPendingNumber();
                if (mStatus == EventStatus.FINISHED) {
                    printStatus(EventStatus.PREPARE);
                }
                eventQueue.addNewThreadEvent(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                printStatus(EventStatus.RUNNING);
                                if (mEventWithLockID.size() > 0) {
                                    mEventWithLockID.remove(0);
                                }
                                printPendingNumber();
                            }
                        }
                );
            }
        });

        btnStartEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!eventQueue.isRunning()) {
                    printStatus(EventStatus.RUNNING);
                    eventQueue.start();
                }
            }
        });

        btnLockEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int id = mID++;
                mLockIDs.add(id);
                mEventWithLockID.add(id);
                printLockIDs();
                printPendingNumber();

                if (mStatus == EventStatus.FINISHED) {
                    printStatus(EventStatus.PREPARE);
                }

                eventQueue.addCallerThreadEventWithLock(new Runnable() {
                    @Override
                    public void run() {
                        printStatus(EventStatus.RUNNING);
                        //current event is not locked
                        if (mEventWithLockID.get(0) < 0) {
                            mEventWithLockID.remove(0);
                            printPendingNumber();
                        } else {
                            printStatus(EventStatus.LOCKED);
//                            labelEventStatus.setText("Status : Locked,  ID = " + mLockIDs.get(0));
                        }
                    }
                }, id);
            }
        });

        btnUnlockEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLockIDs.isEmpty()) {
                    int current_lock_id = mLockIDs.get(0);
                    if (mStatus == EventStatus.LOCKED) {
                        mEventWithLockID.remove(0);
                        printPendingNumber();
                        printStatus(EventStatus.RUNNING);
                    } else {
                        mEventWithLockID.set(mEventWithLockID.indexOf(current_lock_id), -1);
                    }
                    mLockIDs.remove(0);
                    printLockIDs();
                    eventQueue.unlockEvent(current_lock_id);
                }
            }
        });

        btnUnlockAllEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLockIDs.isEmpty()) {
                    //current status = locked
                    if (mStatus == EventStatus.LOCKED) {
                        mEventWithLockID.remove(0);
                        printPendingNumber();
                        printStatus(EventStatus.RUNNING);
                    }

                    eventQueue.unlockAllEvents();
                    mLockIDs.clear();
                    printLockIDs();

                    for (int i = 0; i < mEventWithLockID.size(); i++) {
                        mEventWithLockID.set(i, -1);
                    }
                }
            }
        });

        btnClearEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventQueue.clearPendingEvents();
                resetParameter();
                printPendingNumber();
                printLockIDs();

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (eventQueue != null) {
            eventQueue.clearPendingEvents();
        }
        super.onDestroy();
    }

    private void printLockIDs() {
        String s = "";
        for (Integer id : mLockIDs) {
            s += "Lock ID : " + id + "\n";
        }
        labelLockID.setText(s);
    }

    private void printPendingNumber() {
        if (mEventWithLockID != null) {
            labelPending.setText("Number of pending : " + mEventWithLockID.size());
        }
    }

    enum EventStatus {PREPARE, RUNNING, LOCKED, FINISHED}

    ;

    private void printStatus(EventStatus status) {
        switch (status) {
            case PREPARE:
                mStatus = EventStatus.PREPARE;
                labelEventStatus.setText("Status : Prepare");
                break;

            case RUNNING:
                if (eventQueue.isRunning()) {
                    mStatus = EventStatus.RUNNING;
                    labelEventStatus.setText("Status : Running");
                }
                break;

            case LOCKED:
                if (!mLockIDs.isEmpty()) {
                    mStatus = EventStatus.LOCKED;
                    labelEventStatus.setText("Status : Locked,  ID = " + mLockIDs.get(0));
                }
                break;

            case FINISHED:
                mStatus = EventStatus.FINISHED;
                labelEventStatus.setText("Status : Finished");
                break;
        }

    }
}
