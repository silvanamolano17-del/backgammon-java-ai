public enum Player {
    WHITE("Blanco [IA]"),
    BLACK("Negro  [Humano]");

    public final String displayName;

    Player(String name) { this.displayName = name; }

    public Player opponent() {
        return this == WHITE ? BLACK : WHITE;
    }
}
