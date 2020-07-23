package com.shinetech.mjpeglib;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;
import com.github.niqdev.mjpeg.OnFrameCapturedListener;


public class IpCamDefaultActivityCopy extends AppCompatActivity implements View.OnClickListener, OnFrameCapturedListener {


    private MjpegSurfaceView mjpegView;
    private TextView tvTakePhoto;
    private ImageView imageView;

    private Bitmap lastPreview = null;


    private static final int TIMEOUT = 5;
    private MediaPlayer player;

    private String url;

    private SurfaceHolder mSurfaceHolder;
    private MediaRecorder mRecoder;
    private boolean mIsRecording = false;
    private boolean mIsSufaceCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_default);

//        ButterKnife.bind(this);

        init();
    }

    private void init() {
        url = getIntent().getStringExtra("baseURL");

        mjpegView = findViewById(R.id.mjpegViewDefault);
        imageView = findViewById(R.id.imageView);
//        tvTakePhoto = findViewById(R.id.tv_take_photo);


        mjpegView.setOnFrameCapturedListener(this);


        mSurfaceHolder = mjpegView.getSurfaceView().getHolder();



    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(this);
    }

    private String getPreference(String key) {
        return getSharedPreferences()
                .getString(key, "");
    }

    private Boolean getBooleanPreference(String key) {
        return getSharedPreferences()
                .getBoolean(key, false);
    }

    private DisplayMode calculateDisplayMode() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ?
                DisplayMode.FULLSCREEN : DisplayMode.BEST_FIT;
    }

    public void loadIpCam(String url) {
        Mjpeg.newInstance()
                .open(url, TIMEOUT)
                .subscribe(
                        inputStream -> {
                            mjpegView.setSource(inputStream);
                            mjpegView.setDisplayMode(calculateDisplayMode());
                            mjpegView.flipHorizontal(true);
                            mjpegView.flipVertical(true);
                            mjpegView.setRotate(180);
                            mjpegView.showFps(true);
                        },
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIpCam(url);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mjpegView.stopPlayback();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_capture) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (lastPreview != null) {
                        imageView.setImageBitmap(lastPreview);
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_capture, menu);
        return true;
    }

    @Override
    public void onFrameCaptured(Bitmap bitmap) {
        lastPreview = bitmap;
    }

    private  SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mIsSufaceCreated = false;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mIsSufaceCreated = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            startPreview();
        }
    };
}