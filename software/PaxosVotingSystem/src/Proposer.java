import java.util.List;

public class Proposer {
    private int proposalId;
    private String value;

    public Proposer(int proposalId, String value) {
        this.proposalId = proposalId;
        this.value = value;
    }

    public void propose(List<Acceptor> acceptors, Learner learner) {
        int promiseCount = 0;

        for (Acceptor acceptor : acceptors) {
            if (acceptor.receivePrepare(proposalId)) {
                promiseCount++;
            }
        }

        if (promiseCount > acceptors.size() / 2) {
            int acceptCount = 0;
            for (Acceptor acceptor : acceptors) {
                if (acceptor.receiveAcceptRequest(proposalId, value)) {
                    acceptCount++;
                }
            }
            if (acceptCount > acceptors.size() / 2) {
                learner.learn(value);
            } else {
                System.out.println("Proposal not accepted by majority.");
            }
        } else {
            System.out.println("Not enough promises.");
        }
    }
}
