public class Promise {
    private boolean accepted;

    public Promise(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }
}