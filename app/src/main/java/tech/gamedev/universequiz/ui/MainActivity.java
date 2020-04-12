package tech.gamedev.universequiz.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import tech.gamedev.universequiz.R;

public class MainActivity extends AppCompatActivity {

    Button readyBtn;
    Animation btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniViews();
        setBackGroundMusic();
        setBtnAnim();
        setBtnOnClick();


    }

    private void setBtnOnClick() {
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setBtnAnim() {
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_scale_anim);
        readyBtn.startAnimation(btnAnim);
    }

    private void setBackGroundMusic() {
        MediaPlayer player = MediaPlayer.create(this, R.raw.bg_music);
        player.setLooping(true);
        player.start();
    }

    private void iniViews() {
        readyBtn = findViewById(R.id.ready_btn);
    }
}
