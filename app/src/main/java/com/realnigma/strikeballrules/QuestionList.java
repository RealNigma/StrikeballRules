package com.realnigma.strikeballrules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

//Массив объектов класса - Список вопросов
class QuestionList implements Serializable {

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

    //Формируем вопросы и ответы локально
    public void setOfflineQuestions(){

        //При добавлении нового вопроса нужно изменить значение questionsCount TODO: Сделать 34 оффлайн вопроса и подумать как сделать это более изящно.

        Question question = new Question();

        question.questionText = "В каких случаях попадание из стрелкового оружия не засчитывается?";
        question.questionTopic = "8. Игровое оружие, пункт 8.1.2";
        question.rightAnswers.add("При рикошете");
        question.rightAnswers.add("При попадании в оружие");
        question.wrongAnswers.add("При попадании хотя бы одного шара от игрока твоей же стороны");
        question.wrongAnswers.add("При попадании хотя бы одного шара от МАКЛАУДА");

        questions.add(question);

        Question question1 = new Question();

        question1.questionText = "Чьи требования во время игрового процесса должны выполняться всеми без исключения присутствующими лицами?";
        question1.questionTopic = "2. Организатор игры, пункт 2.1";
        question1.rightAnswers.add("Организатора");
        question1.wrongAnswers.add("Бескомандника");
        question1.wrongAnswers.add("Командира любой команды");
        question1.wrongAnswers.add("Командира своей команды");

        questions.add(question1);

        Question question2 = new Question();

        question2.questionText = "В костюме какой расцветки бескомандник обязан появляться на играх, если иное не предусмотрено сценарием?";
        question2.questionTopic = "6. Бескомандники, пункт 6.3";
        question2.rightAnswers.add("В любой незакрепленной за действующими командами");
        question2.wrongAnswers.add("В любой закрепленной за действующими командами, без опознавательных шевронов команды");
        question2.wrongAnswers.add("В любой, которую смог приобрести");
        question2.wrongAnswers.add("У меня есть своя");

        questions.add(question2);

        /*
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

 */

    }




}
