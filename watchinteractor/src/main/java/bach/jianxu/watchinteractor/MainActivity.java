package bach.jianxu.watchinteractor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity
        implements
        DataApi.DataListener,
        MessageApi.MessageListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private static final String WEAR_MESSAGE_PATH = "/message";

    private TextView mTextView;
    private float x1, x2;
    private float y1, y2;
    static final int MIN_DISTANCE = 50;
    public static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        // use this to start and trigger a service
        Context context = getApplicationContext();
        Intent i= new Intent(context, BackgroundService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        context.startService(i);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //Log.i(TAG, "dispatchTouchEvent.....");
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    //Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "swipe horizontally......");
                } else if (Math.abs(deltaY) > MIN_DISTANCE && y1 < y2) {
                    Log.i(TAG, "swipe down......");
                    sendMessage(WEAR_MESSAGE_PATH, "swiping down....");

                } else if (Math.abs(deltaY) > MIN_DISTANCE && y2 < y1) {
                    Log.i(TAG, "swipe up......");

                } else {
                    // consider as something else - a screen tap for example
                    Log.i(TAG, "something else......");
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        Log.i(TAG,"Received message.~~~~~~~~~~~~~~~~~~~~~~~" + new String(messageEvent.getData()));


        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //Log.i(TAG, "Received data: " + (new String(messageEvent.getData())));
//
//                if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)) {
//                    Log.i(TAG, "Received data: " + (new String(messageEvent.getData())));
//                }
//                analyzeDOM(new String(messageEvent.getData()));
//                constructUI();
//
//
//            }

            @Override
            public void run() {
                //Log.i(TAG, "Received data: " + (new String(messageEvent.getData())));

                //analyzeDOM(new String(messageEvent.getData()));
                //mBitmap = StringToBitMap(new String(messageEvent.getData()));
                //constructBitmap();
            }
        });
    }

    private void sendMessage(final String path, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                for (Node node: nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

}
