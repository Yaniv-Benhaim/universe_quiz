package tech.gamedev.universequiz.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

import tech.gamedev.universequiz.Common.Common;
import tech.gamedev.universequiz.R;
import tech.gamedev.universequiz.Services.HomeWatcher;
import tech.gamedev.universequiz.Services.MusicService;
import tech.gamedev.universequiz.broadcastReceiver.AlarmReceiver;
import tech.gamedev.universequiz.model.User;

public class SplashActivity extends AppCompatActivity {

    HomeWatcher mHomeWatcher;



    //Constant Time Delay
    private final int SPLASH_DELAY = 4000;
    VideoView videoBg;

    //Fields
    private ImageView imageView;
    MaterialEditText edtNewUser, edtNewPassword, edtNewEmail; // for signup
    EditText edtUser, edtPassword; // for sign in
    TextView logoTitle;

    Button btnSignIn, btnSignUp, guestBtn;

    FirebaseDatabase database;
    DatabaseReference users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



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

        getWindow().setBackgroundDrawable(null);

        //Methods to call
        iniViews();
        animateLogo();
        goToMainActivity();
        btnOnClickListeners();
        registerAlarm();

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");






    }

    private void playClickSound(){
        final MediaPlayer mp = MediaPlayer.create(this,R.raw.click_sound);
        mp.start();

    }

    private void registerAlarm() {
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR_OF_DAY,7);
        calender.set(Calendar.MINUTE,33);
        calender.set(Calendar.SECOND,0);

        Intent intent = new Intent(SplashActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SplashActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);

        am.setRepeating(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


    }

    private void btnOnClickListeners() {

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playClickSound();
                signIn(edtUser.getText().toString(),edtPassword.getText().toString());
            }


        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickSound();
                showSignUpDialog();
            }



            private void showSignUpDialog() {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                alertDialog.setTitle("Sign Up");
                alertDialog.setMessage("Please enter all the required information");

                LayoutInflater inflater = SplashActivity.this.getLayoutInflater();
                View sign_up = inflater.inflate(R.layout.sign_up_layout,null);

                edtNewUser = sign_up.findViewById(R.id.edtNewUserName);
                edtNewEmail = sign_up.findViewById(R.id.edtNewEmail);
                edtNewPassword = sign_up.findViewById(R.id.edtNewPassword);

                alertDialog.setView(sign_up);
                alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final User user = new User(edtNewUser.getText().toString(),
                                edtNewPassword.getText().toString(),
                                edtNewUser.getText().toString());

                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child(user.getUserName()).exists())
                                {
                                    Toast.makeText(SplashActivity.this, "User already Exists!", Toast.LENGTH_SHORT).show();
                                }else
                                {
                                    users.child(user.getUserName()).setValue(user);
                                    Toast.makeText(SplashActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickSound();
                signIn("guest","guest");
            }
        });


    }

    private void signIn(final String user, final String password) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user).exists())
                {
                    if(!user.isEmpty())
                    {
                        User login = dataSnapshot.child(user).getValue(User.class);
                        if(login.getPassWord().equals(password))
                        {
                            Toast.makeText(SplashActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                            Common.currentUser = login;
                            Intent intent = new Intent(SplashActivity.this,QuizActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SplashActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(SplashActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(SplashActivity.this, "Wrong username or password, Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goToMainActivity() {
                /*
               Intent intent = new Intent(SplashActivity.this, MainActivity.class);
               startActivity(intent);
               finish();

                 */


    }

    private void animateLogo() {
       //this method will animate the logo
        Animation fadingInAnimation = AnimationUtils.loadAnimation(this, R.anim.btn_scale_anim);
        fadingInAnimation.setDuration(SPLASH_DELAY);

        //imageView.startAnimation(fadingInAnimation);
        logoTitle.startAnimation(fadingInAnimation);

        /*YoYo.with(Techniques.SlideInUp)
                .duration(7000)
                .repeat(10)
                .playOn(imageView);

         */

    }

    private void iniViews() {
        imageView = findViewById(R.id.imageView);
        edtUser = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
        guestBtn = findViewById(R.id.btn_guest_sign_in);

        logoTitle = findViewById(R.id.logo_title);



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
                Scon,Context.BIND_AUTO_CREATE);
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
}
