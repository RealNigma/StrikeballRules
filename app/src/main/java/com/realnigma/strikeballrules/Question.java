package com.realnigma.strikeballrules;

import java.util.ArrayList;

//Класс описывающий вопрос
public class Question {

    //Текст вопроса
    public String questionText;

    //Тема вопроса
    public String questionTopic;

    //Массив правильных ответов
    public ArrayList<String> rightAnswers = new ArrayList<>();

    //Массив неправильных ответов
    public ArrayList<String> wrongAnswers = new ArrayList<>();
}
