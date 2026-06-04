import java.util.List;

public class MinMax {

    private static final int MAX_DEPTH = 2;

    public static GameState bestMove(GameState state) {
        List<GameState> successors = BackgammonRules.getSuccessors(state);

        GameState bestState = null;
        int       bestValue = Integer.MIN_VALUE;

        for (GameState succ : successors) {
            int value = minValue(succ, MAX_DEPTH - 1);
            if (value > bestValue) {
                bestValue = value;
                bestState = succ;
            }
        }

        return (bestState != null) ? bestState : state;
    }

    private static int maxValue(GameState state, int depth) {
        if (depth == 0 || BackgammonRules.isTerminal(state)) {
            return Heuristic.evaluate(state);
        }

        int best = Integer.MIN_VALUE;

        for (int die = 1; die <= 6; die++) {
            GameState withDie = new GameState(state);
            withDie.die = die;
            List<GameState> successors = BackgammonRules.getSuccessors(withDie);
            for (GameState succ : successors) {
                int val = minValue(succ, depth - 1);
                if (val > best) best = val;
            }
        }

        return best;
    }

    private static int minValue(GameState state, int depth) {
        if (depth == 0 || BackgammonRules.isTerminal(state)) {
            return Heuristic.evaluate(state);
        }

        int worst = Integer.MAX_VALUE;

        for (int die = 1; die <= 6; die++) {
            GameState withDie = new GameState(state);
            withDie.die = die;
            List<GameState> successors = BackgammonRules.getSuccessors(withDie);
            for (GameState succ : successors) {
                int val = maxValue(succ, depth - 1);
                if (val < worst) worst = val;
            }
        }

        return worst;
    }
}
