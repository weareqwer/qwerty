public class Acceptor {
    private String name;
    private int promisedId = -1;
    private int acceptedId = -1;
    private String acceptedValue = null;

    public Acceptor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Promise receivePrepare(int proposalId) {
        if (proposalId > promisedId) {
            promisedId = proposalId;
            return new Promise(true);
        } else {
            return new Promise(false);
        }
    }

    public boolean receiveAccept(int proposalId, String value) {
        if (proposalId >= promisedId) {
            promisedId = proposalId;
            acceptedId = proposalId;
            acceptedValue = value;
            return true;
        } else {
            return false;
        }
    }

    public String getAcceptedValue() {
        return acceptedValue;
    }
}
