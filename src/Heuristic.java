public class Heuristic {

    private static final int W1 =  10;
    private static final int W2 =  -1;
    private static final int W3 =   2;
    private static final int W4 =   3;
    private static final int WIN  =  Integer.MAX_VALUE / 2;
    private static final int LOSE = -WIN;

    public static int evaluate(GameState s) {
        if (s.offWhite == 15) return WIN;
        if (s.offBlack == 15) return LOSE;

        int ind1 = s.offWhite - s.offBlack;

        int distWhite = 0, distBlack = 0;
        for (int i = 0; i < 24; i++) {
            if (s.board[i] > 0)  distWhite += s.board[i]  * (i + 1);
            if (s.board[i] < 0)  distBlack += (-s.board[i]) * (24 - i);
        }
        distWhite += s.barWhite * 25;
        distBlack += s.barBlack * 25;
        int ind2 = distWhite - distBlack;

        int blocked = 0;
        for (int i = 0; i < 24; i++) {
            if (s.board[i] >= 2) blocked++;
        }
        int ind3 = blocked;

        int ind4 = s.barBlack - s.barWhite;

        return W1 * ind1 + W2 * ind2 + W3 * ind3 + W4 * ind4;
    }
}
