package tech.gamedev.universequiz.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hotchemi.android.rate.AppRate;
import tech.gamedev.universequiz.Common.Common;
import tech.gamedev.universequiz.R;
import tech.gamedev.universequiz.Services.HomeWatcher;
import tech.gamedev.universequiz.Services.MusicService;
import tech.gamedev.universequiz.model.QuestionScore;

public class DoneActivity extends AppCompatActivity {

    Button btnTryAgain;
    TextView txtResultScore,getTextResultQuestion;
    ProgressBar progressBar;
    HomeWatcher mHomeWatcher;

    FirebaseDatabase database;
    DatabaseReference question_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        AppRate.with(this)
                .setInstallDays(0)
                .setLaunchTimes(0)
                .setRemindInterval(1)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);


        //BIND Music Service
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        //Start HomeWatcher
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");

        txtResultScore = findViewById(R.id.txtTotalScore);
        getTextResultQuestion = findViewById(R.id.get_text_result_questions);
        progressBar = findViewById(R.id.doneProgressBar);
        btnTryAgain = findViewById(R.id.try_again);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickSound();
                Intent intent = new Intent(DoneActivity.this,QuizActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Bundle extra = getIntent().getExtras();
        if(extra != null)
        {
            int score = extra.getInt("SCORE");
            int totalQuestions = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT");

            txtResultScore.setText(String.format("SCORE : %d",score));
            getTextResultQuestion.setText(String.format("PASSED : %d / %d",correctAnswer,totalQuestions));

            progressBar.setMax(totalQuestions);
            progressBar.setProgress(correctAnswer);

            //upload point ot db
            question_score.child(String.format("%s_%s", Common.currentUser.getUserName(),
                    Common.levelId)).setValue(new QuestionScore(String.format("%s_%s", Common.currentUser.getUserName(),
                    Common.levelId),
                    Common.currentUser.getUserName(),
                    String.valueOf(score),
                    Common.levelId,
                    Common.levelName));

        }
    }

    //Bind/Unbind music service
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Detect idle screen
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //UNBIND music service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);

    }

    private void playClickSound(){
        final MediaPlayer mp = MediaPlayer.create(this,R.raw.click_sound);
        mp.start();

    }
}
