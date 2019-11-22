package com.realnigma.strikeballrules;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class ResultActivity extends AppCompatActivity {

    private TextView progressText, resultText;

    private ProgressBar progressBar;

    private String userName;

    //Список проблемных тем вопроса при которых были даны неверные ответы
    private ArrayList<String> problemTopicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //Запрещаем поворот экрана
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showResult();
    }

    //Метод вывода результата тестирования
    private void showResult() {
        //Получаем результат из предыдущей активности
        Intent intent = getIntent();

        //Имя пользователя
        userName = intent.getStringExtra("Имя");


        //Список разделов для повторения
        problemTopicList = intent.getStringArrayListExtra("Список проблемных тем");

        //Получаем результат и количество вопросов
        int result, questionsCount;
        result = intent.getIntExtra("Результат",0);
        questionsCount = intent.getIntExtra("Количество вопросов",0);

        //Считаем процент правильных ответов
        int percent;
        if (result > 0 ) percent = (result*100)/questionsCount;
        else percent = 0;

        StringBuilder resultSB = new StringBuilder();

        resultSB.append("Имя тестируемого: ").append(userName).append("\n" + "\n" ).append("Результат: ").append(result).append(" из ").append(questionsCount);
        //resultText = "Имя тестируемого: " + userName + "\n" + "\n" + "Результат: " + result + " из " + questionsCount;

        //Удаляем дубликаты в списке проблемных тем, используя множества
        //TreeSet позволяет создать сортированный список
        SortedSet<String> set = new TreeSet<>(problemTopicList);
        problemTopicList.clear();
        problemTopicList.addAll(set);


        //Если есть неправильные ответы - выводим список тем для повторения
        if (result < questionsCount && !problemTopicList.isEmpty()) {
            resultSB.append("\n" + "\n" + "Рекомендуем повторить следующие разделы правил: " + "\n");
            for (int i = 0; i < problemTopicList.size(); i++) {
                resultSB.append("\n").append(" • ").append(problemTopicList.get(i));
            }
        }

        //Поздравительный текст
        if (result == questionsCount)
            resultSB.append("\n" + "\n" + "Поздравляю! Вы ответили на все вопросы правильно" + "\n");

        //Выставляем прогресс взяв процент правильных ответов
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setProgress(percent);

        //Количество вопросов из Общего количества
        progressText = findViewById(R.id.progressText);
        progressText.setText(result + " из " + questionsCount);

        //Общий текст результатов
        resultText = findViewById(R.id.resultText);
        resultText.setText(resultSB.toString());

        //Анимируем ProgressBar
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progressBar.getProgress());
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.start();
    }

    //Кнопка "Сначала"
    public void startOver(View view){
        finish();
    }
}
