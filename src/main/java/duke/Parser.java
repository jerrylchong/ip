package duke;

import duke.command.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static java.lang.Integer.parseInt;

public class Parser {

    private static Command parseDone(String input) throws DukeException {
        try {
            String pattern = "(done\\s)(.+)";
            if (input.trim().matches(pattern)) {
                String number = input.substring(5);
                int index = parseInt(number) - 1;
                return new DoneCommand(index);
            } else {
                throw(DukeException.emptyDesc("done"));
            }
        } catch (NumberFormatException e) {
            throw(DukeException.typeMismatch("done"));
        } catch (DukeException e) {
            throw(e);
        }
    }

    private static Command handleToDo(String input) throws DukeException {
        String pattern = "(todo\\s)(.+)";
        if (input.trim().matches(pattern)) {
            String task = input.replaceAll(pattern, "$2");
            return new ToDoCommand(task);
        } else {
            throw(DukeException.emptyDesc("todo"));
        }
    }

    private static Command handleDeadline(String input) throws DukeException {
        String basePattern = "(deadline\\s)(.+)";
        String almostCompletePattern = "(deadline\\s)(.+)\\s(/by\\s)(.+)";
        String datePattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d)\\s";
        String timePattern = "(\\d\\d)(\\d\\d)";
        String completePattern = "(deadline\\s)(.+)\\s(/by\\s)"+ datePattern + timePattern;
        String missingTaskPattern = "(deadline\\s)(/by)((\\s(.*))*)";
        try {
            if (input.trim().matches(basePattern)) {
                if (input.trim().matches(almostCompletePattern)) {
                    if (input.trim().matches(completePattern)) {
                        String task = input.replaceAll(completePattern, "$2");
                        LocalDateTime dateTime = extractDateTime(input, completePattern);
                        return new DeadlineCommand(task, dateTime);
                    } else {
                        throw(DukeException.wrongDateTimeFormat());
                    }
                } else if (input.trim().matches(missingTaskPattern)) {
                    throw(DukeException.missingTask());
                } else {
                    throw(DukeException.missingTime("by"));
                }
            } else {
                throw(DukeException.emptyDesc("deadline"));
            }
        } catch (DukeException e) {
            throw(e);
        }
    }

    private static Command handleEvent(String input) throws DukeException {
        String basePattern = "(event\\s)(.+)";
        String almostCompletePattern = "(event\\s)(.+)\\s(/at\\s)(.+)";
        String datePattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d)\\s";
        String timePattern = "(\\d\\d)(\\d\\d)";
        String completePattern = "(event\\s)(.+)\\s(/at\\s)"+ datePattern + timePattern;
        String missingTaskPattern = "(event\\s)(/at)((\\s(.*))*)";
        try {
            if (input.trim().matches(basePattern)) {
                if (input.trim().matches(almostCompletePattern)) {
                    if (input.trim().matches(completePattern)) {
                        String task = input.replaceAll(completePattern, "$2");
                        LocalDateTime dateTime = extractDateTime(input, completePattern);
                        return new EventCommand(task, dateTime);
                    } else {
                        throw(DukeException.wrongDateTimeFormat());
                    }
                } else if (input.trim().matches(missingTaskPattern)) {
                    throw(DukeException.missingTask());
                } else {
                    throw(DukeException.missingTime("at"));
                }
            } else {
                throw(DukeException.emptyDesc("event"));
            }
        } catch (DukeException e) {
            throw(e);
        }
    }

    private static Command delete(String input) throws DukeException {
        try {
            String pattern = "(delete\\s)(.+)";
            if (input.trim().matches(pattern)) {
                String number = input.substring(7);
                int index = parseInt(number) - 1;
                return new DeleteCommand(index);
            } else {
                throw(DukeException.emptyDesc("delete"));
            }
        } catch (NumberFormatException e) {
            throw(DukeException.typeMismatch("delete"));
        } catch (IndexOutOfBoundsException e) {
            throw(DukeException.outOfBounds());
        }
    }

    /**
     * Returns the LocalDateTime extracted from the input String.
     *
     * @param input User input.
     * @param completePattern Regex pattern that follows the command format.
     * @return LocalDateTime of specified dateTime.
     * @throws DukeException If dateTime in input is invalid.
     */
    public static LocalDateTime extractDateTime(String input, String completePattern) throws DukeException {
        try {
            String date = input.replaceAll(completePattern, "$4");
            String hours = input.replaceAll(completePattern, "$5");
            String minutes = input.replaceAll(completePattern, "$6");
            String time = hours + ":" + minutes;
            return LocalDateTime.parse(date + "T" + time);
        } catch (DateTimeParseException e) {
            throw(DukeException.invalidDateTime());
        }
    }

    private static Command handleDueIn(String input) throws DukeException {
        try {
            String basePattern = "(due in\\s)(.+)";
            String hourPattern = "(due in\\s)(\\d+)\\s(hours)";
            String dayPattern = "(due in\\s)(\\d+)\\s(days)";
            if (input.trim().matches(basePattern)) {
                if (input.trim().matches(hourPattern)) {
                    long time = parseInt(input.replaceAll(hourPattern, "$2"));
                    return new DueInCommand(time, true);
                } else if (input.trim().matches(dayPattern)) {
                    long time = parseInt(input.replaceAll(dayPattern, "$2"));
                    return new DueInCommand(time, false);
                } else {
                    throw(DukeException.wrongDueInFormat());
                }
            } else {
                throw(DukeException.emptyDesc("due in"));
            }
        } catch (NumberFormatException e) {
            throw(DukeException.typeMismatch("due in"));
        } catch (IndexOutOfBoundsException e) {
            throw(DukeException.outOfBounds());
        }
    }

    /**
     * Returns a command to respond to the user input.
     *
     * @param input User input.
     * @return Command responding to the input.
     * @throws DukeException If user input is not recognized as a command.
     */
    public static Command parse(String input) throws DukeException {
        if (input.trim().equals("help")) {
            return new HelpCommand();
        } else if (input.trim().equals("list")) {
            return new ListCommand();
        } else if (input.startsWith("done")) {
            return parseDone(input);
        } else if (input.startsWith("todo")) {
            return handleToDo(input);
        } else if (input.startsWith("deadline")) {
            return handleDeadline(input);
        } else if (input.startsWith("event")) {
            return handleEvent(input);
        } else if (input.startsWith("delete")) {
            return delete(input);
        } else if (input.startsWith("due in")) {
            return handleDueIn(input);
        } else if (input.equals("bye")) {
            return new ByeCommand();
        } else {
            throw(DukeException.unknownCommand());
        }
    }
}
