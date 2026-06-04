import java.util.Arrays;

public class GameState {

    public int[] board;
    public int barWhite;
    public int barBlack;
    public int offWhite;
    public int offBlack;
    public Player turn;
    public int die;
    public Move lastMove;
    public boolean isPassed;

    public GameState() {
        board = new int[24];
        initBoard();
        turn = Player.WHITE;
        die  = 0;
    }

    public GameState(GameState other) {
        this.board    = Arrays.copyOf(other.board, 24);
        this.barWhite = other.barWhite;
        this.barBlack = other.barBlack;
        this.offWhite = other.offWhite;
        this.offBlack = other.offBlack;
        this.turn     = other.turn;
        this.die      = other.die;
        this.lastMove = other.lastMove;
        this.isPassed = other.isPassed;
    }

    private void initBoard() {
        board[23] =  2;
        board[12] =  5;
        board[7]  =  3;
        board[5]  =  5;
        board[0]  = -2;
        board[11] = -5;
        board[16] = -3;
        board[18] = -5;
    }

    public int countWhiteOnBoard() {
        int t = 0;
        for (int v : board) if (v > 0) t += v;
        return t;
    }

    public int countBlackOnBoard() {
        int t = 0;
        for (int v : board) if (v < 0) t -= v;
        return t;
    }
}
