import java.util.ArrayList;
import java.util.List;

public class Proposer {
    private int proposalId;
    private String proposalValue;
    private List<Acceptor> acceptors;
    private Learner learner;

    public Proposer(int proposalId, String proposalValue, List<Acceptor> acceptors, Learner learner) {
        this.proposalId = proposalId;
        this.proposalValue = proposalValue;
        this.acceptors = acceptors;
        this.learner = learner;
    }

    public void propose() {
        System.out.println("[Proposer] Sending prepare request with proposal ID " + proposalId);
        List<Promise> promises = new ArrayList<>();

        for (Acceptor acceptor : acceptors) {
            Promise promise = acceptor.receivePrepare(proposalId);
            if (promise.isAccepted()) {
                System.out.println("[Acceptor] " + acceptor.getName() + " promised for proposal ID " + proposalId);
                promises.add(promise);
            } else {
                System.out.println("[Acceptor] " + acceptor.getName() + " rejected proposal ID " + proposalId);
            }
        }

        if (promises.size() > acceptors.size() / 2) {
            System.out.println("[Proposer] Majority promises received. Sending accept requests.");
            int acceptCount = 0;

            for (Acceptor acceptor : acceptors) {
                boolean accepted = acceptor.receiveAccept(proposalId, proposalValue);
                if (accepted) {
                    System.out.println("[Acceptor] " + acceptor.getName() + " accepted proposal ID " + proposalId + " with value " + proposalValue);
                    acceptCount++;
                } else {
                    System.out.println("[Acceptor] " + acceptor.getName() + " rejected proposal ID " + proposalId + " in accept phase.");
                }
            }

            if (acceptCount > acceptors.size() / 2) {
                learner.learn(proposalValue);
            } else {
                System.out.println("[Proposer] Proposal was not accepted by majority.");
            }
        } else {
            System.out.println("[Proposer] Failed to receive majority promises.");
        }
    }
}
