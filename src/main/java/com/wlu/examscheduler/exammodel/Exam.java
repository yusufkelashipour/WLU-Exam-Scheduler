package com.wlu.examscheduler.exammodel; 

import com.wlu.examscheduler.util.DateUtil;

public class Exam {
    private String course;
    private String section;
    private String date;
    private String time;
    private String rooms;

    public Exam() {}

    public Exam(String course, String section, String date, String time, String rooms) {
        this.course = course;
        this.section = section;
        this.date = date;
        this.time = time;
        this.rooms = rooms;
    }

    // Getters and Setters
    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }
    
    // Calendar utility methods
    public String getCalendarStartTime() {
        return DateUtil.parseExamDate(this.date, this.time);
    }
    
    public String getCalendarEndTime() {
        String startTime = DateUtil.parseExamDate(this.date, this.time);
        if (startTime == null) return null;
        
        // Add 3 hours for exam duration
        String endTime = startTime.substring(0, 8) + DateUtil.getEndTime(this.time) + "00Z";
        return endTime;
    }
    
    public String getFormattedDate() {
        if (this.date == null || this.date.trim().isEmpty()) {
            return "";
        }
        return this.date.replace("Aug.", "August");
    }

    @Override
    public String toString() {
        return "Exam{" +
                "course='" + course + '\'' +
                ", section='" + section + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", rooms='" + rooms + '\'' +
                '}';
    }
}
