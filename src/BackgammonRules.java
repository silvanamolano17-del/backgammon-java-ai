import java.util.ArrayList;
import java.util.List;

public class BackgammonRules {

    public static boolean isTerminal(GameState s) {
        return s.offWhite == 15 || s.offBlack == 15;
    }

    public static List<GameState> getSuccessors(GameState s) {
        List<GameState> result = new ArrayList<>();

        if (s.turn == Player.WHITE) {
            generateWhiteMoves(s, result);
        } else {
            generateBlackMoves(s, result);
        }

        if (result.isEmpty()) {
            GameState pass = new GameState(s);
            pass.turn     = s.turn.opponent();
            pass.isPassed = true;
            pass.lastMove = null;
            result.add(pass);
        }

        return result;
    }

    private static void generateWhiteMoves(GameState s, List<GameState> result) {
        int die = s.die;

        if (s.barWhite > 0) {
            int destIdx = 24 - die;
            if (destIdx >= 18 && isOpenForWhite(s, destIdx)) {
                GameState next = applyWhiteMove(s, -1, destIdx);
                next.barWhite--;
                next.turn = Player.BLACK;
                result.add(next);
            }
            return;
        }

        boolean bearOff = canBearOffWhite(s);

        for (int srcIdx = 23; srcIdx >= 0; srcIdx--) {
            if (s.board[srcIdx] <= 0) continue;

            int destIdx = srcIdx - die;

            if (destIdx >= 0) {
                if (isOpenForWhite(s, destIdx)) {
                    GameState next = applyWhiteMove(s, srcIdx, destIdx);
                    next.turn = Player.BLACK;
                    result.add(next);
                }
            } else if (bearOff) {
                GameState next = new GameState(s);
                next.board[srcIdx]--;
                next.offWhite++;
                next.turn     = Player.BLACK;
                next.lastMove = new Move(srcIdx + 1, 0);
                result.add(next);
            }
        }
    }

    private static void generateBlackMoves(GameState s, List<GameState> result) {
        int die = s.die;

        if (s.barBlack > 0) {
            int destIdx = die - 1;
            if (destIdx <= 5 && isOpenForBlack(s, destIdx)) {
                GameState next = applyBlackMove(s, -1, destIdx);
                next.barBlack--;
                next.turn = Player.WHITE;
                result.add(next);
            }
            return;
        }

        boolean bearOff = canBearOffBlack(s);

        for (int srcIdx = 0; srcIdx < 24; srcIdx++) {
            if (s.board[srcIdx] >= 0) continue;

            int destIdx = srcIdx + die;

            if (destIdx <= 23) {
                if (isOpenForBlack(s, destIdx)) {
                    GameState next = applyBlackMove(s, srcIdx, destIdx);
                    next.turn = Player.WHITE;
                    result.add(next);
                }
            } else if (bearOff) {
                GameState next = new GameState(s);
                next.board[srcIdx]++;
                next.offBlack++;
                next.turn     = Player.WHITE;
                next.lastMove = new Move(srcIdx + 1, 0);
                result.add(next);
            }
        }
    }

    private static boolean isOpenForWhite(GameState s, int idx) {
        return s.board[idx] >= -1;
    }

    private static boolean isOpenForBlack(GameState s, int idx) {
        return s.board[idx] <= 1;
    }

    private static GameState applyWhiteMove(GameState s, int srcIdx, int destIdx) {
        GameState next = new GameState(s);
        if (srcIdx >= 0) next.board[srcIdx]--;
        if (next.board[destIdx] == -1) {
            next.board[destIdx] = 0;
            next.barBlack++;
        }
        next.board[destIdx]++;
        next.lastMove = new Move(srcIdx >= 0 ? srcIdx + 1 : 0, destIdx + 1);
        return next;
    }

    private static GameState applyBlackMove(GameState s, int srcIdx, int destIdx) {
        GameState next = new GameState(s);
        if (srcIdx >= 0) next.board[srcIdx]++;
        if (next.board[destIdx] == 1) {
            next.board[destIdx] = 0;
            next.barWhite++;
        }
        next.board[destIdx]--;
        next.lastMove = new Move(srcIdx >= 0 ? srcIdx + 1 : 0, destIdx + 1);
        return next;
    }

    public static boolean canBearOffWhite(GameState s) {
        if (s.barWhite > 0) return false;
        for (int i = 6; i < 24; i++) {
            if (s.board[i] > 0) return false;
        }
        return true;
    }

    public static boolean canBearOffBlack(GameState s) {
        if (s.barBlack > 0) return false;
        for (int i = 0; i < 18; i++) {
            if (s.board[i] < 0) return false;
        }
        return true;
    }
}
