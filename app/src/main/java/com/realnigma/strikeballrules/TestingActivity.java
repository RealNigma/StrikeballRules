package com.realnigma.strikeballrules;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private int result;

    //Имя тестируемого
    private String userName;

    //id авторизованного пользователя
    private String userId;

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

        userId = intent.getStringExtra("userId");

        //Сохраненное состояние пустое - значит Activity создается в первый раз
        if (savedInstanceState == null) {
            questionList = new QuestionList();
            getCloudQuestions();
            isStateRestored = false;
            }
        else {
            result = savedInstanceState.getInt("result");

            savedAnswersArray = savedInstanceState.getStringArrayList("savedAnswersArray");
            checkedRadioButtonId = savedInstanceState.getInt("checkedRadioButtonId");
            problemTopicList = savedInstanceState.getStringArrayList("problemTopicList");


            Gson gson = new Gson();
            String json = savedInstanceState.getString("questionList", "");
            questionList = gson.fromJson(json, QuestionList.class);


            isStateRestored = true;
            getQuestion();
            setObjectsVisibility();

            //questionList.setCurrentQuestionNum(savedInstanceState.getInt("currentQuestion"));
            //currentQuestionRandom = savedInstanceState.getInt("currentQuestionRandom");
            //randomOrderQuestions = savedInstanceState.getIntegerArrayList("randomOrderQuestions");

            }

        //Если вопрос всего один
        if (questionList.getQuestionsCount() == 1){
            acceptButton.setText("Закончить тестирование");
        }

    }

    //Сохраняем дополнительные данные формы, чтобы они не терялись при автоповороте или сворачивании на длительное время
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("result", result);
        savedInstanceState.putInt("currentQuestion", questionList.getCurrentQuestionNum());
        savedInstanceState.putStringArrayList("savedAnswersArray", answersArray);
        savedInstanceState.putStringArrayList("problemTopicList", problemTopicList);
        savedInstanceState.putInt("checkedRadioButtonId", answersGroup.getCheckedRadioButtonId());

        //savedInstanceState.putSerializable("questionList", questionList);
        Gson gson = new Gson();
        String json = gson.toJson(questionList);
        savedInstanceState.putString("questionList",json);

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
        nameTextView.setText(getString(R.string.question_from, num+1, questionList.getQuestionsCount()));


        //Инициализируем настройки
        String APP_PREFERENCES = "settings";
        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //Подсказка о нескольких вариантах ответа будет показываться всего один раз
        if (mSettings.getBoolean("APP_PREFERENCES_HINT_MULTIPLE",true) && questionList.getRightAnswersNum() > 1) {
            CardView hindCard = findViewById(R.id.hintCardView);
            hindCard.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("APP_PREFERENCES_HINT_MULTIPLE", false);
            editor.apply();
        }
    }


    //Проверяем правильность ответа
    public void checkResult(View view){

        //Если список вопросов пуст, то ничего не происходит
        if (questionList.isEmpty()){
            return;
        }

        //Сброс значения переменной при смене вопроса
        isStateRestored = false;

        //Варианты ответа RadioButton


        //Если один из RadioButtons выбран
        if (answersGroup.getCheckedRadioButtonId() != -1
                && questionList.getRightAnswersNum() == 1) {

            RadioButton checkedRadioButton = findViewById(answersGroup.getCheckedRadioButtonId());

            //Правильные ответы
            ArrayList<String> rightAnswers = questionList.getRightAnswers();

            String checkedButtonText = checkedRadioButton.getText().toString();
            String rightAnswerText = rightAnswers.get(0);

            //Прибавляем к результату 1 балл
            if (checkedButtonText.equals(rightAnswerText))
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
                     if(checkBox.getText().equals(rightAnswers.get(j)) && checkBox.isChecked())
                        selectedRightNumber++;

                for (int k = 0; k < wrongAnswersNumber; k++)
                    if(checkBox.getText().equals(wrongAnswers.get(k)) && checkBox.isChecked())
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
        else {
            getQuestion();

            //Сброс выбора ответа, answersGroup.getCheckedRadioButtonId() будет сброшен и не будет создавать проблем
            answersGroup.clearCheck();

            //Анимация смены вопроса
            CardView card = findViewById(R.id.cardView);
            Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left); //R.anim.down_to_up;
            anim.setDuration(200);
            card.startAnimation(anim);
        }

    }

    //Скрыть карточку с подсказкой
    public void dismissCardHint(View view){
        CardView hindCard = findViewById(R.id.hintCardView);
        hindCard.setVisibility(View.GONE);
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


    //Получаем вопросы из облака
    private void getCloudQuestions (){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("questions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                Question question = doc.toObject(Question.class);
                                questionList.addQuestion(question);
                            }
                        }
                        //Загружаем offline вопросы
                        // if (questionList.isEmpty()){
                        //questionList.setOfflineQuestions();
                        // }
                        questionList.shuffleQuestions();
                        getQuestion();

                        setObjectsVisibility();
                    }
                    //Log.d(TAG," ");
                });

    }

    //Полученные данных методом get()
    /*private void readSlow(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Question question = document.toObject(Question.class);
                                questionList.addQuestion(question);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        getQuestion();
                    }
                });
    }*/

    //Получение данных из RealTimeDatabase
    /* private void readRDData(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference questionsRef = mDatabase.child("questions_short");

        ValueEventListener questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);
                    questionList.addQuestion(question);
                    getQuestion();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        questionsRef.addValueEventListener(questionListener);

    }*/

    //Полученные данных через метод get() c использованием Callback
   /* private void readData(final FirestoreCallback firestoreCallback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("questions_short")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Question question = document.toObject(Question.class);
                                questionList.addQuestion(question);
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            firestoreCallback.onCallback(questionList);
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        //questionList = cloudQuestions;
                        //questionList.setQuestionsCount(cloudQuestions.getQuestionsCount());
                        //questionList.questions = cloudQuestions.questions;


                    }
                });
    }

    private interface FirestoreCallback {
        void onCallback(QuestionList list);
    } */

    //Считаем количество записей в коллекции
   /* private Task<Integer> getCount(final CollectionReference ref) {
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
    }*/

    //Отправляем вопросы в облако
    /*private void sendQuestions(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i <= (questionList.getQuestionsCount()-1); i++) {
            Map<String, Object> question = new HashMap<>();
            question.put("questionText", questionList.getQuestionText());
            question.put("questionTopic", questionList.getQuestionTopic());
            question.put("rightAnswers", questionList.getRightAnswers());
            question.put("wrongAnswers", questionList.getWrongAnswers());

            db.collection("questions_short").document(String.valueOf(i)).set(question);
        }
    }*/

    //Делаем текстовые поля и кнопку "Далее" видимыми
    private void setObjectsVisibility() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        TextView questionText = findViewById(R.id.questionText);
        questionText.setVisibility(View.VISIBLE);

        View divider = findViewById(R.id.divider);
        divider.setVisibility(View.VISIBLE);

        acceptButton.setVisibility(View.VISIBLE);

        //Смена названия кнопки перед последним вопросом
        if (questionList.getCurrentQuestionNum() + 1 == questionList.getQuestionsCount()){
            acceptButton.setText("Закончить тестирование");
        }
    }

    //Отправляем результаты тестирования в облако
    private void sendCloud() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Date time = Calendar.getInstance().getTime();

        //DateFormat df = new DateFormat.getDateInstance();
        //String formattedDate = df.format(time);

        // Create a new user with a first, middle, and last name
        Map<String, Object> user = new HashMap<>();
        user.put("userName", userName);
        user.put("result", result);
        user.put("date", new Timestamp(new Date()));
        user.put("problemTopicList" , problemTopicList);

        db.collection("users").document(userId).set(user);
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
