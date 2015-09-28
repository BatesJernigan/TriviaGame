package com.example.triviagame;
import java.util.LinkedList;

/**
 * Created by batesjernigan on 9/25/15.
 */
public class Question {
    private int questionId;
    private String question;
    private LinkedList<String> answers;
    private String pictureUrl;

    public Question(LinkedList<String> answers, String pictureUrl, String question, int questionId) {
        this.answers = answers;
        this.pictureUrl = pictureUrl;
        this.question = question;
        this.questionId = questionId;
    }

    public LinkedList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(LinkedList<String> answers) {
        this.answers = answers;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        String answerString = "";
        for(String answer: answers) {
            answerString += answer + "\n";
        }
        return "Question{" +
            "answers=" + answerString +
            ", questionId=" + questionId +
            ", question='" + question + '\'' +
            ", pictureUrl='" + pictureUrl + '\'' +
            '}';
    }
}
