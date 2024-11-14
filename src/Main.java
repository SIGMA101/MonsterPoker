import java.util.Scanner;

/**
 * Main
 */
public class Main {

  public static void main(String[] args) throws InterruptedException {
    MonsterPoker mp = initializeGame();
    startGameLoop(mp);
  }

  private static MonsterPoker initializeGame() {
    return new MonsterPoker();
  }

  private static void startGameLoop(MonsterPoker mp) throws InterruptedException {
    Scanner scanner = new Scanner(System.in);
    playUntilGameEnds(mp, scanner);
    scanner.close();
  }

  private static void playUntilGameEnds(MonsterPoker mp, Scanner scanner) throws InterruptedException {
    while (true) {
      playRound(mp, scanner);
      if (isGameOver(mp)) break;
      Thread.sleep(2000);
    }
  }

  private static void playRound(MonsterPoker mp, Scanner scanner) throws InterruptedException{
    mp.drawPhase(scanner);
    mp.battlePhase();
  }

  private static boolean isGameOver(MonsterPoker mp) throws InterruptedException{
    return displayResult(mp.isDraw(), "引き分け！") ||
           displayResult(mp.isCpuWin(), "CPU Win!") ||
           displayResult(mp.isPlayerWin(), "Player Win!");
  }

  private static boolean displayResult(boolean condition, String message) throws InterruptedException{
    if (condition) {
      System.out.println(message);
      return true;
    }
    return false;
  }
}
