package StateManager;

public class StateManager {
    private States state;

    public StateManager() {
        this.state = States.NOT_LOGGED;
    }

    public void loop() {
        while (true) {
            if (this.state == States.EXITED) {
                return;
            }

            this.state = this.state.performTask(this.state.getTask());
        }
    }
}
