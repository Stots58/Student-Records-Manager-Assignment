import java.io.*;
import java.util.*;

/**
 * Main class that manages student records.
 * This class demonstrates file I/O and exception handling in Java.
 */
public class StudentRecordsManager {

    public static void main(String[] args) {
        StudentRecordsManager manager = new StudentRecordsManager();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter input filename: ");
        String inputFile = scanner.nextLine();

        System.out.print("Enter output filename: ");
        String outputFile = scanner.nextLine();

        try {
            manager.processStudentRecords(inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public void processStudentRecords(String inputFile, String outputFile) {
        try {
            List<Student> students = readStudentRecords(inputFile);
            writeResultsToFile(students, outputFile);
            System.out.println("Processing complete. Results written to " + outputFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + inputFile + ". Check the file path and name.");
        } catch (IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        }
    }

    public List<Student> readStudentRecords(String filename) throws IOException {
        List<Student> students = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(",");

                    if (parts.length < 6) {
                        throw new ArrayIndexOutOfBoundsException("Line " + lineNumber + ": Not enough fields.");
                    }

                    String studentId = parts[0].trim();
                    String name = parts[1].trim();
                    int[] grades = new int[4];

                    for (int i = 0; i < 4; i++) {
                        grades[i] = Integer.parseInt(parts[i + 2].trim());
                        if (grades[i] < 0 || grades[i] > 100) {
                            throw new InvalidGradeException("Grade must be between 0 and 100.");
                        }
                    }

                    students.add(new Student(studentId, name, grades));
                } catch (NumberFormatException e) {
                    System.err.println("Line " + lineNumber + ": Invalid number format. " + e.getMessage());
                } catch (InvalidGradeException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return students;
    }

    public void writeResultsToFile(List<Student> students, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== Student Report ===");
            writer.println("-----------------------");

            int totalStudents = students.size();
            double totalAvg = 0;
            int aCount = 0, bCount = 0, cCount = 0, dCount = 0, fCount = 0;

            for (Student s : students) {
                double avg = s.getAverage();
                String letter = s.getLetterGrade();

                writer.printf("ID: %s | Name: %s | Average: %.2f | Grade: %s%n",
                        s.getStudentId(), s.getName(), avg, letter);

                totalAvg += avg;
                switch (letter) {
                    case "A": aCount++; break;
                    case "B": bCount++; break;
                    case "C": cCount++; break;
                    case "D": dCount++; break;
                    case "F": fCount++; break;
                }
            }

            writer.println("\n=== Class Statistics ===");
            writer.println("Total Students: " + totalStudents);
            writer.printf("Class Average: %.2f%n", totalStudents > 0 ? totalAvg / totalStudents : 0);
            writer.println("Grade Distribution:");
            writer.println("A: " + aCount);
            writer.println("B: " + bCount);
            writer.println("C: " + cCount);
            writer.println("D: " + dCount);
            writer.println("F: " + fCount);
        }
    }
}
