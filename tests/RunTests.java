import exception.RepositoryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RunTests {
    public static void main(String[] args) throws RepositoryException {
        TestEntity.run();
        TestRepository.run();
        TestValidator.run();
        TestService.run();
        TestCommunities.run();

        System.out.println("All tests passed at: " + DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }
}
