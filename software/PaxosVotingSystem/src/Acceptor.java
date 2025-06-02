public class Acceptor {
    private int promisedId = -1;
    private int acceptedId = -1;
    private String acceptedValue = null;

    public boolean receivePrepare(int proposalId) {
        if (proposalId > promisedId) {
            promisedId = proposalId;
            return true;
        }
        return false;
    }

    public boolean receiveAcceptRequest(int proposalId, String value) {
        if (proposalId >= promisedId) {
            acceptedId = proposalId;
            acceptedValue = value;
            return true;
        }
        return false;
    }

    public String getAcceptedValue() {
        return acceptedValue;
    }
}
