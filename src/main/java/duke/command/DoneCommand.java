package duke.command;

import duke.DukeException;
import duke.Storage;
import duke.TaskList;
import duke.Ui;

public class DoneCommand extends Command {
    private int index;

    public DoneCommand(int index) {
        this.index = index;
    }

    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        String task = tasks.done(index);
        ui.say("Marked this task as done:\n" + task);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DoneCommand;
    }
}
