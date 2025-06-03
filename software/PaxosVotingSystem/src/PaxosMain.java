import java.util.*;

public class PaxosMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Acceptor 생성
        Acceptor a1 = new Acceptor("Acceptor1");
        Acceptor a2 = new Acceptor("Acceptor2");
        Acceptor a3 = new Acceptor("Acceptor3");
        List<Acceptor> acceptors = Arrays.asList(a1, a2, a3);

        Learner learner = new Learner();

        // === Proposer A 입력 받기 ===
        System.out.println("▶ Proposer A");
        System.out.print("Enter proposal ID for A: ");
        int idA = scanner.nextInt();
        scanner.nextLine(); // 줄바꿈 제거
        System.out.print("Enter proposal value for A: ");
        String valueA = scanner.nextLine();

        // === Proposer B 입력 받기 ===
        System.out.println("▶ Proposer B");
        System.out.print("Enter proposal ID for B: ");
        int idB = scanner.nextInt();
        scanner.nextLine(); // 줄바꿈 제거
        System.out.print("Enter proposal value for B: ");
        String valueB = scanner.nextLine();

        // Proposer 객체 생성
        Proposer proposerA = new Proposer(idA, valueA, acceptors, learner);
        Proposer proposerB = new Proposer(idB, valueB, acceptors, learner);

        // 순차적으로 제안 시도
        System.out.println("\n==== Proposer A 실행 ====");
        proposerA.propose();

        System.out.println("\n==== Proposer B 실행 ====");
        proposerB.propose();
    }
}
