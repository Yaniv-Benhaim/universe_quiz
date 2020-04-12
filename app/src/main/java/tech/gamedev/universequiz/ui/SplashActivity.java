package tech.gamedev.universequiz.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import tech.gamedev.universequiz.Common.Common;
import tech.gamedev.universequiz.R;
import tech.gamedev.universequiz.model.User;

public class SplashActivity extends AppCompatActivity {

    //Constant Time Delay
    private final int SPLASH_DELAY = 4000;

    //Fields
    private ImageView imageView;
    MaterialEditText edtNewUser, edtNewPassword, edtNewEmail; // for signup
    MaterialEditText edtUser, edtPassword; // for sign in

    Button btnSignIn, btnSignUp;

    FirebaseDatabase database;
    DatabaseReference users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setBackgroundDrawable(null);

        //Methods to call
        iniViews();
        animateLogo();
        goToMainActivity();
        btnOnClickListeners();

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");






    }

    private void btnOnClickListeners() {

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUser.getText().toString(),edtPassword.getText().toString());
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
                                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
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
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        Animation fadingInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadingInAnimation.setDuration(SPLASH_DELAY);

        imageView.startAnimation(fadingInAnimation);

    }

    private void iniViews() {
        imageView = findViewById(R.id.imageView);
        edtUser = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);



    }
}
