import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;

import static java.lang.Integer.parseInt;

public class Duke {
    private static ArrayList<Task> list = new ArrayList<>();
    private static final String BORDER = "_____________________________________________________________";

    private static void say(String s) {
        System.out.println(BORDER);
        System.out.println(s);
        System.out.println(BORDER);
    }

    private static void printList() throws DukeException {
        if (list.size() > 0) {
            System.out.println(BORDER);
            System.out.println("Here is your list:");
            for (int i = 1; i <= list.size(); i++) {
                System.out.println(i + "." + list.get(i - 1));
            }
            System.out.println(BORDER);
        } else {
            throw(DukeException.emptyList());
        }
    }

    private static void printDone(String input) throws DukeException {
        try {
            String pattern = "(done\\s)(.+)";
            if (input.trim().matches(pattern)) {
                String number = input.substring(5);
                int index = parseInt(number) - 1;
                list.get(index).setDone();
                say("Marked this task as done:\n" + list.get(index));
            } else {
                throw(DukeException.emptyDesc("done"));
            }
        } catch (NumberFormatException e) {
            throw(DukeException.typeMismatch("done"));
        } catch (IndexOutOfBoundsException e) {
            throw(DukeException.outOfBounds());
        }
    }

    private static void handleToDo(String input) throws DukeException {
        String pattern = "(todo\\s)(.+)";
        if (input.trim().matches(pattern)) {
            String task = input.replaceAll(pattern, "$2");
            Task next = new ToDo(task);
            list.add(next);
            String text = "Added ToDo '" + task + "' to your list!";
            say(text);
        } else {
            throw(DukeException.emptyDesc("todo"));
        }
    }

    private static void handleDeadline(String input) throws DukeException {
        String basePattern = "(deadline\\s)(.+)";
        String almostCompletePattern = "(deadline\\s)(.+)\\s(/by\\s)(.+)";
        String datePattern = "(\\d\\d\\d\\d-[01]\\d-[0123]\\d)\\s";
        String timePattern = "([012]\\d)([012345]\\d)";
        String completePattern = "(deadline\\s)(.+)\\s(/by\\s)"+ datePattern + timePattern;
        String missingTaskPattern = "(deadline\\s)(/by)((\\s(.*))*)";
        try {
            if (input.trim().matches(basePattern)) {
                if (input.trim().matches(almostCompletePattern)) {
                    if (input.trim().matches(completePattern)) {
                        String task = input.replaceAll(completePattern, "$2");
                        LocalDateTime dateTime = extractDateTime(input, completePattern);
                        if (LocalDateTime.now().isBefore(dateTime)) {
                            Task next = new Deadline(task, dateTime);
                            list.add(next);
                            String text = "Added Deadline '" + task + "' to your list!";
                            say(text);
                        } else {
                            throw(DukeException.pastDateTime());
                        }
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
        } catch (DateTimeParseException e) {
            throw(DukeException.invalidDateTime());
        }
    }

    private static void handleEvent(String input) throws DukeException {
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
                        if (LocalDateTime.now().isBefore(dateTime)) {
                            Task next = new Event(task, dateTime);
                            list.add(next);
                            String text = "Added Event '" + task + "' to your list!";
                            say(text);
                        } else {
                            throw(DukeException.pastDateTime());
                        }
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
        } catch (DateTimeParseException e) {
            throw(DukeException.invalidDateTime());
        }
    }

    private static void handleOthers() throws DukeException {
        throw(DukeException.unknownCommand());
    }

    private static void delete(String input) throws DukeException {
        try {
            String pattern = "(delete\\s)(.+)";
            if (input.trim().matches(pattern)) {
                String number = input.substring(7);
                int index = parseInt(number) - 1;
                Task task = list.get(index);
                list.remove(index);
                say("Deleted this task:\n" + task);
            } else {
                throw(DukeException.emptyDesc("delete"));
            }
        } catch (NumberFormatException e) {
            throw(DukeException.typeMismatch("delete"));
        } catch (IndexOutOfBoundsException e) {
            throw(DukeException.outOfBounds());
        }
    }

    private static LocalDateTime extractDateTime(String input, String completePattern) {
        String date = input.replaceAll(completePattern, "$4");
        String hours = input.replaceAll(completePattern, "$5");
        String minutes = input.replaceAll(completePattern, "$6");
        String time = hours + ":" + minutes;
        return LocalDateTime.parse(date + "T" + time);
    }

    private static void handleDueIn(String input) throws DukeException {
        try {
            String basePattern = "(due in\\s)(.+)";
            String hourPattern = "(due in\\s)(\\d+)\\s(hours)";
            String dayPattern = "(due in\\s)(\\d+)\\s(days)";
            if (input.trim().matches(basePattern)) {
                if (input.trim().matches(hourPattern)) {
                    long hours = parseInt(input.replaceAll(hourPattern, "$2"));
                    int i = 0;
                    System.out.println(BORDER);
                    System.out.println("These tasks are due in " + hours + " hours:");
                    for (Task t : list) {
                        if (t.compareTime(LocalDateTime.now(), hours)) {
                            i++;
                            System.out.println(i + "." + t);
                        }
                    }
                    System.out.println("Count: " + i);
                    System.out.println(BORDER);
                } else if (input.trim().matches(dayPattern)) {
                    long days = parseInt(input.replaceAll(dayPattern, "$2"));
                    int i = 0;
                    System.out.println(BORDER);
                    System.out.println("These tasks are due in " + days + " days:");
                    for (Task t : list) {
                        if (t.compareTime(LocalDateTime.now(), days * 24)) {
                            i++;
                            System.out.println(i + "." + t);
                        }
                    }
                    System.out.println("Count: " + i);
                    System.out.println(BORDER);
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

    public static void printHelp() {
        String help = "These are the available commands:\n" +
                "bye - exits the program\n" +
                "deadline <description> /by <due date and time in YYYY-MM-DD HHMM> - " +
                "adds a deadline with the given description and due date to the task list\n" +
                "delete <task number> - deletes the task corresponding to the number from the task list\n" +
                "done <task number> - marks the task corresponding to the number as done\n" +
                "due in <number> hours - shows the tasks in the tasks list that are due in the given number of hours\n" +
                "due in <number> days - shows the tasks in the tasks list that are due in the given number of days\n" +
                "event <description> /at <due date and time in YYYY-MM-DD HHMM> - " +
                "adds an event with the given description and due date to the task list\n" +
                "help - shows this list of commands\n" +
                "list - shows the contents of the task list\n" +
                "todo <description> - adds a todo task with the given description to the task list";
        say(help);
    }

    private static void createTask(String s) {
        Task next;
        if (s.startsWith("todo")) {
            next = new ToDo(s.substring(5));
        } else if (s.startsWith("deadline")) {
            String datePattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d)\\s";
            String timePattern = "(\\d\\d)(\\d\\d)";
            String pattern = "(dadeline\\s)(.+)\\s(/by\\s)"+ datePattern + timePattern;
            String task = s.replaceAll(pattern, "$2");
            LocalDateTime dateTime = extractDateTime(s, pattern);
            next = new Deadline(task, dateTime);
        } else {
            String datePattern = "(\\d\\d\\d\\d-\\d\\d-\\d\\d)\\s";
            String timePattern = "(\\d\\d)(\\d\\d)";
            String pattern = "(event\\s)(.+)\\s(/at\\s)"+ datePattern + timePattern;
            String task = s.replaceAll(pattern, "$2");
            LocalDateTime dateTime = extractDateTime(s, pattern);
            next = new Event(task, dateTime);
        }
        list.add(next);
    }

    private static void loadList() {
        try {
            File f = new File("./data/list.txt");
            Scanner listScanner = new Scanner(f);
            int index = 0;
            while (listScanner.hasNext()) {
                String command = listScanner.nextLine();
                if (command.startsWith("done")) {
                    createTask(command.substring(5));
                    list.get(index).setDone();
                } else {
                    createTask(command);
                }
                index++;
            }
            say("List has been loaded.");
        } catch (FileNotFoundException e) {
            say("No saved data found.");
        }

    }

    private static void saveList() {
        try {
            File saveFile = new File("./data/list.txt");
            File parent_directory = saveFile.getParentFile();

            if (null != parent_directory) {
                parent_directory.mkdirs();
            }

            FileWriter fw = new FileWriter(saveFile);
            int len = list.size();
            if (len > 0) {
                String text = list.get(0).toCommand();
                for (int i = 1; i < len; i++) {
                    Task t = list.get(i);
                    text = text + "\n" + t.toCommand();
                }
                fw.write(text);
            } else {
                fw.write("");
            }
            fw.close();
        } catch (IOException e) {
            say("Something went wrong: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadList();
        String logo =
                "█████████████████████████████████████████████████████████████\n" +
                "█░░░░░░░░░░░░░░███░░░░░░░░░░░░░░░░░░░░░░░░░█░░░░░░░░░░░░░░███\n" +
                "█░░▄▀▄▀▄▀▄▀▄▀░░███░░▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄░░█░░▄▀▄▀▄▀▄▀▄▀░░███\n" +
                "█░░▄▀░░░░░░▄▀░░███░░▄▀░░░░░░░░░░░░░░░░░▄▀░░█░░▄▀░░░░░░▄▀░░███\n" +
                "█░░▄▀░░██░░▄▀░░███░░▄▀░░█████████████░░▄▀░░█░░▄▀░░██░░▄▀░░███\n" +
                "█░░▄▀░░░░░░▄▀░░░░█░░▄▀░░█████████████░░▄▀░░█░░▄▀░░░░░░▄▀░░░░█\n" +
                "█░░▄▀▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░█░░█░███░█░░█░░▄▀░░█░░▄▀▄▀▄▀▄▀▄▀▄▀░░█\n" +
                "█░░▄▀░░░░░░░░▄▀░░█░░▄▀░░█████░░░█████░░▄▀░░█░░▄▀░░░░░░░░▄▀░░█\n" +
                "█░░▄▀░░████░░▄▀░░█░░▄▀░░█████████████░░▄▀░░█░░▄▀░░████░░▄▀░░█\n" +
                "█░░▄▀░░░░░░░░▄▀░░█░░▄▀░░░░░░░░░░░░░░░░░▄▀░░█░░▄▀░░░░░░░░▄▀░░█\n" +
                "█░░▄▀▄▀▄▀▄▀▄▀▄▀░░█░░▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄▀▄░░█░░▄▀▄▀▄▀▄▀▄▀▄▀░░█\n" +
                "█░░░░░░░░░░░░░░░░█░░░░░░░░░░░░░░░░░░░░░░░░░█░░░░░░░░░░░░░░░░█\n" +
                "█████████████████████████████████████████████████████████████";
        String welcome = "Hello I am\n" + logo +"\nPlease say something.";
        say(welcome);
        String input = sc.nextLine();
        while (!input.equals("bye")) {
            try {
                if (input.trim().equals("help")) {
                    printHelp();
                } else if (input.trim().equals("list")) {
                    printList();
                } else if (input.startsWith("done")) {
                    printDone(input);
                } else if (input.startsWith("todo")) {
                    handleToDo(input);
                } else if (input.startsWith("deadline")) {
                    handleDeadline(input);
                } else if (input.startsWith("event")) {
                    handleEvent(input);
                } else if (input.startsWith("delete")) {
                    delete(input);
                } else if (input.startsWith("due in")) {
                    handleDueIn(input);
                } else {
                    handleOthers();
                }
            } catch (DukeException e) {
                say(e.getMessage());
            }
            input = sc.nextLine();
        }
        saveList();
        say("Goodbye!");
        sc.close();
    }
}
