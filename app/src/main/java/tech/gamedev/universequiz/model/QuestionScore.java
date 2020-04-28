package tech.gamedev.universequiz.model;

public class QuestionScore {
    private String Question_Score;
    private String User;
    private String Score;
    private String LevelId;
    private String LevelName;


    public QuestionScore() {

    }

    public QuestionScore(String question_Score, String user, String score, String levelId, String levelName) {
        Question_Score = question_Score;
        User = user;
        Score = score;
        LevelId = levelId;
        LevelName = levelName;
    }

    public String getQuestion_Score() {
        return Question_Score;
    }

    public void setQuestion_Score(String question_Score) {
        Question_Score = question_Score;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getLevelId() {
        return LevelId;
    }

    public void setLevelId(String levelId) {
        LevelId = levelId;
    }

    public String getLevelName() {
        return LevelName;
    }

    public void setLevelName(String levelName) {
        LevelName = levelName;
    }
}