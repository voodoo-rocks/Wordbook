package my.beelzik.mobile.wordbook;

import java.util.List;
import java.util.Random;

import my.beelzik.mobile.wordbook.data.DictionaryData;
import my.beelzik.mobile.wordbook.data.Quiz;
import my.beelzik.mobile.wordbook.data.QuizGameOption;

/**
 * Created by Andrey on 10.01.2016.
 */
public class QuizFactory {

    QuizGameOption mQuizGameOption;

    Random mRandom;

    public QuizFactory(QuizGameOption quizGameOption) {
        mQuizGameOption = quizGameOption;
        mRandom = new Random();
    }

    public Quiz createQuiz(List<DictionaryData> variant){

        DictionaryData.WordType questionWordType = getQuestionWordType();
        int correctAnswerPosition = mRandom.nextInt(variant.size());

        DictionaryData correctAnswer = variant.get(correctAnswerPosition);
        String question = questionWordType == DictionaryData.WordType.NATIVE ?
                correctAnswer.getNative() : correctAnswer.getLearn();

        Quiz quiz = new Quiz();
        quiz.setVariantList(variant);
        quiz.setQuestionWordType(questionWordType);
        quiz.setCorrectAnswer(correctAnswer);
        quiz.setQuestion(question);

        return quiz;
    }

    public DictionaryData.WordType getQuestionWordType() {

        switch (mQuizGameOption.getQuestionLanguage()){
            case QuizGameOption.QUESTION_LANGUAGE_NATIVE:
                return  DictionaryData.WordType.NATIVE;
            case QuizGameOption.QUESTION_LANGUAGE_LEARN:
                return  DictionaryData.WordType.LEARN;
            case QuizGameOption.QUESTION_LANGUAGE_RANDOM:
                return mRandom.nextBoolean() ? DictionaryData.WordType.NATIVE : DictionaryData.WordType.LEARN;
            default:
                return mRandom.nextBoolean() ? DictionaryData.WordType.NATIVE : DictionaryData.WordType.LEARN;
        }

    }
}
