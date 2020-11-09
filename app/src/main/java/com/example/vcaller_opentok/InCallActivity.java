package com.example.vcaller_opentok;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;


import android.content.Intent;

import android.widget.Button;


public class InCallActivity extends AppCompatActivity
                            implements  Session.SessionListener,
                                        PublisherKit.PublisherListener {

    private static String API_KEY = "46978084";
    private static String SESSION_ID = "1_MX40Njk3ODA4NH5-MTYwNDc5MzQ3MDQ3NX51c0ZOTjc5eGRVaHBQL1cxcFRHbFpSbVN-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00Njk3ODA4NCZzaWc9MmQ3OTRhM2U4MWY3ZjllOTA0MTZkMTI3NDcyODlmZjhkOTkxYmIxZDpzZXNzaW9uX2lkPTFfTVg0ME5qazNPREE0Tkg1LU1UWXdORGM1TXpRM01EUTNOWDUxYzBaT1RqYzVlR1JWYUhCUUwxY3hjRlJIYkZwU2JWTi1mZyZjcmVhdGVfdGltZT0xNjA0NzkzNTk4Jm5vbmNlPTAuODY3NTIwMjQzNTAwNTg5NSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjA3Mzg1NTk3JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_call);
        // initialize and connect to the session
        mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
        mSession.setSessionListener(this);
        mSession.connect(TOKEN);

        // initialize view objects from layout
        mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);

        disconnectButtonListener();
    }

    public void disconnectButtonListener() {
        Button startCallButton = (Button) findViewById(R.id.disconnectButton);
        startCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InCallActivity.this, MainActivity.class));
            }
        });
    }

    private Session mSession; // Session is defined in the OpenTok Android SDK
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    private Publisher mPublisher;
    private Subscriber mSubscriber; // Subscriber is defined in the OpenTok Android SDK

    // SessionListener methods

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
        // TODO
        // mSession.disconnect();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }


    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    // PublisherListener methods

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }



    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

}
