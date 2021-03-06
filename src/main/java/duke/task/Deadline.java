package duke.task;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class Deadline extends Task {
    private LocalDateTime dateTime;
    private String time;

    /**
     * Creates a Deadline task with the given description and due date.
     *
     * @param s The description of the Deadline task.
     * @param dateTime The due date of the Deadline task.
     */
    public Deadline(String s, LocalDateTime dateTime) {
        super(s);
        this.dateTime = dateTime;
        String minute = dateTime.getMinute() < 10
                ? "0" + dateTime.getMinute()
                : String.format("%d", dateTime.getMinute());
        String hour = dateTime.getHour() < 10
                ? "0" + dateTime.getHour()
                : String.format("%d", dateTime.getHour());
        this.time = String.format("%s:%s", hour, minute);
    }

    @Override
    public String toString() {
        String month = dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int day = dateTime.getDayOfMonth();
        int year = dateTime.getYear();
        String dueDate = String.format("%s %d %d, %s", month, day, year, time);
        return done
                ? "[D][O] " + text + " (by: " + dueDate + ")"
                : "[D][X] " + text + " (by: " + dueDate + ")";
    }

    @Override
    public boolean compareTime(LocalDateTime givenDateTime, long hours) {
        return givenDateTime.plusHours(hours).isAfter(dateTime);
    }

    @Override
    public String toCommand() {
        String rawDateTime = dateTime.toString();
        String pattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d)T(\\d\\d):(\\d\\d)";
        String date = rawDateTime.replaceAll(pattern, "$1");
        String hours = rawDateTime.replaceAll(pattern, "$2");
        String minutes = rawDateTime.replaceAll(pattern, "$3");
        String newDateTime = date + " " + hours + minutes;
        return done
                ? "done deadline " + text + " /by " + newDateTime
                : "deadline " + text + " /by " + newDateTime;
    }

    @Override
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.DEADLINE;
    }
}
