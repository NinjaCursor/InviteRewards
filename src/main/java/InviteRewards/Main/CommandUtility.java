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
}
