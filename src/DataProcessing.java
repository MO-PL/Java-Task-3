import pl.blueenergy.document.ApplicationForHolidays;
import pl.blueenergy.document.DocumentDao;
import pl.blueenergy.document.Question;
import pl.blueenergy.document.Questionnaire;
import pl.blueenergy.organization.User;

import java.util.Collection;
import java.util.List;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import java.lang.reflect.*;
public class DataProcessing {

    public static <T> List<T> getElementsOf(DocumentDao documentDao, Class<T> documentType) {
        return documentDao
                .getAllDocumentsInDatabase()
                .stream()
                .filter(documentType::isInstance)
                .map(documentType::cast)
                .collect(Collectors.toList());
    }
    public static double getMeanNumberOfAnswersInQuestionnaires(List<Questionnaire> questionnaireList){
        double meanValue = questionnaireList
                .stream()
                .map(Questionnaire::getQuestions)
                .flatMap(Collection::stream)
                .map(Question::getPossibleAnswers)
                .map(Collection::size)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(Double.NaN);
        System.out.println("The average value of all possible answers in Questionnaires is: " +
                "\r\n" + meanValue + "\r\n");

        return meanValue;
    }
    public static List<User> getListWithUsersWhoGoOnHoliday(List<ApplicationForHolidays> applicationForHolidaysList){
//      Make the list of Users

        List<User> listWithUsersWhoGoOnHoliday = applicationForHolidaysList
                .stream()
                .map(ApplicationForHolidays::getUserWhoRequestAboutHolidays)
                .collect(Collectors.toList());

//        Example:
//        User kindUser = new User();
//        kindUser.setLogin("ĘdwardĄcki2000");
//        kindUser.setName("Ędward");
//        kindUser.setSurname("Ącki");
//        listWithUsersWhoGoOnHoliday.add(kindUser);

//        Check logins

        listWithUsersWhoGoOnHoliday
                .stream()
                .filter(e -> Stream.of("ą", "ć", "ę", "ł", "ń", "ó", "ś", "ź", "ż")
                        .anyMatch(f -> e.getLogin().toLowerCase(Locale.ROOT).contains(f)))
                .map(e -> "User: " + e.getName() + " " + e.getSurname() + ", has unsuitable login: " +
                        e.getLogin() + "\r\n")
                .forEach(System.out::println);

        return listWithUsersWhoGoOnHoliday;
    }
    public static void checkIfDateOfHolidayIsCorrect(List<ApplicationForHolidays> applicationForHolidaysList){
        applicationForHolidaysList
                .stream()
                .filter(e->!e.getSince().before(e.getTo()))
                .map(e -> "User: " + e.getUserWhoRequestAboutHolidays().getName() +
                        " " + e.getUserWhoRequestAboutHolidays().getSurname() +
                        ", with login: " +  e.getUserWhoRequestAboutHolidays().getLogin() +
                        ", has got replaced the start and the end of the holiday in the application: " +
                        "\r\nStart: " + e.getSince() + ", End: " + e.getTo() + "\r\n")
                .forEach(System.out::println);
    }
    public static void saveQuestionnairToTextFileHelper(Questionnaire questionnaire, String fileName) {

        AtomicInteger questionNumber = new AtomicInteger();
        questionNumber.set(1);

        AtomicInteger answerNumber = new AtomicInteger();

        String textToWrite = questionnaire
                .getQuestions()
                .stream()
                .map(e -> {
                    answerNumber.set(1);
                    return  e
                            .getPossibleAnswers()
                            .stream()
                            .reduce("Pytanie " + questionNumber.getAndIncrement() + ": " +  e.getQuestionText(),
                                    (memory, element) -> memory + "\r\n\t" + answerNumber.getAndIncrement() + ". " +
                                    element);})
                .reduce("Ankieta: " + questionnaire.getTitle(),(memory, element) ->
                        memory + "\r\n\r\n" + element);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName + ".txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf(textToWrite);
        printWriter.close();


        System.out.println(textToWrite);
        System.out.println("The Questinnair is saved in the file: " + fileName + ".txt");
        System.out.println();
    }
    public static void setSalaryOfUser(User user, double newSalary) {
        try {
            Field field = User.class.getDeclaredField("salary");
            field.setAccessible(true);

            System.out.println("Users salary before change: " + field.getDouble(user));

            field.setDouble(user, newSalary);

            System.out.println("Users salary after change: " + field.getDouble(user));
            System.out.println();

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static double getParallellyMeanNumberOfAnswersInQuestionnaires(List<Questionnaire> questionnaireList){
        double meanValue = questionnaireList
                .parallelStream()
                .map(Questionnaire::getQuestions)
                .flatMap(Collection::stream)
                .map(Question::getPossibleAnswers)
                .map(Collection::size)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(Double.NaN);
        System.out.println("The average value of all possible answers in Questionnaires is: " +
                "\r\n" + meanValue + "\r\n");

        return meanValue;
    }
    public static void checkParallellyIfDateOfHolidayIsCorrect(List<ApplicationForHolidays> applicationForHolidaysList){
        applicationForHolidaysList
                .parallelStream()
                .filter(e->!e.getSince().before(e.getTo()))
                .map(e -> "User: " + e.getUserWhoRequestAboutHolidays().getName() +
                        " " + e.getUserWhoRequestAboutHolidays().getSurname() +
                        ", with login: " +  e.getUserWhoRequestAboutHolidays().getLogin() +
                        ", has got replaced the start and the end of the holiday in the application: " +
                        "\r\nStart: " + e.getSince() + ", End: " + e.getTo() + "\r\n")
                .forEach(System.out::println);
    }
    public static void measureTimeOfParallelAndNotParallelTasks(List<Questionnaire> questionnaireList,
                                                                List<ApplicationForHolidays> applicationForHolidaysList){
        long start;
        long end;

        start = System.nanoTime();
        DataProcessing.getMeanNumberOfAnswersInQuestionnaires(questionnaireList);
        end = System.nanoTime();
        long firstTaskNotParallelTime = end - start;

        start = System.nanoTime();
        DataProcessing.getParallellyMeanNumberOfAnswersInQuestionnaires(questionnaireList);
        end = System.nanoTime();
        long firstTaskParallelTime = end - start;

        start = System.nanoTime();
        DataProcessing.checkIfDateOfHolidayIsCorrect(applicationForHolidaysList);
        end = System.nanoTime();
        long secondTaskNotParallelTime = end - start;

        start = System.nanoTime();
        DataProcessing.checkParallellyIfDateOfHolidayIsCorrect(applicationForHolidaysList);
        end = System.nanoTime();
        long secondTaskParallelTime = end - start;

        System.out.println("\"Time measurement\"");
        System.out.println("Task 1 Not Parallel: " + firstTaskNotParallelTime);
        System.out.println("Task 1 Parallel: " + firstTaskParallelTime);

        System.out.println("Task 3 Not Parallel: " + secondTaskNotParallelTime);
        System.out.println("Task 3 Parallel: " + secondTaskParallelTime);
    }
}
