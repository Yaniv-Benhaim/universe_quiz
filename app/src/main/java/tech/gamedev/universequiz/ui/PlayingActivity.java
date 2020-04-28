package tech.gamedev.universequiz.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import tech.gamedev.universequiz.Common.Common;
import tech.gamedev.universequiz.R;
import tech.gamedev.universequiz.Services.HomeWatcher;
import tech.gamedev.universequiz.Services.MusicService;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener{

    final static long INTERVAL = 20000;
    final static long TIMEOUT = 20000;
    int progressValue = 0;

    HomeWatcher mHomeWatcher;
    CountDownTimer countDownTimer;
    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;

    Animation btnAnim;
    ProgressBar progressBar;
    ImageView question_image;
    Button btnA,btnB,btnC,btnD;
    TextView textScore,textQuestionNum,question_text;
    LinearLayout score_wrap_layout;
    LottieAnimationView checkMark,wrongMark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        iniViews();

        setOnClickListeners();

        setMusic();


    }

    private void setMusic(){
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
    }

    private void setOnClickListeners() {
        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);

        setBtnAnim();
    }

    private void iniViews() {
        //views
        checkMark = findViewById(R.id.check_mark);
        wrongMark = findViewById(R.id.wrong_mark);
        textScore = findViewById(R.id.textScore);
        textQuestionNum = findViewById(R.id.textTotalQuestions);
        question_text = findViewById(R.id.question_text);
        question_image = findViewById(R.id.question_image);

        progressBar = findViewById(R.id.progress_bar);
        score_wrap_layout = findViewById(R.id.score_wrap_layout);

        btnA = findViewById(R.id.btnAnswerA);
        btnB = findViewById(R.id.btnAnswerB);
        btnC = findViewById(R.id.btnAnswerC);
        btnD = findViewById(R.id.btnAnswerD);
    }

    private void setBtnAnim() {
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_scale_anim);
        score_wrap_layout.startAnimation(btnAnim);
    }

    @Override
    public void onClick(View v) {

        countDownTimer.cancel();
        Button clickedButton = (Button) v;
        if(clickedButton.getText().equals(Common.questionList.get(index).getCorrectAnswer()))
        {
            
            //choose correct answer
            checkMark.setVisibility(View.VISIBLE);
            CorrectAnswerSound();


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    score+=10;
                    correctAnswer++;
                    checkMark.setVisibility(View.INVISIBLE);
                    showQuestion(++index);
                }
            }, 1000);
            // next question
        }
        else
        {
            //chooses wrong answer
            wrongMark.setVisibility(View.VISIBLE);
            InCorrectAnswerSound();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wrongMark.setVisibility(View.INVISIBLE);
                    //chooses wrong answer
                    Intent intent = new Intent(PlayingActivity.this,DoneActivity.class);
                    Bundle dataSend = new Bundle();
                    dataSend.putInt("SCORE",score);
                    dataSend.putInt("TOTAL",totalQuestion);
                    dataSend.putInt("CORRECT",correctAnswer);
                    intent.putExtras(dataSend);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }

        textScore.setText(String.format("%d",score));

    }

    private void showQuestion(int index) {
        if(index < totalQuestion)
        {
            thisQuestion++;
            textQuestionNum.setText(String.format("%d / %d",thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue=0;

            if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                Picasso.get().load
                        (Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            }
            else
            {
                question_text.setText(Common.questionList.get(index).getQuestion());
                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);
            }

            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

            countDownTimer.start();
        }
        else
        {
            //if it is the final question
            Intent intent = new Intent(this,DoneActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }

        totalQuestion = Common.questionList.size();
        countDownTimer = new CountDownTimer(TIMEOUT,INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
    private void CorrectAnswerSound(){
        final MediaPlayer mp = MediaPlayer.create(this,R.raw.correct_sound);
        mp.start();

    }

    private void InCorrectAnswerSound(){
        final MediaPlayer mp = MediaPlayer.create(this,R.raw.wrong_sound);
        mp.start();

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
}
