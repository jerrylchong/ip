package duke;

import java.util.Scanner;

public class Ui {
    private static final String BORDER = "_____________________________________________________________";
    private Scanner sc;

    public Ui() {
        this.sc = new Scanner(System.in);
    }

    public void say(String s) {
        System.out.println(BORDER);
        System.out.println(s);
        System.out.println(BORDER);
    }

    public void showLoadingError() {
        say("Unable to load tasks.");
    }

    public void showWelcome() {
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
        String welcome = "Hello I am\n" + logo + "\nPlease say something.";
        say(welcome);
    }

    public String readCommand() {
        String input = sc.nextLine();
        return input;
    }

    public void end() {
        say("Goodbye!");
        sc.close();
    }

}
