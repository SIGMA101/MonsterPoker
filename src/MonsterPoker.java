import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * MonsterPoker
 */

public class MonsterPoker {
  
  Random card = new Random();

  double p11 = 1000; //PlayerのHP
  double c12 = 1000; //cpuのHP
  int playerDeck[] = new int[5]; // 0~4までの数字（モンスターID）が入る
  int cpuDeck[] = new int[5];
  String monsters[] = { "スライム", "サハギン", "ドラゴン", "デュラハン", "シーサーペント" };// それぞれが0~4のIDのモンスターに相当する
  int monsterAp[] = { 10, 20, 30, 25, 30 }; //各モンスターのAP
  int monsterDp[] = { 40, 20, 25, 15, 20 }; //各モンスターのDP
  int cpuExchangeCards[] = new int[5];// それぞれ0,1でどのカードを交換するかを保持する．{0,1,1,0,1}の場合は2,3,5枚目のカードを交換することを表す
  String c13 = new String();// 交換するカードを1~5の数字の組み合わせで保持する．上の例の場合，"235"となる．
  int playerYaku[] = new int[5];// playerのモンスターカードがそれぞれ何枚ずつあるかを保存する配列．{2,3,0,0,0}の場合，ID0が2枚,ID1が3枚あることを示す．
  int cpuYaku[] = new int[5];// playerのモンスターカードがそれぞれ何枚ずつあるかを保存する配列．{2,3,0,0,0}の場合，ID0が2枚,ID1が3枚あることを示す．
  double p15 = 1;// Playerの役によるAP倍率．初期値は1で役が決まると対応した数値になる．1.5倍の場合は1.5となる
  double p16 = 1;// Playerの役によるDP倍率．初期値は1で役が決まると対応した数値になる．1.5倍の場合は1.5となる
  double p17 = 0;// PlayerのAP
  double p18 = 0;// PlayerのDP
  double c15 = 1;// CPUの役によるAP倍率．1.5倍の場合は1.5となる
  double c16 = 1;
  double c17 = 0;
  double c18 = 0;
  // 役判定用フラグ
  // 役判定
  // 5が1つある：ファイブ->five = true
  // 4が1つある：フォー->four = true
  // 3が1つあり，かつ，2が1つある：フルハウス->three = true and pair = 1
  // 2が2つある：ツーペア->pair = 2
  // 3が1つある：スリー->three = true;
  // 2が1つある：ペア->pair = 1
  // 1が5つある：スペシャルファイブ->one=5
  boolean five = false;
  boolean four = false;
  boolean three = false;
  int pair = 0; // pair数を保持する
  int one = 0;// 1枚だけのカードの枚数

  /**
   * 5枚のモンスターカードをプレイヤー/CPUが順に引く
   *
   * @throws InterruptedException
   */
  public void drawPhase(Scanner scanner) throws InterruptedException {
    playerTurn(scanner);
    cpuTurn();
  }

  public void playerTurn(Scanner scanner) throws InterruptedException{
    playerInitialDraw();
    playerCardDisplay();
    playerCardExchangeTurn(scanner);
  }

  public void cpuTurn() throws InterruptedException{
    cpuInitialDraw();
    cpuCardDisplay();
    cpuCardExchangePhase();
  }

  public void playerInitialDraw() throws InterruptedException{
    System.out.println("PlayerのDraw！");
    for (int i = 0; i < playerDeck.length; i++) {
      this.playerDeck[i] = card.nextInt(5);
    }
  }

  public void cpuInitialDraw() throws InterruptedException{
    System.out.println("CPUのDraw！");
    for (int i = 0; i < cpuDeck.length; i++) {
      this.cpuDeck[i] = card.nextInt(5);
    }
  }

  public void playerCardDisplay() throws InterruptedException{
    System.out.print("[Player]");
    for (int i = 0; i < playerDeck.length; i++) {
      System.out.printf("%s ", this.monsters[playerDeck[i]]);
    }
    System.out.println();
  }

  public void cpuCardDisplay() throws InterruptedException{
    System.out.print("[CPU]");
    for (int i = 0; i < cpuDeck.length; i++) {
      System.out.printf("%s ", this.monsters[cpuDeck[i]]);
    }
    System.out.println();
  }


  public void playerCardExchangeTurn(Scanner scanner) throws InterruptedException{
    boolean isExchange = playerCardExchange(scanner);
    if(isExchange){
      playerCardExchange(scanner);
    }
  }
  

  public boolean playerCardExchange(Scanner scanner) throws InterruptedException{
    System.out.println("カードを交換する場合は1から5の数字（左から数えた位置を表す）を続けて入力してください．交換しない場合は0と入力してください");
    String exchange = scanner.nextLine();
    if (exchange.charAt(0) == '0') return false;
    for (int i = 0; i < exchange.length(); i++) {
      this.playerDeck[Character.getNumericValue(exchange.charAt(i)) - 1] = card.nextInt(5);
    }
    playerCardDisplay();
    return true;
  }

  public void cpuCardExchangePhase() throws InterruptedException{
    cpuCardExchangeDecideTurn();
    cpuDisplayExchangeCardNum();
    cpuCardExchangeTurn();
    if (this.c13 != "0"){
      cpuCardExchangeDecideTurn();
      cpuDisplayExchangeCardNum();
      cpuCardExchangeTurn();
    }
  }

  public void cpuCardExchangeDecideTurn() throws InterruptedException{
    // 交換するカードの決定
    System.out.println("CPUが交換するカードを考えています・・・・・・");
    Thread.sleep(2000);
    // cpuDeckを走査して，重複するカード以外のカードをランダムに交換する
    // 0,1,0,2,3 といったcpuDeckの場合，2枚目，4枚目，5枚目のカードをそれぞれ交換するかどうか決定し，例えば24といった形で決定する
    // 何番目のカードを交換するかを0,1で持つ配列の初期化
    // 例えばcpuExchangeCards[]が{0,1,1,0,0}の場合は2,3枚目を交換の候補にする
    cpuInitializeExchangeCards();
    cpuCardExchangeDecide();
  }

  public void cpuCardExchangeDecide() throws InterruptedException{
    for (int i = 0; i < this.cpuDeck.length; i++) {
      exchangeJudgement(i);
    }
  }

  public void cpuInitializeExchangeCards() throws InterruptedException{
    for (int i = 0; i < this.cpuExchangeCards.length; i++) {
      this.cpuExchangeCards[i] = -1;
    }
  }

  public void exchangeJudgement(int i) throws InterruptedException{
    if (this.cpuExchangeCards[i] == -1) {
      duplicateJudgement(i);
      if (this.cpuExchangeCards[i] != 0) {
        this.cpuExchangeCards[i] = this.card.nextInt(2);// 交換するかどうかをランダムに最終決定する
        // this.cpuExchangeCards[i] = 1;
      }
    }
  }

  public void duplicateJudgement(int i) throws InterruptedException{
    for (int j = i + 1; j < this.cpuDeck.length; j++) {
      duplicateFlag(i, j);
    }
  }

  public void duplicateFlag(int i, int j) throws InterruptedException{
    if (this.cpuDeck[i] == this.cpuDeck[j]) {
      this.cpuExchangeCards[i] = 0;
      this.cpuExchangeCards[j] = 0;
    }
  }

  public void cpuDisplayExchangeCardNum() throws InterruptedException{
    this.c13 = "";
    for (int i = 0; i < cpuExchangeCards.length; i++) {
      cpuExchangeCardsFlag(i);
    }
    if (this.c13.length() == 0) {
      this.c13 = "0";
    }
    System.out.println(this.c13);
  }

  public void cpuExchangeCardsFlag(int i) throws InterruptedException{
    if (this.cpuExchangeCards[i] == 1) {
      this.c13 = this.c13 + (i + 1);
    }
  }


  public void cpuCardExchangeTurn() throws InterruptedException{
    if (c13.charAt(0) != '0') {
      cpuCardExchangeLoop();
      // カードの表示
      cpuCardDisplay();
    }
  }

  public void cpuCardExchangeLoop() throws InterruptedException{
    for (int i = 0; i < c13.length(); i++) {
      cpuCardExchange(i);
    }
  }

  public void cpuCardExchange(int i) throws InterruptedException{
    this.cpuDeck[Character.getNumericValue(c13.charAt(i)) - 1] = card.nextInt(5);
  }




  public void battlePhase() throws InterruptedException {
    // Playerの役の判定
    playerYakuJudgementPhase();
    
    // CPUの役の判定
    cpuYakuJudgementPhase();

    // バトル
    attackGuardPhase();

    initializeApDp();
  }

  public void initializeApDp() throws InterruptedException{
    p15 = 1;// Playerの役によるAP倍率．初期値は1で役が決まると対応した数値になる．1.5倍の場合は1.5となる
    p16 = 1;// Playerの役によるDP倍率．初期値は1で役が決まると対応した数値になる．1.5倍の場合は1.5となる
    p17 = 0;// PlayerのAP
    p18 = 0;// PlayerのDP
    c15 = 1;// CPUの役によるAP倍率．1.5倍の場合は1.5となる
    c16 = 1;
    c17 = 0;
    c18 = 0;
  }

  public void playerYakuJudgementPhase() throws InterruptedException{
    playerYakuInitialize();   
    playerYakuRegistration();
    yakuCounterInitialize();
    makePlayerYakuLoop();   
    System.out.println("Playerの役は・・");
    playerApDpInitialize(); 
    playerYakuDecision();
    playerApDpCalculate();
  }

  public void cpuYakuJudgementPhase() throws InterruptedException{
    cpuYakuInitialize();
    cpuYakuRegistration();
    yakuCounterInitialize();
    makeCpuYakuLoop();
    System.out.println("CPUの役は・・");
    cpuApDpInitialize();
    cpuYakuDecision();
    cpuApDpCalculate();
  }


  public void playerYakuInitialize() throws InterruptedException {
    for (int i = 0; i < playerYaku.length; i++) {
      this.playerYaku[i] = 0;
    }
  }

  public void playerYakuRegistration() throws InterruptedException{
    for (int i = 0; i < playerDeck.length; i++) {
      this.playerYaku[this.playerDeck[i]]++;
    }
  }

  public void cpuYakuInitialize() throws InterruptedException {
    for (int i = 0; i < cpuYaku.length; i++) {
      this.cpuYaku[i] = 0;
    }
  }

  public void cpuYakuRegistration() throws InterruptedException{
    for (int i = 0; i < cpuDeck.length; i++) {
      this.cpuYaku[this.cpuDeck[i]]++;
    }
  }


  public void makePlayerYakuLoop() throws InterruptedException{
    for (int i = 0; i < playerYaku.length; i++) {
      makePlayerYakuBranch(i);
    }
  }

  public void makePlayerYakuBranch(int i) throws InterruptedException{
    if (playerOneJudgement(i)) return;
    if (playerPairJudgement(i)) return;
    if (playerThreeJudgement(i)) return;
    if (playerFourJudgement(i)) return;
    if (playerFiveJudgement(i)) return;
  }

  public boolean playerOneJudgement(int i) throws InterruptedException{
    if (playerYaku[i] == 1) {
      one++;
      return true;
    }
    return false;
  }

  public boolean playerPairJudgement(int i) throws InterruptedException{
    if (playerYaku[i] == 2) {
      pair++;
      return true;
    }
    return false;
  }

  public boolean playerThreeJudgement(int i) throws InterruptedException{
    if (playerYaku[i] == 3) {
      three = true;
    }
    return three;
  }

  public boolean playerFourJudgement(int i) throws InterruptedException{
    if (playerYaku[i] == 4) {
      four = true;
    }
    return four;
  }

  public boolean playerFiveJudgement(int i) throws InterruptedException{
    if (playerYaku[i] == 5) {
      five = true;
    }
    return five;
  }

  public void makeCpuYakuLoop() throws InterruptedException{
    for (int i = 0; i < cpuYaku.length; i++) {
      makeCpuYakuBranch(i);
    }
  }

  public void makeCpuYakuBranch(int i) throws InterruptedException{
    if (cpuOneJudgement(i)) return;
    if (cpuPairJudgement(i)) return;
    if (cpuThreeJudgement(i)) return;
    if (cpuFourJudgement(i)) return;
    if (cpuFiveJudgement(i)) return;
  }

  public boolean cpuOneJudgement(int i) throws InterruptedException{
    if (cpuYaku[i] == 1) {
      one++;
      return true;
    }
    return false;
  }

  public boolean cpuPairJudgement(int i) throws InterruptedException{
    if (cpuYaku[i] == 2) {
      pair++;
      return true;
    }
    return false;
  }

  public boolean cpuThreeJudgement(int i) throws InterruptedException{
    if (cpuYaku[i] == 3) {
      three = true;
    }
    return three;
  }

  public boolean cpuFourJudgement(int i) throws InterruptedException{
    if (cpuYaku[i] == 4) {
      four = true;
    }
    return four;
  }

  public boolean cpuFiveJudgement(int i) throws InterruptedException{
    if (cpuYaku[i] == 5) {
      five = true;
    }
    return five;
  }


  public void playerYakuDecision() throws InterruptedException{
    if (one == 5) playerYakuSpecialFive();
    else if (five == true) playerYakuFive();
    else if (four == true) playerYakuFour();
    else if (three == true && pair == 1) playerYakuFullhouse();
    else if (three == true) playerYakuThree();
    else if (pair == 2) playerYakuTwoPair();
    else if (pair == 1) playerYakuOnePair();
    Thread.sleep(1000);
  }

  public void playerYakuSpecialFive() throws InterruptedException{
    System.out.println("スペシャルファイブ！AP/DPは両方10倍！");
    this.p15 = 10;
    this.p16 = 10;
  }

  public void playerYakuFive() throws InterruptedException{
    System.out.println("ファイブ！AP/DPは両方5倍！");
    this.p15 = 5;
    this.p16 = 5;
  }

  public void playerYakuFour() throws InterruptedException{
    System.out.println("フォー！AP/DPは両方4倍！");
    this.p15 = 3;
    this.p16 = 3;
  }

  public void playerYakuFullhouse() throws InterruptedException{
    System.out.println("フルハウス！AP/DPは両方3倍");
    this.p15 = 3;
    this.p16 = 3;
  }

  public void playerYakuThree() throws InterruptedException{
    System.out.println("スリーカード！AP/DPはそれぞれ3倍と2倍");
    this.p15 = 3;
    this.p16 = 2;
  }

  public void playerYakuTwoPair() throws InterruptedException{
    System.out.println("ツーペア！AP/DPは両方2倍");
    this.p15 = 2;
    this.p16 = 2;
  }

  public void playerYakuOnePair() throws InterruptedException{
    System.out.println("ワンペア！AP/DPは両方1/2倍");
    this.p15 = 0.5;
    this.p16 = 0.5;
  }

  public void cpuYakuDecision() throws InterruptedException{
    if (one == 5) cpuYakuSpecialFive();
    else if (five == true) cpuYakuFive();
    else if (four == true) cpuYakuFour();
    else if (three == true && pair == 1) cpuYakuFullhouse();
    else if (three == true) cpuYakuThree();
    else if (pair == 2) cpuYakuTwoPair();
    else if (pair == 1) cpuYakuOnePair();
    Thread.sleep(1000);
  }

  public void cpuYakuSpecialFive() throws InterruptedException{
    System.out.println("スペシャルファイブ！AP/DPは両方10倍！");
    this.c15 = 10;
    this.c16 = 10;
  }

  public void cpuYakuFive() throws InterruptedException{
    System.out.println("ファイブ！AP/DPは両方5倍！");
    this.c15 = 5;
    this.c16 = 5;
  }

  public void cpuYakuFour() throws InterruptedException{
    System.out.println("フォー！AP/DPは両方4倍！");
    this.c15 = 3;
    this.c16 = 3;
  }

  public void cpuYakuFullhouse() throws InterruptedException{
    System.out.println("フルハウス！AP/DPは両方3倍");
    this.c15 = 3;
    this.c16 = 3;
  }

  public void cpuYakuThree() throws InterruptedException{
    System.out.println("スリーカード！AP/DPはそれぞれ3倍と2倍");
    this.c15 = 3;
    this.c16 = 2;
  }

  public void cpuYakuTwoPair() throws InterruptedException{
    System.out.println("ツーペア！AP/DPは両方2倍");
    this.c15 = 2;
    this.c16 = 2;
  }

  public void cpuYakuOnePair() throws InterruptedException{
    System.out.println("ワンペア！AP/DPは両方1/2倍");
    this.c15 = 0.5;
    this.c16 = 0.5;
  }

  public void yakuCounterInitialize() throws InterruptedException{
    five = false;
    four = false;
    three = false;
    pair = 0; // pair数を保持する
    one = 0;// 1枚だけのカードの枚数
  }

  public void playerApDpCalculate() throws InterruptedException{
    for (int i = 0; i < playerYaku.length; i++) {
      if (playerYaku[i] >= 1) {
        this.p17 = this.p17 + this.monsterAp[i] * playerYaku[i];
        this.p18 = this.p18 + this.monsterDp[i] * playerYaku[i];
      }
    }
    this.p17 = this.p17 * this.p15;
    this.p18 = this.p18 * this.p16;
  }

  public void cpuApDpCalculate() throws InterruptedException{
    for (int i = 0; i < cpuYaku.length; i++) {
      if (cpuYaku[i] >= 1) {
        this.c17 = this.c17 + this.monsterAp[i] * cpuYaku[i];
        this.c18 = this.c18 + this.monsterDp[i] * cpuYaku[i];
      }
    }
    this.c17 = this.c17 * this.c15;
    this.c18 = this.c18 * this.c16;
  }

  public void playerApDpInitialize() throws InterruptedException{
    this.p15 = 1;// 初期化
    this.p16 = 1;
  }

  public void cpuApDpInitialize() throws InterruptedException{
    this.c15 = 1;// 初期化
    this.c16 = 1;
  }

  // public double getPlayerHp() {
  //   return this.p11;
  // }

  // public double getCpuHp() {
  //   return this.c12;
  // }

  public void applyDamageToPlayer(double damage) {
    this.p11 -= damage;
    System.out.printf("Playerは%.0fのダメージを受けた！\n", damage);
  }

  public void applyDamageToCpu(double damage) {
      this.c12 -= damage;
      System.out.printf("CPUは%.0fのダメージを受けた！\n", damage);
  }

  public boolean isDraw() {
    return this.p11 <= 0 && this.c12 <= 0;
  }

  public boolean isCpuWin() {
      return this.p11 <= 0 && this.c12 > 0;
  }

  public boolean isPlayerWin() {
      return this.c12 <= 0 && this.p11 > 0;
  }


  public void attackGuardPhase() throws InterruptedException{
    System.out.println("バトル！！");
    // Playerの攻撃
    playerAttackPhase();
    // CPUの攻撃
    cpuAttackPhase();
    playerCpuHpDisplay();
  }
  
  
  public void playerAttackPhase() throws InterruptedException{
    playerDrawMonsterDisplay();
    
    cpuDamageDisplay();
  }
  
  public void cpuAttackPhase() throws InterruptedException{
    cpuDrawMonsterDisplay();
    
    playerDamageDisplay();
  }

  public void playerCpuHpDisplay() throws InterruptedException{
    System.out.println("PlayerのHPは" + this.p11);
    System.out.println("CPUのHPは" + this.c12);
  }

  public void playerDrawMonsterDisplay() throws InterruptedException{
    System.out.print("PlayerのDrawした");
    for (int i = 0; i < playerYaku.length; i++) {
      if (playerYaku[i] >= 1) {
        System.out.print(this.monsters[i] + " ");
        Thread.sleep(500);
      }
    }
    System.out.print("の攻撃！");
    Thread.sleep(1000);
  }


  public void cpuDrawMonsterDisplay() throws InterruptedException{
    System.out.print("CPUのDrawした");
    for (int i = 0; i < cpuYaku.length; i++) {
      if (cpuYaku[i] >= 1) {
        System.out.print(this.monsters[i] + " ");
        Thread.sleep(500);
      }
    }
    System.out.print("の攻撃！");
    Thread.sleep(1000);
  }

  public void cpuDamageDisplay() throws InterruptedException{
    System.out.println("CPUのモンスターによるガード！");
    if (this.c18 >= this.p17) {
      System.out.println("CPUはノーダメージ！");
      return;
    }
    double damage = this.p17 - this.c18;
    applyDamageToCpu(damage);
  }

  public void playerDamageDisplay() throws InterruptedException{
    System.out.println("Playerのモンスターによるガード！");
    if (this.p18 >= this.c17) {
      System.out.println("Playerはノーダメージ！");
      return;
    }
    double damage = this.c17 - this.p18;
    applyDamageToPlayer(damage);
  }


  public double getp15() {
    return this.p15;
  }

  public void setp15(double p15) {
    this.p15 = p15;
  }

  public double getp16() {
    return this.p16;
  }

  public void setp16(double p16) {
    this.p16 = p16;
  }

  public double getp17() {
    return this.p17;
  }

  public void setp17(double p17) {
    this.p17 = p17;
  }

  public double getc15() {
    return this.c15;
  }

  public void setc15(double c15) {
    this.c15 = c15;
  }

  public double getc16() {
    return this.c16;
  }

  public void setc16(double c16) {
    this.c16 = c16;
  }

  public void h() throws InterruptedException{

  }

}
