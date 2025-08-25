package com.wlu.examscheduler.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
    
    private static final Map<String, Integer> monthMap = new HashMap<>();
    private static final Map<String, Integer> dayMap = new HashMap<>();
    
    static {
        // Month mapping
        monthMap.put("Jan", 1);
        monthMap.put("Feb", 2);
        monthMap.put("Mar", 3);
        monthMap.put("Apr", 4);
        monthMap.put("May", 5);
        monthMap.put("Jun", 6);
        monthMap.put("Jul", 7);
        monthMap.put("Aug", 8);
        monthMap.put("Sep", 9);
        monthMap.put("Oct", 10);
        monthMap.put("Nov", 11);
        monthMap.put("Dec", 12);
        
        // Day mapping
        dayMap.put("Monday", 1);
        dayMap.put("Tuesday", 2);
        dayMap.put("Wednesday", 3);
        dayMap.put("Thursday", 4);
        dayMap.put("Friday", 5);
        dayMap.put("Saturday", 6);
        dayMap.put("Sunday", 7);
    }
    
    public static String parseExamDate(String dateStr, String timeStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Parse date like "Friday, Aug. 8" or "Wednesday, Aug. 06"
            String[] parts = dateStr.split(",");
            if (parts.length < 2) return null;
            
            String monthDayPart = parts[1].trim();
            String[] monthDay = monthDayPart.split("\\.");
            if (monthDay.length < 2) return null;
            
            String month = monthDay[0].trim();
            String day = monthDay[1].trim();
            
            Integer monthNum = monthMap.get(month);
            Integer dayNum = Integer.parseInt(day);
            
            if (monthNum == null || dayNum == null) return null;
            
            // Default to 2025 for the exam year
            LocalDate examDate = LocalDate.of(2025, monthNum, dayNum);
            
            // Parse time
            LocalTime examTime = parseTime(timeStr);
            if (examTime == null) {
                examTime = LocalTime.of(8, 0); // Default to 8 AM if time parsing fails
            }
            
            LocalDateTime examDateTime = LocalDateTime.of(examDate, examTime);
            
            // Format for calendar links (ISO format)
            return examDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            timeStr = timeStr.trim();
            
            if (timeStr.equals("Noon")) {
                return LocalTime.of(12, 0);
            }
            
            if (timeStr.contains("p.m")) {
                String timePart = timeStr.replace("p.m", "").trim();
                if (timePart.contains(":")) {
                    String[] parts = timePart.split(":");
                    int hour = Integer.parseInt(parts[0].trim());
                    int minute = Integer.parseInt(parts[1].trim());
                    return LocalTime.of(hour + 12, minute);
                } else {
                    int hour = Integer.parseInt(timePart);
                    return LocalTime.of(hour + 12, 0);
                }
            }
            
            if (timeStr.contains("a.m")) {
                String timePart = timeStr.replace("a.m", "").trim();
                if (timePart.contains(":")) {
                    String[] parts = timePart.split(":");
                    int hour = Integer.parseInt(parts[0].trim());
                    int minute = Integer.parseInt(parts[1].trim());
                    return LocalTime.of(hour, minute);
                } else {
                    int hour = Integer.parseInt(timePart);
                    return LocalTime.of(hour, 0);
                }
            }
            
            // Try to parse as regular time format
            if (timeStr.contains(":")) {
                String[] parts = timeStr.split(":");
                int hour = Integer.parseInt(parts[0].trim());
                int minute = Integer.parseInt(parts[1].trim());
                return LocalTime.of(hour, minute);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getEndTime(String startTimeStr) {
        LocalTime startTime = parseTime(startTimeStr);
        if (startTime == null) {
            startTime = LocalTime.of(8, 0);
        }
        
        // Add 3 hours for exam duration
        LocalTime endTime = startTime.plusHours(3);
        return endTime.format(DateTimeFormatter.ofPattern("HHmm"));
    }
}
