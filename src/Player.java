public class Player {
    private double hp = 1000;
    private double apMultiplier = 1.0;
    private double dpMultiplier = 1.0;
    private double ap = 0;
    private double dp = 0;
    private int[] yaku = new int[5]; // モンスターカードの配列

    // コンストラクタやゲッター、セッターメソッドを必要に応じて追加
    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    // 他のゲッターやセッターも同様に追加
}
