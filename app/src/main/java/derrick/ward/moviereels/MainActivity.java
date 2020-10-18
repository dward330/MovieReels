package derrick.ward.moviereels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private CountDownTimer timer;
    private MovieData movieData = new MovieData();
    private ImageView imageView;
    private int imageCounter = 0;
    private final int COUNT_DOWN_START_TIME = 5000;
    private final int COUNT_DOWN_TIMER_INTERVAL = 10;
    private GestureDetectorCompat gestureDetector;
    private final String DERRICK_LOGGING = "Derrick Logging: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.imageView = findViewById(R.id.imageView);

        this.proceedToNextMovieReel();

        // Create and Attach Gesture Listener
        this.gestureDetector = new GestureDetectorCompat(this,this);
    }

    /*
    * Builds and Returns a CountDownTimer
    */
    private CountDownTimer getTimer(final long maxTime, final long interval) {
         this.timer = new CountDownTimer(maxTime, interval) {
            @Override
            public void onTick(long l) {
                ProgressBar progressBarTimer = findViewById(R.id.progressBarTimer);
                progressBarTimer.setProgress((int)(COUNT_DOWN_START_TIME - l));
            }

            @Override
            public void onFinish() {
                proceedToNextMovieReel();
            }
        };

        return timer;
    }

    /*
    * Loads the Next Movie Reel and Resets the Timer
    */
    private void proceedToNextMovieReel() {

        // Load the next Movie Reel
        Object imageId = this.movieData.getItem(this.imageCounter++).get("image");
        if (imageId == null) {
            this.imageCounter = 0;
            imageId = this.movieData.getItem(imageCounter).get("image");
        }
        this.imageView.setImageResource((int) imageId);

        // Set Touch Listener
        this.imageView.setOnTouchListener(this);

        // Cancel the previous timer if it is exist
        if (this.timer != null) {
            this.timer.cancel();
        }

        this.timer = getTimer(COUNT_DOWN_START_TIME, COUNT_DOWN_TIMER_INTERVAL);
        this.timer.start();
    }

    /*
    * Event Handler for when touch event occurs
    */
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        switch(action) {
            case ACTION_DOWN:
                this.timer.cancel();
                Log.d(DERRICK_LOGGING, "Timer Paused");
                break;
            case ACTION_UP:
                ProgressBar progressBarTimer = findViewById(R.id.progressBarTimer);
                int progressBarCurrentPosition = progressBarTimer.getProgress();
                int timerResumeValue = (progressBarCurrentPosition - COUNT_DOWN_START_TIME) * -1;
                this.timer = getTimer(timerResumeValue, COUNT_DOWN_TIMER_INTERVAL);
                this.timer.start();
                Log.d(DERRICK_LOGGING, "Timer Resumed");
                break;
        }

        // Pass motion event information to gesture detector in case a swipe/fling has occured
        this.gestureDetector.onTouchEvent(event);

        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    /*
    * Event Handler for when a Fling/Swipe is detected
    */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        Log.d(DERRICK_LOGGING, "on Fling Event: "+v+", "+v1);

        // User Swiped to the right
        if (v > v1) {
            proceedToNextMovieReel();
        }

        return false;
    }
}