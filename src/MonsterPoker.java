import java.util.Random;
import java.util.Scanner;

/**
 * MonsterPoker
 */
public class MonsterPoker {

  Random card = new Random();

  double playerHp = 1000;
  double cpuHp = 1000;
  int playerDeck[] = new int[5]; // 0~4までの数字（モンスターID）が入る
  int cpuDeck[] = new int[5];
  String monsters[] = { "スライム", "サハギン", "ドラゴン", "デュラハン", "シーサーペント" };// それぞれが0~4のIDのモンスターに相当する
  int monsterAp[] = { 10, 20, 30, 25, 30 };
  int monsterDp[] = { 40, 20, 25, 15, 20 };
  int cpuExchangeCards[] = new int[5];// それぞれ0,1でどのカードを交換するかを保持する．{0,1,1,0,1}の場合は2,3,5枚目のカードを交換することを表す
  String cpuExchange = new String();// 交換するカードを1~5の数字の組み合わせで保持する．上の例の場合，"235"となる．
  int playerYaku[] = new int[5];// playerのモンスターカードがそれぞれ何枚ずつあるかを保存する配列．{2,3,0,0,0}の場合，ID0が2枚,ID1が3枚あることを示す．
  int cpuYaku[] = new int[5];// playerのモンスターカードがそれぞれ何枚ずつあるかを保存する配列．{2,3,0,0,0}の場合，ID0が2枚,ID1が3枚あることを示す．
  double playerApRate = 1;// 役によるAP倍率．1.5倍の場合は1.5となる
  double playerDpRate = 1;
  double playerAp = 0;
  double playerDp = 0;
  double cpuApRate = 1;// 役によるAP倍率．1.5倍の場合は1.5となる
  double cpuDpRate = 1;
  double cpuAp = 0;
  double cpuDp = 0;
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
    // 初期Draw
    System.out.println("PlayerのDraw！");
    for (int i = 0; i < playerDeck.length; i++) {
      this.playerDeck[i] = card.nextInt(5);
    }
    // カードの表示
    System.out.print("[Player]");
    for (int i = 0; i < playerDeck.length; i++) {
      System.out.printf("%s ", this.monsters[playerDeck[i]]);
    }
    System.out.println();

    // カードの交換
    System.out.println("カードを交換する場合は1から5の数字（左から数えた位置を表す）を続けて入力してください．交換しない場合は0と入力してください");
    String exchange = scanner.nextLine();
    if (exchange.charAt(0) != '0') {
      for (int i = 0; i < exchange.length(); i++) {
        this.playerDeck[Character.getNumericValue(exchange.charAt(i)) - 1] = card.nextInt(5);
      }
      // カードの表示
      System.out.print("[Player]");
      for (int i = 0; i < playerDeck.length; i++) {
        System.out.printf("%s ", this.monsters[playerDeck[i]]);
      }
      System.out.println();
      System.out.println("もう一度カードを交換する場合は1から5の数字（左から数えた位置を表す）を続けて入力してください．交換しない場合は0と入力してください");
      exchange = scanner.nextLine();
      if (exchange.charAt(0) != '0') {
        for (int i = 0; i < exchange.length(); i++) {
          this.playerDeck[Character.getNumericValue(exchange.charAt(i)) - 1] = card.nextInt(5);
        }
        // カードの表示
        System.out.print("[Player]");
        for (int i = 0; i < playerDeck.length; i++) {
          System.out.printf("%s ", this.monsters[playerDeck[i]]);
        }
        System.out.println();
      }
    }

    System.out.println("CPUのDraw！");
    for (int i = 0; i < cpuDeck.length; i++) {
      this.cpuDeck[i] = card.nextInt(5);
    }
    // カードの表示
    System.out.print("[CPU]");
    for (int i = 0; i < cpuDeck.length; i++) {
      System.out.printf("%s ", this.monsters[cpuDeck[i]]);
    }
    System.out.println();

    // 交換するカードの決定
    System.out.println("CPUが交換するカードを考えています・・・・・・");
    Thread.sleep(2000);
    // cpuDeckを走査して，重複するカード以外のカードをランダムに交換する
    // 0,1,0,2,3 といったcpuDeckの場合，2枚目，4枚目，5枚目のカードをそれぞれ交換するかどうか決定し，例えば24といった形で決定する
    // 何番目のカードを交換するかを0,1で持つ配列の初期化
    // 例えばcpuExchangeCards[]が{0,1,1,0,0}の場合は2,3枚目を交換の候補にする
    for (int i = 0; i < this.cpuExchangeCards.length; i++) {
      this.cpuExchangeCards[i] = -1;
    }
    for (int i = 0; i < this.cpuDeck.length; i++) {
      if (this.cpuExchangeCards[i] == -1) {
        for (int j = i + 1; j < this.cpuDeck.length; j++) {
          if (this.cpuDeck[i] == this.cpuDeck[j]) {
            this.cpuExchangeCards[i] = 0;
            this.cpuExchangeCards[j] = 0;
          }
        }
        if (this.cpuExchangeCards[i] != 0) {
          this.cpuExchangeCards[i] = this.card.nextInt(2);// 交換するかどうかをランダムに最終決定する
          // this.cpuExchangeCards[i] = 1;
        }
      }
    }

    // 交換するカード番号の表示
    this.cpuExchange = "";
    for (int i = 0; i < cpuExchangeCards.length; i++) {
      if (this.cpuExchangeCards[i] == 1) {
        this.cpuExchange = this.cpuExchange + (i + 1);
      }
    }
    if (this.cpuExchange.length() == 0) {
      this.cpuExchange = "0";
    }
    System.out.println(this.cpuExchange);

    // カードの交換
    if (cpuExchange.charAt(0) != '0') {
      for (int i = 0; i < cpuExchange.length(); i++) {
        this.cpuDeck[Character.getNumericValue(cpuExchange.charAt(i)) - 1] = card.nextInt(5);
      }
      // カードの表示
      System.out.print("[CPU]");
      for (int i = 0; i < cpuDeck.length; i++) {
        System.out.printf("%s ", this.monsters[cpuDeck[i]]);
      }
      System.out.println();
    }

    // 交換するカードの決定
    System.out.println("CPUが交換するカードを考えています・・・・・・");
    Thread.sleep(2000);
    // cpuDeckを走査して，重複するカード以外のカードをランダムに交換する
    // 0,1,0,2,3 といったcpuDeckの場合，2枚目，4枚目，5枚目のカードをそれぞれ交換するかどうか決定し，例えば24といった形で決定する
    // 何番目のカードを交換するかを0,1で持つ配列の初期化
    // 例えばcpuExchangeCards[]が{0,1,1,0,0}の場合は2,3枚目を交換の候補にする
    for (int i = 0; i < this.cpuExchangeCards.length; i++) {
      this.cpuExchangeCards[i] = -1;
    }
    for (int i = 0; i < this.cpuDeck.length; i++) {
      if (this.cpuExchangeCards[i] == -1) {
        for (int j = i + 1; j < this.cpuDeck.length; j++) {
          if (this.cpuDeck[i] == this.cpuDeck[j]) {
            this.cpuExchangeCards[i] = 0;
            this.cpuExchangeCards[j] = 0;
          }
        }
        if (this.cpuExchangeCards[i] != 0) {
          this.cpuExchangeCards[i] = this.card.nextInt(2);// 交換するかどうかをランダムに最終決定する
          // this.cpuExchangeCards[i] = 1;
        }
      }
    }

    // 交換するカード番号の表示
    this.cpuExchange = "";
    for (int i = 0; i < cpuExchangeCards.length; i++) {
      if (this.cpuExchangeCards[i] == 1) {
        this.cpuExchange = this.cpuExchange + (i + 1);
      }
    }
    if (this.cpuExchange.length() == 0) {
      this.cpuExchange = "0";
    }
    System.out.println(this.cpuExchange);

    // カードの交換
    if (cpuExchange.charAt(0) != '0') {
      for (int i = 0; i < cpuExchange.length(); i++) {
        this.cpuDeck[Character.getNumericValue(cpuExchange.charAt(i)) - 1] = card.nextInt(5);
      }
      // カードの表示
      System.out.print("[CPU]");
      for (int i = 0; i < cpuDeck.length; i++) {
        System.out.printf("%s ", this.monsters[cpuDeck[i]]);
      }
      System.out.println();
    }
  }

  public void battlePhase() throws InterruptedException {
    // Playerの役の判定
    // 役判定用配列の初期化
    for (int i = 0; i < playerYaku.length; i++) {
      this.playerYaku[i] = 0;
    }
    // モンスターカードが何が何枚あるかをplayerYaku配列に登録
    for (int i = 0; i < playerDeck.length; i++) {
      this.playerYaku[this.playerDeck[i]]++;
    }
    // 役判定
    // 5が1つある：ファイブ
    // 4が1つある：フォー
    // 3が1つあり，かつ，2が1つある：フルハウス
    // 2が2つある：ツーペア
    // 3が1つある：スリー
    // 2が1つある：ペア
    // 1が5つある：スペシャルファイブ
    // 初期化
    five = false;
    four = false;
    three = false;
    pair = 0; // pair数を保持する
    one = 0;// 1枚だけのカードの枚数
    // 手札ごとのplayerYaku配列の作成
    for (int i = 0; i < playerYaku.length; i++) {
      if (playerYaku[i] == 1) {
        one++;
      } else if (playerYaku[i] == 2) {
        pair++;
      } else if (playerYaku[i] == 3) {
        three = true;
      } else if (playerYaku[i] == 4) {
        four = true;
      } else if (playerYaku[i] == 5) {
        five = true;
      }
    }

    // 役の判定
    System.out.println("Playerの役は・・");
    this.playerApRate = 1;// 初期化
    this.playerDpRate = 1;
    if (one == 5) {
      System.out.println("スペシャルファイブ！AP/DPは両方10倍！");
      this.playerApRate = 10;
      this.playerDpRate = 10;
    } else if (five == true) {
      System.out.println("ファイブ！AP/DPは両方5倍！");
      this.playerApRate = 5;
      this.playerDpRate = 5;
    } else if (four == true) {
      System.out.println("フォー！AP/DPは両方4倍！");
      this.playerApRate = 3;
      this.playerDpRate = 3;
    } else if (three == true && pair == 1) {
      System.out.println("フルハウス！AP/DPは両方3倍");
      this.playerApRate = 3;
      this.playerDpRate = 3;
    } else if (three == true) {
      System.out.println("スリーカード！AP/DPはそれぞれ3倍と2倍");
      this.playerApRate = 3;
      this.playerDpRate = 2;
    } else if (pair == 2) {
      System.out.println("ツーペア！AP/DPは両方2倍");
      this.playerApRate = 2;
      this.playerDpRate = 2;
    } else if (pair == 1) {
      System.out.println("ワンペア！AP/DPは両方1/2倍");
      this.playerApRate = 0.5;
      this.playerDpRate = 0.5;
    }
    Thread.sleep(1000);

    // APとDPの計算
    for (int i = 0; i < playerYaku.length; i++) {
      if (playerYaku[i] >= 1) {
        this.playerAp = this.playerAp + this.monsterAp[i] * playerYaku[i];
        this.playerDp = this.playerDp + this.monsterDp[i] * playerYaku[i];
      }
    }
    this.playerAp = this.playerAp * this.playerApRate;
    this.playerDp = this.playerDp * this.playerDpRate;

    // CPUの役の判定
    // 役判定用配列の初期化
    for (int i = 0; i < cpuYaku.length; i++) {
      this.cpuYaku[i] = 0;
    }
    // モンスターカードが何が何枚あるかをcpuYaku配列に登録
    for (int i = 0; i < cpuDeck.length; i++) {
      this.cpuYaku[this.cpuDeck[i]]++;
    }
    // 役判定
    // 5が1つある：ファイブ
    // 4が1つある：フォー
    // 3が1つあり，かつ，2が1つある：フルハウス
    // 2が2つある：ツーペア
    // 3が1つある：スリー
    // 2が1つある：ペア
    // 1が5つある：スペシャルファイブ
    // 初期化
    five = false;
    four = false;
    three = false;
    pair = 0; // pair数を保持する
    one = 0;// 1枚だけのカードの枚数
    // 手札ごとのcpuYaku配列の作成
    for (int i = 0; i < cpuYaku.length; i++) {
      if (cpuYaku[i] == 1) {
        one++;
      } else if (cpuYaku[i] == 2) {
        pair++;
      } else if (cpuYaku[i] == 3) {
        three = true;
      } else if (cpuYaku[i] == 4) {
        four = true;
      } else if (cpuYaku[i] == 5) {
        five = true;
      }
    }

    // 役の判定
    System.out.println("CPUの役は・・");
    this.cpuApRate = 1;// 初期化
    this.cpuDpRate = 1;
    if (one == 5) {
      System.out.println("スペシャルファイブ！AP/DPは両方10倍！");
      this.cpuApRate = 10;
      this.cpuDpRate = 10;
    } else if (five == true) {
      System.out.println("ファイブ！AP/DPは両方5倍！");
      this.cpuApRate = 5;
      this.cpuDpRate = 5;
    } else if (four == true) {
      System.out.println("フォー！AP/DPは両方4倍！");
      this.cpuApRate = 3;
      this.cpuDpRate = 3;
    } else if (three == true && pair == 1) {
      System.out.println("フルハウス！AP/DPは両方3倍");
      this.cpuApRate = 3;
      this.cpuDpRate = 3;
    } else if (three == true) {
      System.out.println("スリーカード！AP/DPはそれぞれ3倍と2倍");
      this.cpuApRate = 3;
      this.cpuDpRate = 2;
    } else if (pair == 2) {
      System.out.println("ツーペア！AP/DPは両方2倍");
      this.cpuApRate = 2;
      this.cpuDpRate = 2;
    } else if (pair == 1) {
      System.out.println("ワンペア！AP/DPは両方1/2倍");
      this.cpuApRate = 0.5;
      this.cpuDpRate = 0.5;
    }
    Thread.sleep(1000);

    // APとDPの計算
    for (int i = 0; i < cpuYaku.length; i++) {
      if (cpuYaku[i] >= 1) {
        this.cpuAp = this.cpuAp + this.monsterAp[i] * cpuYaku[i];
        this.cpuDp = this.cpuDp + this.monsterDp[i] * cpuYaku[i];
      }
    }
    this.cpuAp = this.cpuAp * this.cpuApRate;
    this.cpuDp = this.cpuDp * this.cpuDpRate;

    // バトル
    System.out.println("バトル！！");
    // Playerの攻撃
    System.out.print("PlayerのDrawした");
    for (int i = 0; i < playerYaku.length; i++) {
      if (playerYaku[i] >= 1) {
        System.out.print(this.monsters[i] + " ");
        Thread.sleep(500);
      }
    }
    System.out.print("の攻撃！");
    Thread.sleep(1000);
    System.out.println("CPUのモンスターによるガード！");
    if (this.cpuDp >= this.playerAp) {
      System.out.println("CPUはノーダメージ！");
    } else {
      double damage = this.playerAp - this.cpuDp;
      System.out.printf("CPUは%.0fのダメージを受けた！\n", damage);
      this.cpuHp = this.cpuHp - damage;
    }

    // CPUの攻撃
    System.out.print("CPUのDrawした");
    for (int i = 0; i < cpuYaku.length; i++) {
      if (cpuYaku[i] >= 1) {
        System.out.print(this.monsters[i] + " ");
        Thread.sleep(500);
      }
    }
    System.out.print("の攻撃！");
    Thread.sleep(1000);
    System.out.println("Playerのモンスターによるガード！");
    if (this.playerDp >= this.cpuAp) {
      System.out.println("Playerはノーダメージ！");
    } else {
      double damage = this.cpuAp - this.playerDp;
      System.out.printf("Playerは%.0fのダメージを受けた！\n", damage);
      this.playerHp = this.playerHp - damage;
    }

    System.out.println("PlayerのHPは" + this.playerHp);
    System.out.println("CPUのHPは" + this.cpuHp);

  }

  public double getPlayerHp() {
    return this.playerHp;
  }

  public double getCpuHp() {
    return this.cpuHp;
  }

}
