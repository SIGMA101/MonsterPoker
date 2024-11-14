public class Monster {
    private String name;
    private int attackPower;
    private int defensePower;

    public Monster(String name, int ap, int dp) {
        this.name = name;
        this.attackPower = ap;
        this.defensePower = dp;
    }

    public String getName() {
        return name;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefensePower() {
        return defensePower;
    }
}
