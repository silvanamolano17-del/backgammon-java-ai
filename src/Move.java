public class Move {
    public final int from;
    public final int to;

    public Move(int from, int to) {
        this.from = from;
        this.to   = to;
    }

    @Override
    public String toString() {
        String f = (from == 0) ? "BARRA" : "Pto." + from;
        String t = (to   == 0) ? "FUERA" : "Pto." + to;
        return f + " → " + t;
    }
}
