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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
    public RadioGroup answersGroup;

    //Номер текущего вопроса - для обращения к фиксированному списку вопросов
    private int currentQuestion = 0;

    //Также номер текущего вопроса, но для расположения вопросов в случайном порядке
    private int currentQuestionRandom = 0;

    //Массив IDs вопросов, которые будут расположены в случайном порядке
    private  ArrayList<Integer> randomOrderQuestions = new ArrayList<>();

    //Результат
    private int result = 0;

    //Имя тестируемого
    private String userName;

    //Количество вопросов
    final int questionsCount = 34;

    //Список проблемных тем вопроса при которых были даны неверные ответы
    private ArrayList<String> problemTopicList = new ArrayList<>();

    //Загружается ли Activity после пересоздания
    private boolean isStateRestored;

    //Выбранный radioButton. Для восстановления выбранного варианта ответа при пересоздании Activity (например, автоповорот)
    private int checkedRadioButtonId;

    //Массив для размещения всех видов ответов
    private ArrayList<String> answersArray = new ArrayList<>();

    private ArrayList<String> savedAnswersArray = new ArrayList<>();

    //Класс описывающий вопрос
    static public class Question {

        //Текст вопроса
        public String questionText;

        //Тема вопроса
        public String questionTopic;

        //Массив правильных ответов
        public ArrayList<String> rightAnswers = new ArrayList<>();

        //Массив неправильных ответов
        public ArrayList<String> wrongAnswers = new ArrayList<>();
    }

    //Массив объектов класса - Список вопросов
    public class QuestionList {

        Question[] questions = new Question[questionsCount];

        //Метод для формирования вопросов и ответов
        private void setQuestions(){

            //Обявляем экзепляр каждого элемента, чтобы избежать ошибки NullPointerException
            for (int i = 0; i <= questionsCount-1; i++) {
                questions[i] = new Question();
            }

            //При добавлении нового вопроса нужно изменить значение константы questionCount

            /*questions[0].questionText = "В каких случаях попадание из стрелкового оружия не засчитывается?";
            questions[0].questionTopic = "8. Игровое оружие, пункт 8.1.2";
            questions[0].rightAnswers.add("При рикошете");
            questions[0].rightAnswers.add("При попадании в оружие");
            questions[0].wrongAnswers.add("При попадании хотя бы одного шара от игрока твоей же стороны");
            questions[0].wrongAnswers.add("При попадании хотя бы одного шара от МАКЛАУДА");


            questions[1].questionText = "Чьи требования во время игрового процесса должны выполняться всеми без исключения присутствующими лицами?";
            questions[1].questionTopic = "2. Организатор игры, пункт 2.1";
            questions[1].rightAnswers.add("Организатора");
            questions[1].wrongAnswers.add("Бескомандника");
            questions[1].wrongAnswers.add("Командира любой команды");
            questions[1].wrongAnswers.add("Командира своей команды");

             */


            questions[0].questionText = "Со скольки лет допускается участие в игре страйкбол?";
            questions[0].questionTopic = "1. Общие положения, пункт 1.2";
            questions[0].rightAnswers.add("С 18, а также с 16, с согласия и письменного разрешения родителей/опекунов");
            questions[0].wrongAnswers.add("С 18");
            questions[0].wrongAnswers.add("С 16");
            questions[0].wrongAnswers.add("С 18, а также с 16, с присутствием на игре родителей/опекунов");
            questions[0].wrongAnswers.add("С 18, а также с 14, с согласия и письменного разрешения родителей/опекунов");

            questions[1].questionText = "Чьи требования во время игрового процесса должны выполняться всеми без исключения присутствующими лицами?";
            questions[1].questionTopic = "2. Организатор игры, пункт 2.1";
            questions[1].rightAnswers.add("Организатора");
            questions[1].wrongAnswers.add("Бескомандника");
            questions[1].wrongAnswers.add("Командира любой команды");
            questions[1].wrongAnswers.add("Командира своей команды");

            questions[2].questionText = "Если игрок своим внешним видом не соответствует требованиям установленным командой, кем он будет являться?";
            questions[2].questionTopic = "3. Страйкбольная команда, пункт 3.2.5";
            questions[2].rightAnswers.add("Бескомандником");
            questions[2].wrongAnswers.add("Покемоном");
            questions[2].wrongAnswers.add("Маклаудом");
            questions[2].wrongAnswers.add("Беспризорником");

            questions[3].questionText = "В костюме какой расцветки бескомандник обязан появляться на играх, если иное не предусмотрено сценарием?";
            questions[3].questionTopic = "6. Бескомандники, пункт 6.3";
            questions[3].rightAnswers.add("В любой незакрепленной за действующими командами");
            questions[3].wrongAnswers.add("В любой закрепленной за действующими командами, без опознавательных шевронов команды");
            questions[3].wrongAnswers.add("В любой, которую смог приобрести");
            questions[3].wrongAnswers.add("У меня есть своя");

            questions[4].questionText = "Какую скорость не должен превышать шарик весом 0,2г на выходе из канала ствола при игре в здании, с ведением одиночного и автоматического огня?";
            questions[4].questionTopic = "8. Игровое оружие, пункт 8.1.1";
            questions[4].rightAnswers.add("135 м/с");
            questions[4].wrongAnswers.add("125 м/с");
            questions[4].wrongAnswers.add("140 м/с");
            questions[4].wrongAnswers.add("130 м/с");

            questions[5].questionText = "Какую скорость не должен превышать шарик весом 0,2г на выходе из канала ствола при игре на улице, с ведением автоматического огня?";
            questions[5].questionTopic = "8. Игровое оружие, пункт 8.1.1";
            questions[5].rightAnswers.add("160 м/с");
            questions[5].wrongAnswers.add("155 м/с");
            questions[5].wrongAnswers.add("165 м/с");
            questions[5].wrongAnswers.add("150 м/с");

            questions[6].questionText = "Какую скорость не должен превышать шарик весом 0,2г на выходе из канала ствола при игре на улице, только с одиночным ведением огня?";
            questions[6].questionTopic = "8. Игровое оружие, пункт 8.1.1";
            questions[6].rightAnswers.add("170 м/с");
            questions[6].wrongAnswers.add("180 м/с");
            questions[6].wrongAnswers.add("165 м/с");
            questions[6].wrongAnswers.add("190 м/с");

            questions[7].questionText = "Какой игрок считается «убитым» стрелковым оружием?";
            questions[7].questionTopic = "8. Игровое оружие, пункт 8.1.2";
            questions[7].rightAnswers.add("При попадании хотя бы одного шара в любую часть тела или снаряжения");
            questions[7].wrongAnswers.add("При попадании хотя бы одного шара рикошетом от другого игрока");
            questions[7].wrongAnswers.add("При попадании хотя бы одного шара рядом с игроком, но не более чем 50 см от него");
            questions[7].wrongAnswers.add("При попадании хотя бы одного шара в оружие");

            questions[8].questionText = "В каких случаях попадание из стрелкового оружия не засчитывается?";
            questions[8].questionTopic = "8. Игровое оружие, пункт 8.1.2";
            questions[8].rightAnswers.add("При рикошете");
            questions[8].rightAnswers.add("При попадании в оружие");
            questions[8].wrongAnswers.add("При попадании хотя бы одного шара от игрока твоей же стороны");
            questions[8].wrongAnswers.add("При попадании хотя бы одного шара от МАКЛАУДА");

            questions[9].questionText = "Какой радиус поражения имитации гранаты (мины, подствольной гранаты), при отсутствии попадания поражающего элемента и укрытия?";
            questions[9].questionTopic = "8. Игровое оружие, пункт 8.2.2";
            questions[9].rightAnswers.add("4 м");
            questions[9].wrongAnswers.add("5 м");
            questions[9].wrongAnswers.add("2 м");
            questions[9].wrongAnswers.add("3 м");

            questions[10].questionText = "Засчитывается ли попадание поражающего элемента имитации гранаты в любую часть тела рикошетом?";
            questions[10].questionTopic = "8. Игровое оружие, пункт 8.2.3";
            questions[10].rightAnswers.add("Да");
            questions[10].wrongAnswers.add("Нет");
            questions[10].wrongAnswers.add("Да, если напонитель гранаты - шары");
            questions[10].wrongAnswers.add("Да, если напонитель гранаты - горох");

            questions[11].questionText = "Допускается ли использовать на играх самодельные имитации гранат?";
            questions[11].questionTopic = "8. Игровое оружие, пункт 8.2.1";
            questions[11].rightAnswers.add("Да, если пиротехнический элемент не превышает «Корсар 6» и внешне он схож с оригиналом");
            questions[11].wrongAnswers.add("Да, если они не запрещены правоохранительными органами власти");
            questions[11].wrongAnswers.add("Да");
            questions[11].wrongAnswers.add("Нет");

            questions[12].questionText = "В каких случаях игрок считается «убитым» при использовании имитации холодного оружия?";
            questions[12].questionTopic = "8. Игровое оружие, пункт 8.3.2";
            questions[12].rightAnswers.add("При нанесении удара в область туловища");
            questions[12].wrongAnswers.add("При нанесении удара в руку");
            questions[12].wrongAnswers.add("При нанесении удара в голову");
            questions[12].wrongAnswers.add("Кинув в противника");

            questions[13].questionText = "Что запрещается при использовании имитации холодного оружия?";
            questions[13].questionTopic = "8. Игровое оружие, пункт 8.3.3";
            questions[13].rightAnswers.add("Наносить удары в голову");
            questions[13].rightAnswers.add("Наносить удары в шею");
            questions[13].rightAnswers.add("Производить метание в игрока");
            questions[13].wrongAnswers.add("Наносить удары в руку");

            questions[14].questionText = "Если Вы зашли в здание, разрешается ли использование стрелкового оружия с «тюнингом» свыше 135 м/с. Если да, то в каких случаях? ";
            questions[14].questionTopic = "8. Игровое оружие, пункт 8.1.1";
            questions[14].rightAnswers.add("Да, при стрельбе из окон здания на улицу");
            questions[14].wrongAnswers.add("Да, при стрельбе в здании одиночным огнем");
            questions[14].wrongAnswers.add("Да, при стрельбе в здании с расстояния 15м");
            questions[14].wrongAnswers.add("Нет");

            questions[15].questionText = "Какое защитное снаряжение для глаз должен использовать игрок?";
            questions[15].questionTopic = "9. Игровое снаряжение и экипировка, пункт 9.3";
            questions[15].rightAnswers.add("Прошедшее тестирование согласно правил");
            questions[15].wrongAnswers.add("Любое, которое удобнее");
            questions[15].wrongAnswers.add("Выданное на работе");
            questions[15].wrongAnswers.add("Снятое с другого игрока");

            questions[16].questionText = "Щиты должны соответствовать следующим критериям:";
            questions[16].questionTopic = "9. Игровое снаряжение и экипировка, пункт 9.5.1";
            questions[16].rightAnswers.add("Иметь вид и габариты реально существующих щитов");
            questions[16].wrongAnswers.add("Иметь вид реально существующих щитов");
            questions[16].wrongAnswers.add("Иметь вид, массу и габариты реально существующих щитов");
            questions[16].wrongAnswers.add("Только габариты реально существующих щитов");

            questions[17].questionText = "Разрешается ли строительство фортификационных и оборонительных сооружений во время игры?";
            questions[17].questionTopic = "10. Фортификационные и оборонительные инженерные сооружения, пункт 10.7";
            questions[17].rightAnswers.add("Запрещается, если не предусмотрено сценарием");
            questions[17].wrongAnswers.add("Запрещается, если материалы не прошли проверку");
            questions[17].wrongAnswers.add("Разрешается, если командир команды дал добро");
            questions[17].wrongAnswers.add("Разрешается в любых случаях");


            questions[18].questionText = "Что запрещается убитому игроку?";
            questions[18].questionTopic = "12. Игровая смерть, пункт 12.3";
            questions[18].rightAnswers.add("Передавать кому-либо своё игровое оружие");
            questions[18].rightAnswers.add("Делиться боеприпасами");
            questions[18].rightAnswers.add("Вступать в разговоры с живыми игроками");
            questions[18].wrongAnswers.add("Вступать в разговор с организатором игры");

            questions[19].questionText = "Размер красной ленты должен быть:";
            questions[19].questionTopic = "12. Игровая смерть, пункт 12.1.1";
            questions[19].rightAnswers.add("не менее 400мм в длину и не менее 30мм в ширину");
            questions[19].wrongAnswers.add("не менее 600мм в длину и не менее 30мм в ширину");
            questions[19].wrongAnswers.add("не менее 400мм в длину и не менее 300мм в ширину");
            questions[19].wrongAnswers.add("не менее 200мм в длину и не менее 30мм в ширину");
            questions[19].wrongAnswers.add("не менее 350мм в длину и не менее 40мм в ширину");

            questions[20].questionText = "Как убитый игрок должен обозначить себя:";
            questions[20].questionTopic = "12. Игровая смерть, пункт 12.1";
            questions[20].rightAnswers.add("Поднять руку, достать красную ленту (днём) или включить мигающий маячок (фонарь) красного цвета (ночью)");
            questions[20].wrongAnswers.add("Достать красную ленту (днём) или включить мигающий маячок (фонарь) красного цвета (ночью)");
            questions[20].wrongAnswers.add("Поднять руку, достать красную ленту (днём) или включить мигающий маячок (фонарь) белого цвета (ночью)");
            questions[20].wrongAnswers.add("Достать красную ленту");
            questions[20].wrongAnswers.add("Поднять руку");
            questions[20].wrongAnswers.add("Поднять руку, достать красную ленту (ночью) или включить мигающий маячок (фонарь) красного цвета (днем)");

            questions[21].questionText = "Какие действия используются при пленении игрока?";
            questions[21].questionTopic = "13. Пленные, пункт 13.1";
            questions[21].rightAnswers.add("Коснуться рукой");
            questions[21].rightAnswers.add("Объявить о пленении");
            questions[21].wrongAnswers.add("Ткнуть оружием в спину");
            questions[21].wrongAnswers.add("Произвести захват и повалить на землю");

            questions[22].questionText = "Каждого пленного игрока, сопровождает конвой из скольки игроков:";
            questions[22].questionTopic = "13. Пленные, пункт 13.2";
            questions[22].rightAnswers.add("1");
            questions[22].wrongAnswers.add("0");
            questions[22].wrongAnswers.add("2");
            questions[22].wrongAnswers.add("3");
            questions[22].wrongAnswers.add("Состав конвоя не установлен правилами страйкбола г. Норильска");

            questions[23].questionText = "Максимальная дистанция между пленным и конвоем сопровождения (при привышение данной дистанции, игрок считается сбежавшим):";
            questions[23].questionTopic = "13. Пленные, пункт 13.2";
            questions[23].rightAnswers.add("10");
            questions[23].wrongAnswers.add("1");
            questions[23].wrongAnswers.add("3");
            questions[23].wrongAnswers.add("5");
            questions[23].wrongAnswers.add("7");

            questions[24].questionText = "Сколько вопросов можно задать пленному игроку?";
            questions[24].questionTopic = "13. Пленные, пункт 13.3";
            questions[24].rightAnswers.add("3 простых вопроса или 1 расширенный вопрос");
            questions[24].wrongAnswers.add("1 простой вопрос или 1 расширенный вопрос");
            questions[24].wrongAnswers.add("4 простых вопроса или 2 расширенный вопрос");
            questions[24].wrongAnswers.add("5 простых вопроса или 1 расширенный вопрос");
            questions[24].wrongAnswers.add("1 расширенный");
            questions[24].wrongAnswers.add("1 простой вопрос");

            questions[25].questionText = "Время нахождения в плену?";
            questions[25].questionTopic = "13. Пленные, пункт 13.4";
            questions[25].rightAnswers.add("Не более 1 часа");
            questions[25].wrongAnswers.add("Не более 30 минут");
            questions[25].wrongAnswers.add("Не более 45 минут");
            questions[25].wrongAnswers.add("Не более 1 часа 30 минут");
            questions[25].wrongAnswers.add("Не регламентировано, время устанаваливает командир стороны");

            questions[26].questionText = "После плена, игроку _______";
            questions[26].questionTopic = "13. Пленные, пункт 13.5";
            questions[26].rightAnswers.add("Запрещается рассказывать любые сведения о противнике, полученные за время нахождения в плену.");
            questions[26].wrongAnswers.add("Разрешается рассказывать любые сведения о противнике, полученные за время нахождения в плену.");

            questions[27].questionText = "Что запрещается делать на игре, при обнаружении инициированного пиротехнического изделия?";
            questions[27].questionTopic = "16. Ограничения, пункт 16.1";
            questions[27].rightAnswers.add("Запрещается брать в руки");
            questions[27].rightAnswers.add("Запрещается повторное использование");
            questions[27].rightAnswers.add("Запрещается отбрасывать от себя");
            questions[27].wrongAnswers.add("Запрещается укрываться от изделия");
            questions[27].wrongAnswers.add("Запрещается смотреть на изделие во время срабатывания");

            questions[28].questionText = "Что должен сделать игрок при входе на территорию «мертвяка» и/или жилого лагеря с стрелковым оружием?";
            questions[28].questionTopic = "16. Ограничения, пункт 16.7";
            questions[28].rightAnswers.add("Отомкнуть магазин");
            questions[28].rightAnswers.add("Произвести отстрел шара в безопасную сторону");
            questions[28].wrongAnswers.add("Перевести оружие «за спину»");
            questions[28].wrongAnswers.add("Поднять оружие дулом к верху");

            questions[29].questionText = "Если игрок в отношении которого используется имитация гранаты (мина, подствольная граната, растяжка и т.д.) находился на дистанции более 4 метров от взрыва и в игрока попал поражающий элемент изделия, он считается:";
            questions[29].questionTopic = "8. Игровое оружие, пункт 8.2.3";
            questions[29].rightAnswers.add("Убитым");
            questions[29].wrongAnswers.add("Раненым");
            questions[29].wrongAnswers.add("На дистанции больше 4 метров попадания не засчитываются");

            questions[30].questionText = "Как должна осуществляться переноска оружия вне полигона?";
            questions[30].questionTopic = "Вне правил";
            questions[30].rightAnswers.add("В чехле");
            questions[30].rightAnswers.add("С отсоединенным магазином");
            questions[30].wrongAnswers.add("С пристегнутым аккумулятором");
            questions[30].wrongAnswers.add("С отсоединенным прицелом");

            questions[31].questionText = "Используют киперную-ограждающую ленту на полигоне, для:";
            questions[31].questionTopic = "Вне правил";
            questions[31].rightAnswers.add("Обозначения границ полигона (при необходимости)");
            questions[31].rightAnswers.add("Опасных зон на полигоне (куда игрокам проход запрещен)");
            questions[31].rightAnswers.add("Для обозначения мертвяков (при необходимости)");
            questions[31].rightAnswers.add("Для обозначения жилого лагеря при проведении игры");

            questions[32].questionText = "В процессе игры, игрок услышал команду «СТОП ИГРА!», его действия:";
            questions[32].questionTopic = "Вне правил";
            questions[32].rightAnswers.add("Оставаться на месте и ждать дальнейших указаний от организаторов");
            questions[32].rightAnswers.add("Продублировать команду (голосом, по рации)");
            questions[32].wrongAnswers.add("Продолжить играть");
            questions[32].wrongAnswers.add("Передвигаться по полигону, чтобы узнать позиции других игроков");
            questions[32].wrongAnswers.add("Вернуться на базу мертвяке");


            questions[33].questionText = "Как должны разрешаться спорные ситуации на игре?:";
            questions[33].questionTopic = "Вне правил";
            questions[33].rightAnswers.add("После ее окончания");
            questions[33].rightAnswers.add("В неигровой зоне");
            questions[33].wrongAnswers.add("Предложить спаринг");
            questions[33].wrongAnswers.add("Послать куда подальше");


        }

        private void initQuestions() {

            //Обявляем экзепляр каждого элемента, чтобы избежать ошибки NullPointerException
            for (int i = 0; i <= questionsCount - 1; i++) {
                questions[i] = new Question();
            }
        }


    }

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
        questionList.setQuestions();
        getCloudQuestions();

            //Сохраненное состояние пустое - значит Activity создается в первый раз
        if (savedInstanceState == null) {

            //Формируем список IDs вопросов
            for (int i = 0; i < questionsCount; i++){
                randomOrderQuestions.add(i);
            }
            //Распологаем в случайном порядке
            Collections.shuffle(randomOrderQuestions);

            isStateRestored = false;
            }
        else {
            result = savedInstanceState.getInt("result");
            currentQuestion = savedInstanceState.getInt("currentQuestion");
            currentQuestionRandom = savedInstanceState.getInt("currentQuestionRandom");
            randomOrderQuestions = savedInstanceState.getIntegerArrayList("randomOrderQuestions");
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
        savedInstanceState.putInt("currentQuestion", currentQuestion);
        savedInstanceState.putInt("currentQuestionRandom", currentQuestionRandom);
        savedInstanceState.putIntegerArrayList("randomOrderQuestions", randomOrderQuestions);

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

        //Кнопка "Превать"
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

        //Получаем номер вопроса из списка IDs расположенных в случайном порядке
        currentQuestion = randomOrderQuestions.get(currentQuestionRandom);

        //Варианты ответов с возможностью выбрать только один вариант
        answersGroup = findViewById(R.id.RadioGroup);

        //Очищаем answersGroup
        answersGroup.removeAllViews();

        //Задаем текст вопроса
        TextView questionTextView;
        questionTextView = findViewById(R.id.questionText);
        questionTextView.setText(questionList.questions[currentQuestion].questionText);




        answersArray.clear();

        //Число вариантов ответа
        int wrongAnswersNumber = questionList.questions[currentQuestion].wrongAnswers.size();
        int rightAnswersNumber = questionList.questions[currentQuestion].rightAnswers.size();
        int answersNumber = wrongAnswersNumber + rightAnswersNumber;

        //Формируем список вопрос в первый раз
        if (!isStateRestored) {

            //Пишем неправильные и правильные ответы в массив answersArray
            for (int i = 0; i < wrongAnswersNumber; i++){
                answersArray.add(questionList.questions[currentQuestion].wrongAnswers.get(i));
            }

            for (int i = 0; i < rightAnswersNumber; i++){
                answersArray.add(questionList.questions[currentQuestion].rightAnswers.get(i));
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
        if (questionList.questions[currentQuestion].rightAnswers.size() > 1) {
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

        if (questionList.questions[currentQuestion].rightAnswers.size() == 1){
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
        nameTextView.setText("Вопрос: " + Integer.toString(currentQuestionRandom + 1) + " из " + questionsCount);

    }

    //Проверяем правильность ответа
    public void checkResult(View view){
        //Сброс значения переменной при смене вопроса
        isStateRestored = false;

        //Варианты ответа RadioButton

        //Если один из RadioButtons выбран
        if (answersGroup.getCheckedRadioButtonId() != -1
                && questionList.questions[currentQuestion].rightAnswers.size() == 1) {

            RadioButton checkedRadioButton = findViewById(answersGroup.getCheckedRadioButtonId());

            //Прибавляем к результату 1 балл
            if (checkedRadioButton.getText() == questionList.questions[currentQuestion].rightAnswers.get(0))
                result++;
            //Отмечаем проблемную тему
            else if (!questionList.questions[currentQuestion].questionTopic.equals("Вне правил"))
                problemTopicList.add(questionList.questions[currentQuestion].questionTopic);

            }

        //Если ни один из RadioButtons, то записываем проблемную тему
        if (answersGroup.getCheckedRadioButtonId() == -1 &&
                questionList.questions[currentQuestion].rightAnswers.size() == 1)

            if (!questionList.questions[currentQuestion].questionTopic.equals("Вне правил"))
                problemTopicList.add(questionList.questions[currentQuestion].questionTopic);


        //Варианты ответа CheckBoxes

        //Если количество правильных ответов больше 1
        if (questionList.questions[currentQuestion].rightAnswers.size() > 1) {

            //Число вариантов ответа
            int wrongAnswersNumber = questionList.questions[currentQuestion].wrongAnswers.size();
            int rightAnswersNumber = questionList.questions[currentQuestion].rightAnswers.size();
            int answersNumber = wrongAnswersNumber + rightAnswersNumber;

            //Количество правильно и неправльно выбранных ответов
            int selectedRightNumber = 0;
            int selectedWrongNumber = 0;

            for (int i = 0; i <answersNumber; i++){
                //CheckBox для проверки
                CheckBox checkBox = findViewById(i);
                 for (int j = 0; j < rightAnswersNumber; j++)
                     if(checkBox.getText() == questionList.questions[currentQuestion].rightAnswers.get(j) && checkBox.isChecked())
                        selectedRightNumber++;

                for (int k = 0; k < wrongAnswersNumber; k++)
                    if(checkBox.getText() == questionList.questions[currentQuestion].wrongAnswers.get(k) && checkBox.isChecked())
                        selectedWrongNumber++;
                 }
            //Прибавляем 1 балл, если выбраны правильные и не выбраны неправильные
            if (selectedRightNumber == rightAnswersNumber && selectedWrongNumber == 0)
                result++;
            //Отмечаем проблемную тему
            else
            if (!questionList.questions[currentQuestion].questionTopic.equals("Вне правил"))
                problemTopicList.add(questionList.questions[currentQuestion].questionTopic);
        }


        //Следующий вопрос
        if (currentQuestionRandom < questionsCount)
             currentQuestionRandom++;



        //Смена названия кнопки перед последним вопросом
        if (currentQuestionRandom + 1 == questionsCount){
            acceptButton.setText("Закончить тестирование");
        }


        //Если вопросы закончились - окончание тестирования
        if (currentQuestionRandom == questionsCount)
            endTest();
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

        intent.putExtra("Количество вопросов", questionsCount);

        intent.putExtra("Список проблемных тем", problemTopicList);

        // Отправляем результаты тестирования в облако
        sendCloud();


        //intent.putStringArrayListExtra("Имя", resultText.toString());
        startActivity(intent);

        //Обнуляем переменные и чистим список проблемных тем
        result = 0;
        currentQuestionRandom = 0;
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
        cloudQuestions.initQuestions();

       // QuestionList test = questionList;
        //test.initQuestions();

       db.collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               Question question = document.toObject(Question.class);
                               cloudQuestions.questions[Integer.parseInt(document.getId())] = question;
                               Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        questionList = cloudQuestions;
                        //String test = cloudQuestions.questions[0].questionText;
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





    private void sendQuestions(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i <= questionsCount-1; i++) {
            Map<String, Object> question = new HashMap<>();
            question.put("questionText", questionList.questions[i].questionText);
            question.put("questionTopic", questionList.questions[i].questionTopic);
            question.put("rightAnswers", questionList.questions[i].rightAnswers);
            question.put("wrongAnswers", questionList.questions[i].wrongAnswers);

            db.collection("questions").document(String.valueOf(i)).set(question);
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
        sendQuestions();

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
