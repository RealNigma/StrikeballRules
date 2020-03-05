package com.realnigma.strikeballrules;

import java.util.ArrayList;
import java.util.Collections;

//Класс описывающий список вопросов. Инкапсулирует массив объектов типа Question и методы для работы с ним
class QuestionList {

    //Номер текущего вопроса
    private int currentQuestion = 0;

    //Динамический массив объектов с вопросами
    private ArrayList<Question> questions = new ArrayList<>();

    //Получить количество вопросов
    int getQuestionsCount(){
        return questions.size();
    }

    //Получить текст вопроса
    String getQuestionText() {
        if (questions.size() > 0) {
            return questions.get(currentQuestion).questionText;
        }
        else return null;
    }

    //Список вопросов пуст
    boolean isEmpty(){
        boolean result = false;
        if (questions.size() == 0) {
            result = true;
        }
        return result;
    }

    //Получить тему вопроса
    String getQuestionTopic() {
        if (questions.size() > 0) {
            return questions.get(currentQuestion).questionTopic;
        }
        else return null;
    }

    //Получить число правильных ответов
    int getRightAnswersNum(){
        if (questions.size() > 0){
            return questions.get(currentQuestion).rightAnswers.size();
        }
        else return 0;

    }

    //Получить список правильных ответов
    ArrayList<String> getRightAnswers(){
        if (questions.size() > 0) {
            return questions.get(currentQuestion).rightAnswers;
        }
        else return null;
    }

    //Получить список неправильных ответов
    ArrayList<String> getWrongAnswers(){
        if (questions.size() > 0) {
            return questions.get(currentQuestion).wrongAnswers;
        }
        else return null;
    }

    //Получить число неправильных ответов
    int getWrongAnswersNum(){
        if (questions.size() > 0) {
            return questions.get(currentQuestion).wrongAnswers.size();
        }
        else return 0;
    }

    //Получить номер текущего вопроса
    int getCurrentQuestionNum(){
        return currentQuestion;
    }

//    void setCurrentQuestionNum(int currentQuestion){
//        this.currentQuestion = currentQuestion;
//    }

    //Добавить новый вопрос
    void addQuestion(Question question){
        questions.add(question);
    }

    //Следующий вопрос
    void nextQuestion(){
        if (currentQuestion < questions.size())
            currentQuestion++;
    }

    //Расположить вопросы в случайном порядке
    void shuffleQuestions(){
        Collections.shuffle(questions);
    }



}
