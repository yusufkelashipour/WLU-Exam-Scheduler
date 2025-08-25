# WLU Exam Scheduler

A Spring Boot web application that allows students to search for their final exam schedules and add them to their calendar.

## Features

- Search for exams by course code (e.g., BU111, MA101)
- View all exams in a table format
- Add exams to Google Calendar, Outlook, or Apple Calendar
- Responsive design for mobile and desktop
- Clean, modern UI

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Setup

1. **Clone or download the project files**

2. **Ensure the CSV file is in the project root**
   - The file `students.wlu.ca-Spring 2025 Waterloo Final Examination Schedule.csv` should be in the same directory as `pom.xml`

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Open your browser and go to `http://localhost:8080`

## Usage

### Searching for Exams
1. Enter a course code in the search box (e.g., BU111, MA101, CP104)
2. Click "Search Exams" or press Enter
3. View the exam details including date, time, and room
4. Click on calendar buttons to add the exam to your preferred calendar

### Viewing All Exams
1. Click "View All Exams" from the home page
2. Browse through all available exams in a table format
3. Use the calendar buttons to add any exam to your calendar

### Calendar Integration
The application provides three calendar options:
- **Google Calendar**: Opens Google Calendar with pre-filled exam details
- **Outlook**: Opens Outlook Calendar with pre-filled exam details  
- **Apple Calendar**: Downloads an .ics file that can be imported into Apple Calendar

## Project Structure

```
src/
├── main/
│   ├── java/com/wlu/examscheduler/
│   │   ├── ExamSchedulerApplication.java    # Main Spring Boot application
│   │   ├── controller/
│   │   │   └── ExamController.java          # Web controller for handling requests
│   │   ├── model/
│   │   │   └── Exam.java                    # Exam data model
│   │   └── service/
│   │       └── ExamService.java             # Business logic for exam data processing
│   └── resources/
│       ├── templates/                       # Thymeleaf HTML templates
│       │   ├── index.html                   # Home page
│       │   ├── search.html                  # Search results page
│       │   └── all.html                     # All exams page
│       └── application.properties           # Spring Boot configuration
├── pom.xml                                  # Maven dependencies and build configuration
└── README.md                                # This file
```

## Technical Details

- **Framework**: Spring Boot 3.2.0
- **Template Engine**: Thymeleaf
- **CSV Processing**: OpenCSV
- **Java Version**: 17
- **Build Tool**: Maven

## Customization

### Adding New Calendar Providers
To add support for additional calendar providers, modify the calendar links in the HTML templates (`search.html` and `all.html`).

### Styling
The application uses plain CSS for styling. You can modify the styles in the `<style>` sections of each HTML template.

### Data Source
The application reads from the CSV file located in the project root. To use a different data source, modify the `ExamService.loadExams()` method.

## Troubleshooting

### CSV File Not Found
- Ensure the CSV file is in the project root directory
- Check that the filename matches exactly: `students.wlu.ca-Spring 2025 Waterloo Final Examination Schedule.csv`

### Port Already in Use
- Change the port in `application.properties`: `server.port=8081`

### Build Errors
- Ensure you have Java 17+ installed: `java -version`
- Clean and rebuild: `mvn clean install`

## License

This project is for educational purposes.
