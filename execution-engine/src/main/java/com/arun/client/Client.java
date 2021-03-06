package com.arun.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arun.parallel.ParallelProcessor;
import com.arun.parallel.Signature;
import com.arun.student.SchoolService;
import com.arun.student.SchoolService_;
import com.arun.student.Student;
import com.arun.student.StudentService;
import com.arun.student.StudentService_;

public class Client {

	public static void serialExecution() {
		long startTime = System.nanoTime();
		StudentService service = new StudentService();
		SchoolService schoolService = new SchoolService();
		Map<String, Integer> bookSeries = new HashMap<>();
		bookSeries.put("A Song of Ice and Fire", 7);
		bookSeries.put("Wheel of Time", 14);
		bookSeries.put("Harry Potter", 7);

		Student student = service.findStudent("john@gmail.com", 11, false);
		List<Integer> marks = service.getStudentMarks(1L);
		List<Student> students = service.getStudentsByFirstNames(Arrays.asList("John","Alice"));
		String randomName = service.getRandomLastName();
		Long studentId = service.findStudentIdByName("Kate", "Williams");
		service.printMapValues(bookSeries);
		List<String> schoolNames = schoolService.getSchoolNames();

		System.out.println(student);
		System.out.println(marks);
		System.out.println(students);
		System.out.println(randomName);
		System.out.println(studentId);
		System.out.println(schoolNames);

		long executionTime = (System.nanoTime() - startTime) / 1000000;
		System.out.printf("\nTotal elapsed time is %d\n\n", executionTime);
	}
	
	public static <T> void parallelExecution() {
    	long startTime = System.nanoTime();
    	StudentService studentService = new StudentService();
		SchoolService schoolService = new SchoolService();
    	Map<Object, List<Signature>> executionMap = new HashMap<>();
    	List<Signature> studentServiceSignatures = new ArrayList<>();
		List<Signature> schoolServiceSignatures = new ArrayList<>();
		Map<String, Integer> bookSeries = new HashMap<>();
		bookSeries.put("A Song of Ice and Fire", 7);
		bookSeries.put("Wheel of Time", 14);
		bookSeries.put("Middle Earth Legendarium", 5);

		studentServiceSignatures.add(Signature.method(StudentService_.getStudentMarks)
				.returnType(List.class)
				.argsList(Arrays.asList(1L))
				.argTypes(Arrays.asList(Long.class))
				.build());

		studentServiceSignatures.add(Signature.method(StudentService_.getStudentsByFirstNames)
				.returnType(List.class)
				.argsList(Arrays.asList(Arrays.asList("John","Alice")))
				.argTypes(Arrays.asList(List.class))
				.build());

		studentServiceSignatures.add(Signature.method(StudentService_.getRandomLastName)
				.returnType(String.class)
				.build());

		studentServiceSignatures.add(Signature.method(StudentService_.findStudentIdByName)
				.returnType(Long.class)
				.argsList(Arrays.asList("Kate", "Williams"))
				.argTypes(Arrays.asList(String.class, String.class))
				.build());

		studentServiceSignatures.add(Signature.method(StudentService_.findStudent)
				.returnType(Student.class)
				.argsList(Arrays.asList("bob@gmail.com", 14, false))
				.argTypes(Arrays.asList(String.class, Integer.class, Boolean.class))
				.build());

		studentServiceSignatures.add(Signature.method(StudentService_.printMapValues)
				.returnType(void.class)
				.argsList(Arrays.asList(bookSeries))
				.argTypes(Arrays.asList(Map.class))
				.build());

		schoolServiceSignatures.add(Signature.method(SchoolService_.getSchoolNames)
				.returnType(List.class)
				.build());

		executionMap.put(studentService, studentServiceSignatures);
		executionMap.put(schoolService, schoolServiceSignatures);

		List<T> result = ParallelProcessor.genericParallelExecutor(executionMap);
		result.forEach(s -> System.out.println(s));
    	long executionTime = (System.nanoTime() - startTime) / 1000000;
    	System.out.printf("\nTotal elapsed time is %d", executionTime);
    }

	public static void main(String[] args) {
		serialExecution();
		parallelExecution();
	}
	
}
