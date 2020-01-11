package InviteRewards.Main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CommandUtility {

    private boolean isExecutingDependentCommand;

    public CommandUtility() {
        this.isExecutingDependentCommand = false;
    }

    public boolean isExecutingDependentCommand() {
        return isExecutingDependentCommand;
    }

    public void setRunning() {
        this.isExecutingDependentCommand = true;
    }

    public void setFinished() {
        this.isExecutingDependentCommand = false;
    }

    public String getDependencyMessage() {
        return "Please wait before using this command";
    }

}
