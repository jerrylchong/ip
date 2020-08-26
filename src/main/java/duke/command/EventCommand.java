package duke.command;

import duke.DukeException;
import duke.Storage;
import duke.TaskList;
import duke.Ui;
import duke.TaskType;

import java.time.LocalDateTime;

public class EventCommand extends Command {
    private String task;
    private LocalDateTime dateTime;

    public EventCommand(String task, LocalDateTime dateTime) {
        this.task = task;
        this.dateTime = dateTime;
    }

    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (LocalDateTime.now().isBefore(dateTime)) {
            tasks.add(task, dateTime, TaskType.EVENT);
            ui.say("Added Event '" + task + "' to your list!");
        } else {
            throw(DukeException.pastDateTime());
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EventCommand;
    }
}