package com.wlu.examscheduler.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.wlu.examscheduler.exammodel.Exam;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private List<Exam> exams = new ArrayList<>();

    @PostConstruct
    public void loadExams() {
        try (CSVReader reader = new CSVReader(new FileReader("students.wlu.ca-Spring 2025 Waterloo Final Examination Schedule.csv"))) {
            List<String[]> rows = reader.readAll();
            String[] currentRow = null;
            String[] nextRow = null;
            
            // Skip header rows
            int startIndex = 0;
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i)[0] != null && rows.get(i)[0].equals("Course")) {
                    startIndex = i + 1;
                    break;
                }
            }
            
            for (int i = startIndex; i < rows.size() - 1; i++) {
                currentRow = rows.get(i);
                nextRow = rows.get(i + 1);
                
                // Skip empty rows and non-course rows
                if (currentRow.length < 5 || currentRow[0] == null || currentRow[0].trim().isEmpty() ||
                    !currentRow[0].matches("^[A-Z]{2}\\d{3}[A-Z]?$")) {
                    continue;
                }
                
                Exam exam = createExamFromRow(currentRow, nextRow);
                if (exam != null && exam.getDate() != null && !exam.getDate().isEmpty()) {
                    exams.add(exam);
                }
            }
            
            System.out.println("Loaded " + exams.size() + " exams from CSV");
            
        } catch (IOException | CsvException e) {
            System.err.println("Error loading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Exam createExamFromRow(String[] currentRow, String[] nextRow) {
        try {
            String course = currentRow[0].trim();
            String section = currentRow[1] != null ? currentRow[1].trim() : "";
            
            // Initialize date components
            String dateStr = "";
            String timeStr = "";
            String roomsStr = "";
            List<String> roomParts = new ArrayList<>();
            
            // First, try to get date from the current row
            for (int i = 2; i < currentRow.length; i++) {
                if (currentRow[i] != null && !currentRow[i].trim().isEmpty()) {
                    String value = currentRow[i].trim();
                    if (value.contains("day,")) {
                        dateStr = value;
                        if (value.contains("Aug.")) {
                            dateStr = value.replace("Aug.", "Aug");
                        }
                    } else if (value.contains(":") || value.equals("Noon")) {
                        timeStr = value;
                    } else if (value.contains("p.m.") || value.contains("a.m.")) {
                        if (timeStr.isEmpty()) {
                            timeStr = value;
                        } else {
                            timeStr = timeStr + " " + value;
                        }
                    } else if (isRoomToken(value)) {
                        if (roomParts.isEmpty()) {
                            roomParts.add(value);
                        } else if (!roomParts.get(roomParts.size() - 1).equals(value)) {
                            roomParts.add(value);
                        }
                    }
                }
            }
            
            // Then check the next row for additional date and time info
            if (nextRow != null) {
                for (int i = 0; i < nextRow.length; i++) {
                    if (nextRow[i] != null && !nextRow[i].trim().isEmpty()) {
                        String value = nextRow[i].trim();
                        // Look for date components
                        if (value.matches("\\d+")) {
                            dateStr = dateStr + " " + value;
                        } else if (value.contains("Aug.")) {
                            if (dateStr.isEmpty()) {
                                dateStr = value;
                            } else {
                                dateStr = dateStr + " " + value.replace("Aug.", "Aug");
                            }
                        } else if (value.contains("p.m.") || value.contains("a.m.")) {
                            if (timeStr.isEmpty()) {
                                timeStr = value;
                            } else {
                                timeStr = timeStr + " " + value;
                            }
                        } else if (isRoomToken(value)) {
                            if (roomParts.isEmpty() || !roomParts.get(roomParts.size() - 1).equals(value)) {
                                roomParts.add(value);
                            }
                        }
                    }
                }
            }
            
            // Join collected room tokens, ensure combinations like "PET P110A/110B"
            if (!roomParts.isEmpty()) {
                roomsStr = String.join(" ", roomParts);
            }

            // If section continues on the next line (e.g., names A - L), append it
            if (nextRow != null && nextRow.length > 1) {
                String nextSection = nextRow[1] != null ? nextRow[1].trim() : "";
                if (!nextSection.isEmpty() && !isDateOrTimeToken(nextSection) && !isRoomToken(nextSection)) {
                    if (!section.isEmpty()) {
                        section = section + " " + nextSection;
                    } else {
                        section = nextSection;
                    }
                }
            }

            // Combine time components if they're split
            if (timeStr.contains(":") && !timeStr.contains("a.m.") && !timeStr.contains("p.m.")) {
                // Look for a.m./p.m. in the next row
                if (nextRow != null) {
                    for (String value : nextRow) {
                        if (value != null && (value.trim().equals("a.m.") || value.trim().equals("p.m."))) {
                            timeStr = timeStr + " " + value.trim();
                            break;
                        }
                    }
                }
            }
            
            // Clean up the date string
            if (dateStr.contains("Aug") && !dateStr.endsWith("Aug")) {
                dateStr = dateStr.replace("Aug", "Aug.");
            }
            
            // Clean up the time string - remove trailing periods
            if (timeStr.endsWith(".")) {
                timeStr = timeStr.substring(0, timeStr.length() - 1);
            }
            
            return new Exam(course, section, dateStr, timeStr, roomsStr);
        } catch (Exception e) {
            System.err.println("Error creating exam from row: " + String.join(",", currentRow));
            return null;
        }
    }

    private boolean isDateOrTimeToken(String value) {
        String v = value.trim();
        if (v.isEmpty()) return false;
        if (v.contains("day,") || v.contains("Aug")) return true;
        if (v.equals("Noon") || v.contains("a.m.") || v.contains("p.m.")) return true;
        if (v.matches("\\d+")) return true;
        return false;
    }

    private boolean isRoomToken(String value) {
        String v = value.trim();
        if (v.isEmpty()) return false;
        if (v.equals("Remote")) return true;
        if (v.startsWith("LH") || v.startsWith("SB") || v.startsWith("PET")) return true;
        // Patterns like P110A/110B or PET P110A/110B second token
        if (v.matches("[A-Z]?[A-Z]?\\d{2,}[A-Z]?(/[A-Z]?[A-Z]?\\d+[A-Z]?)+")) return true;
        if (v.matches("[A-Z]{1,3}\\d{3,}[A-Z]?$")) return true;
        // Avoid picking up date/time tokens
        if (isDateOrTimeToken(v)) return false;
        return false;
    }

    public List<Exam> getAllExams() {
        return new ArrayList<>(exams);
    }

    public List<Exam> searchByCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchCode = courseCode.trim().toUpperCase();
        return exams.stream()
                .filter(exam -> exam.getCourse().toUpperCase().contains(searchCode))
                .collect(Collectors.toList());
    }

    public List<Exam> searchByCourseCodeExact(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchCode = courseCode.trim().toUpperCase();
        return exams.stream()
                .filter(exam -> exam.getCourse().toUpperCase().equals(searchCode))
                .collect(Collectors.toList());
    }
}
