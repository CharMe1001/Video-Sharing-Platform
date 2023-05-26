package statemanager;

public class StateManager {
    private static StateManager stateManager = null;

    // The current state of the program.
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

    // The main program loop.
    public void loop() {
        while (true) {
            if (this.state == States.EXITED) {
                return;
            }

            this.state = this.state.performTask(this.state.getTask());
        }
    }
}
