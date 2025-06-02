import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PaxosMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter proposal ID (integer): ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter proposal value: ");
        String value = scanner.nextLine();

        List<Acceptor> acceptors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            acceptors.add(new Acceptor());
        }

        Learner learner = new Learner();
        Proposer proposer = new Proposer(id, value);
        proposer.propose(acceptors, learner);
    }
}
