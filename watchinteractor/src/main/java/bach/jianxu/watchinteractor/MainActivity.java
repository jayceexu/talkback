package bach.jianxu.watchinteractor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private float x1, x2;
    private float y1, y2;
    static final int MIN_DISTANCE = 50;
    public static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
