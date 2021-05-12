import pl.blueenergy.document.ApplicationForHolidays;
import pl.blueenergy.document.DocumentDao;
import pl.blueenergy.document.Questionnaire;
import pl.blueenergy.organization.User;

import java.util.List;

public class ProgrammerService {

	public void execute(DocumentDao documentDao) {
//		Miejsce na twój kod:

		System.out.println();

//		Split lists of documents to separate lists with applications for holidays and questionnaires

		List<ApplicationForHolidays> applicationForHolidaysList = DataProcessing
				.getElementsOf(documentDao, ApplicationForHolidays.class);
		List<Questionnaire> questionnaireList = DataProcessing
				.getElementsOf(documentDao, Questionnaire.class);

//		Count the mean number of answers for all questions in questionnaires

		System.out.println("Task 1:");

		double meanValue = DataProcessing.getMeanNumberOfAnswersInQuestionnaires(questionnaireList);

//		Get the list with users who go on Holiday and check if their names have polish letters with diacritics

		System.out.println("Task 2:");

		List<User> listWithUsersWhoGoOnHoliday = DataProcessing.getListWithUsersWhoGoOnHoliday(applicationForHolidaysList);

//		Check if any application for a holiday has replaced beginning and end date of holiday

		System.out.println("Task 3:");

		DataProcessing.checkIfDateOfHolidayIsCorrect(applicationForHolidaysList);

//		Saving Questionnair to the text file

		System.out.println("Task 4:");

		DataProcessing.saveQuestionnairToTextFileHelper(questionnaireList.get(0), questionnaireList.get(0).getTitle());

		DataProcessing.saveQuestionnairToTextFileHelper(questionnaireList.get(1), questionnaireList.get(1).getTitle());

//		Set salary in any User object by reflection

		System.out.println("Task 5:");

//			Example:

		User exampleUser = new User();
		DataProcessing.setSalaryOfUser(exampleUser, 20);

//     		Make 2 of previous tasks parallel

		System.out.println("Task 6:");

//			Task 1:

		double meanValue2 = DataProcessing.getParallellyMeanNumberOfAnswersInQuestionnaires(questionnaireList);

//			Task 3:

		DataProcessing.checkParallellyIfDateOfHolidayIsCorrect(applicationForHolidaysList);

//		Measure time of last task and related ones

		System.out.println("Task 7:");

		DataProcessing.measureTimeOfParallelAndNotParallelTasks(questionnaireList, applicationForHolidaysList);
	}
}
