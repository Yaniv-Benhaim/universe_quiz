package tech.gamedev.universequiz.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tech.gamedev.universequiz.Common.Common;
import tech.gamedev.universequiz.R;
import tech.gamedev.universequiz.model.QuestionScore;

public class DoneActivity extends AppCompatActivity {

    Button btnTryAgain;
    TextView txtResultScore,getTextResultQuestion;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference question_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");

        txtResultScore = findViewById(R.id.txtTotalScore);
        getTextResultQuestion = findViewById(R.id.get_text_result_questions);
        progressBar = findViewById(R.id.doneProgressBar);
        btnTryAgain = findViewById(R.id.try_again);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    String.valueOf(score)));

        }
    }
}
