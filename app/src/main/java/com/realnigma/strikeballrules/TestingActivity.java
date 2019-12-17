package com.realnigma.strikeballrules;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TestingActivity extends AppCompatActivity {

    private static final String TAG = "TestingActivity";

    //Текстовые поля с информицей о вопросе и текстом вопроса
    private TextView nameTextView;

    //Класс списка вопросов
    private QuestionList questionList;

    //Кнопка принять
    private Button acceptButton;

    //Контейнер с вариантами ответа
    private RadioGroup answersGroup;

    //Номер текущего вопроса - для обращения к фиксированному списку вопросов
    //private int currentQuestion = 0;

    //Также номер текущего вопроса, но для расположения вопросов в случайном порядке
   // private int currentQuestionRandom = 0;

    //Массив IDs вопросов, которые будут расположены в случайном порядке
   // private  ArrayList<Integer> randomOrderQuestions = new ArrayList<>();

    //Результат
    private int result = 0;

    //Имя тестируемого
    private String userName;

    //Список проблемных тем вопроса при которых были даны неверные ответы
    private ArrayList<String> problemTopicList = new ArrayList<>();

    //Загружается ли Activity после пересоздания
    private boolean isStateRestored;

    //Выбранный radioButton. Для восстановления выбранного варианта ответа при пересоздании Activity (например, автоповорот)
    private int checkedRadioButtonId;

    //Массив для размещения всех видов ответов
    private ArrayList<String> answersArray = new ArrayList<>();

    private ArrayList<String> savedAnswersArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        //Запрещаем поворот экрана
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Получение имени пользователи из предыдущей активности
        Intent intent = getIntent();
        userName = intent.getStringExtra("Имя");
        //Текст сверху: Номер вопроса + " из " + Количество вопросов
        nameTextView = findViewById(R.id.nameTextView);
        //nameTextView.setText("Вопрос: "+ currentQuestionRandom + 1 + " из " + questionsCount);
        //Инициализируем кнопку "Принять"
        acceptButton = findViewById(R.id.acceptButton);

        //Задаем вопрос и ответы
        questionList = new QuestionList();
        //Инициализируем вопросы
        //Заранее созданный список вопрос на случай отстуствия интернета
        questionList.setOfflineQuestions();

        //sendQuestions();

        if (questionList.getQuestionsCount() == 1){
            acceptButton.setText("Закончить тестирование");
        }


        //Сохраненное состояние пустое - значит Activity создается в первый раз
        if (savedInstanceState == null) {

           /* //Формируем список IDs вопросов
            for (int i = 0; i < questionList.getQuestionsCount(); i++){
                //randomOrderQuestions.add(i);
            }
            //Распологаем в случайном порядке
            Collections.shuffle(randomOrderQuestions);*/
            questionList.shuffleQuestions();
            isStateRestored = false;
            }
        else {
            result = savedInstanceState.getInt("result");
            questionList.setCurrentQuestionNum(savedInstanceState.getInt("currentQuestion"));
            //currentQuestionRandom = savedInstanceState.getInt("currentQuestionRandom");
            //randomOrderQuestions = savedInstanceState.getIntegerArrayList("randomOrderQuestions");
            savedAnswersArray = savedInstanceState.getStringArrayList("savedAnswersArray");
            isStateRestored = true;
            checkedRadioButtonId = savedInstanceState.getInt("checkedRadioButtonId");
            problemTopicList = savedInstanceState.getStringArrayList("problemTopicList");
            }

       // isStateRestored = savedInstanceState.getBoolean("isStateRestored");

            //Загружаем вопрос
        getQuestion();


    }

    //Сохраняем дополнительные данные формы, чтобы они не терялись при автоповороте или сворачивании на длительное время
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("result", result);
        savedInstanceState.putInt("currentQuestion", questionList.getCurrentQuestionNum());
        //savedInstanceState.putInt("currentQuestionRandom", currentQuestionRandom);
        //savedInstanceState.putIntegerArrayList("randomOrderQuestions", randomOrderQuestions);

        savedInstanceState.putStringArrayList("savedAnswersArray", answersArray);

        savedInstanceState.putStringArrayList("problemTopicList", problemTopicList);

        savedInstanceState.putInt("checkedRadioButtonId", answersGroup.getCheckedRadioButtonId());



        super.onSaveInstanceState(savedInstanceState);
    }

    //Обработка нажания кнопки "Назад"
    @Override
    public void onBackPressed() {

        //Создаем диалог

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вернуться назад и прервать тестирование?");

        //Кнопка "Прервать"
        builder.setPositiveButton("Прервать", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //endTest();
                //Intent intent = new Intent(".MainActivity");
                //startActivity(intent);
                finish();
            }
        });
        //Кнопка "Отмена"
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               //Ничего не делаем, окно просто закрывается
            }
        });


        AlertDialog dialog = builder.create();
        //dialog.setTitle("Заголовок");
        dialog.show();
    }

    private void getQuestion() {

        //acceptButton.setVisibility(View.VISIBLE);

        //Анимация появления кнопки
        //Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        //acceptButton.startAnimation(anim);
        //anim.setDuration(1200);

        //Получаем номер текущего вопроса
       // questionList.getCurrentQuestionNum();

        //Варианты ответов с возможностью выбрать только один вариант
        answersGroup = findViewById(R.id.RadioGroup);

        //Очищаем answersGroup
        answersGroup.removeAllViews();

        //Задаем текст вопроса
        TextView questionTextView;
        questionTextView = findViewById(R.id.questionText);
        questionTextView.setText(questionList.getQuestionText());

        answersArray.clear();

        //Число вариантов ответа
        int wrongAnswersNumber = questionList.getWrongAnswersNum();
        int rightAnswersNumber = questionList.getRightAnswersNum();
        int answersNumber = wrongAnswersNumber + rightAnswersNumber;

        //Формируем список вопросов в первый раз
        if (!isStateRestored) {

            //Пишем неправильные и правильные ответы в массив answersArray

            ArrayList<String> wrongAnswers = questionList.getWrongAnswers();
            for (int i = 0; i < wrongAnswersNumber; i++){
                answersArray.add(wrongAnswers.get(i));
            }

            ArrayList<String> rightAnswers = questionList.getRightAnswers();
            for (int i = 0; i < rightAnswersNumber; i++){
                answersArray.add(rightAnswers.get(i));
            }

            //Перемешиваем порядок вариантов ответов
            Collections.shuffle(answersArray);
        }
        if (isStateRestored)   {
            answersArray = savedAnswersArray;
        }


        //Добавляем варианты ответов из answersArray в answersGroup

        //Создаем params для установки Margins между вариантами ответов
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,8,0,8);

        //Массивы элементамов RadioButton и CheckBox, которые будут добавляться динамически
        RadioButton[] radioButtons = new RadioButton[answersNumber];
        CheckBox[] checkBoxes = new CheckBox[answersNumber];

        //В засимости от количества правильных ответов размещаем либо CheckBoxes, либо RadioButtons
        if (questionList.getRightAnswersNum() > 1) {
            for (int i = 0; i < answersNumber; i++){
                //Объявляем все элементы массива
                checkBoxes[i] = new CheckBox(this);
                //Задаем текст вопроса
                checkBoxes[i].setText(answersArray.get(i));
                //Задаем ID для последюущей проверки выбранных значений
                checkBoxes[i].setId(i);

                checkBoxes[i].setLayoutParams(params);
                //Добавляем вариант ответа в answersGroup
                answersGroup.addView(checkBoxes[i]);
            }
        }

        if (questionList.getRightAnswersNum() == 1){
            for (int i = 0; i < answersNumber; i++){
                //Объявляем все элементы массива
                radioButtons[i] = new RadioButton(this);
                //Задаем текст вопроса
                radioButtons[i].setText(answersArray.get(i));

                radioButtons[i].setId(i);

                //Отступы сверху и снизу
                radioButtons[i].setLayoutParams(params);

                //Добавляем вариант ответа в answersGroup
                answersGroup.addView(radioButtons[i]);
            }
        }

        //Восстанавливаем выбранный вариант ответа

        if (isStateRestored)  {
            answersGroup.check(checkedRadioButtonId);
        }

        //Выводим номер вопроса
        int num = questionList.getCurrentQuestionNum();
        nameTextView.setText("Вопрос: " + Integer.toString(num + 1) + " из " + questionList.getQuestionsCount());

    }

    //Проверяем правильность ответа
    public void checkResult(View view){
        //Сброс значения переменной при смене вопроса
        isStateRestored = false;

        //Варианты ответа RadioButton

        //Если один из RadioButtons выбран
        if (answersGroup.getCheckedRadioButtonId() != -1
                && questionList.getRightAnswersNum() == 1) {

            RadioButton checkedRadioButton = findViewById(answersGroup.getCheckedRadioButtonId());

            //Правильные ответы
            ArrayList<String> rightAnswers = questionList.getRightAnswers();

            //Прибавляем к результату 1 балл
            if (checkedRadioButton.getText() == rightAnswers.get(0))
                result++;
            //Отмечаем проблемную тему
            else if (!questionList.getQuestionTopic().equals("Вне правил"))
                problemTopicList.add(questionList.getQuestionTopic());

            }

        //Если ни один из RadioButtons, то записываем проблемную тему
        if (answersGroup.getCheckedRadioButtonId() == -1 &&
                questionList.getRightAnswersNum() == 1)

            if (questionList.getQuestionTopic() != null && !questionList.getQuestionTopic().equals("Вне правил"))
                problemTopicList.add(questionList.getQuestionTopic());


        //Варианты ответа CheckBoxes

        //Если количество правильных ответов больше 1
        if (questionList.getRightAnswersNum() > 1) {

            //Число вариантов ответа
            int wrongAnswersNumber = questionList.getWrongAnswersNum();
            int rightAnswersNumber = questionList.getRightAnswersNum();
            int answersNumber = wrongAnswersNumber + rightAnswersNumber;

            //Правильные и неправильные ответы
            ArrayList<String> rightAnswers = questionList.getRightAnswers();
            ArrayList<String> wrongAnswers = questionList.getWrongAnswers();

            //Количество правильно и неправльно выбранных ответов
            int selectedRightNumber = 0;
            int selectedWrongNumber = 0;

            for (int i = 0; i <answersNumber; i++){
                //CheckBox для проверки
                CheckBox checkBox = findViewById(i);
                 for (int j = 0; j < rightAnswersNumber; j++)
                     if(checkBox.getText() == rightAnswers.get(j) && checkBox.isChecked())
                        selectedRightNumber++;

                for (int k = 0; k < wrongAnswersNumber; k++)
                    if(checkBox.getText() == wrongAnswers.get(k) && checkBox.isChecked())
                        selectedWrongNumber++;
                 }
            //Прибавляем 1 балл, если выбраны правильные и не выбраны неправильные
            if (selectedRightNumber == rightAnswersNumber && selectedWrongNumber == 0)
                result++;
            //Отмечаем проблемную тему
            else
            if (!questionList.getQuestionTopic().equals("Вне правил"))
                problemTopicList.add(questionList.getQuestionTopic());
        }


        //Следующий вопрос
        questionList.nextQuestion();



        //Смена названия кнопки перед последним вопросом
        if (questionList.getCurrentQuestionNum() + 1 == questionList.getQuestionsCount()){
            acceptButton.setText("Закончить тестирование");
        }


        //Если вопросы закончились - окончание тестирования
        int currentQuestionNum = questionList.getCurrentQuestionNum();
        int questionsCount = questionList.getQuestionsCount();
        if (currentQuestionNum == questionsCount) {
            endTest();
        }
        //Иначе следующий вопрос
        else getQuestion();

        //Сброс выбора ответа, answersGroup.getCheckedRadioButtonId() будет сброшен и не будет создавать проблем
        answersGroup.clearCheck();

        CardView card = findViewById(R.id.cardView);

        //Анимация смены вопроса

        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left); //R.anim.down_to_up;
        anim.setDuration(200);
        card.startAnimation(anim);

    }


    //Окончание тестирования
    private void endTest() {

        //Вызываем activity с итогами тестирования
        Intent intent = new Intent(".ResultActivity");

        //Текст с итогами тестирования
        //String resultText;


        //Вызываем активити с результатом и передаем текст результата

        intent.putExtra("Имя", userName);

        intent.putExtra("Результат", result);

        intent.putExtra("Количество вопросов", questionList.getQuestionsCount());

        intent.putExtra("Список проблемных тем", problemTopicList);

        // Отправляем результаты тестирования в облако
        sendCloud();


        //intent.putStringArrayListExtra("Имя", resultText.toString());
        startActivity(intent);

        //Обнуляем переменные и чистим список проблемных тем
        result = 0;
        problemTopicList.clear();

        //Выгружаем activity, чтобы нельзя было использовать кнопку "Назад"
        finish();

        //Анимация скрытия кнопки
        //Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        //acceptButton.startAnimation(anim);
        //acceptButton.setVisibility(View.GONE);


    }


    private void getCloudQuestions (){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //DocumentReference docRef = db.collection("questions").document("0");
       // docRef.get();

        final QuestionList cloudQuestions = new QuestionList();
        /*Task<Integer> task = getCount(db.collection("questions_short"));

        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer i) {
                cloudQuestions.setQuestionsCount(i);
                cloudQuestions.initQuestions();
            }
        }); */

       db.collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                               Question question = document.toObject(Question.class);
                               cloudQuestions.addQuestion(question);
                               Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        questionList = cloudQuestions;
                        //questionList.setQuestionsCount(cloudQuestions.getQuestionsCount());
                        //questionList.questions = cloudQuestions.questions;

                        getQuestion();
                    }
                });

        /*final Question question = new Question();
            DocumentReference docRef = db.collection("questions").document(Integer.toString(1));

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //question = documentSnapshot.toObject(Question.class);
                }

            }*/
    }

//Получаем количество вопросов
    private Task<Integer> getCount(final CollectionReference ref) {
        return ref.get()
                .continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        int count = 0;
                        for (DocumentSnapshot snap : task.getResult()) {
                            count++;
                        }
                        return count;
                    }
                });
    }


    private void sendQuestions(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i <= (questionList.getQuestionsCount()-1); i++) {
            Map<String, Object> question = new HashMap<>();
            question.put("questionText", questionList.getQuestionText());
            question.put("questionTopic", questionList.getQuestionTopic());
            question.put("rightAnswers", questionList.getRightAnswers());
            question.put("wrongAnswers", questionList.getWrongAnswers());

            db.collection("questions_short").document(String.valueOf(i)).set(question);
        }
    }

    private void sendCloud() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Date time = Calendar.getInstance().getTime();

        //DateFormat df = new DateFormat.getDateInstance();
        //String formattedDate = df.format(time);

        // Create a new user with a first, middle, and last name
        Map<String, Object> user = new HashMap<>();
        user.put("result", result);
        user.put("date", new Timestamp(new Date()));
        user.put("problemTopicList" , problemTopicList);

        db.collection("users").document(userName).set(user);
       // sendQuestions();

        /*db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                });*/
    }


}
