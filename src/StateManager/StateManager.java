package StateManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class StateManager {
    private static StateManager stateManager = null;

    private States state;

    private StateManager() {
        this.state = States.NOT_LOGGED;
    }

    public static StateManager getInstance() {
        if (stateManager == null) {
            stateManager = new StateManager();
        }

        return stateManager;
    }

    public void loop() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        while (true) {
            if (this.state == States.EXITED) {
                return;
            }

            this.state = this.state.performTask(this.state.getTask());
        }
    }
}
