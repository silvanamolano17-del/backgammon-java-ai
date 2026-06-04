import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Ventana principal del juego.
 *
 * MODO DADO MANUAL:
 *   - Tanto el humano como la IA ingresan el valor del dado manualmente.
 *   - Esto permite que todos los equipos en clase usen los MISMOS valores
 *     de dado y comparen qué IA toma la mejor decisión.
 *   - El botón "Confirmar dado" reemplaza al anterior "Tirar dado".
 *   - Un spinner (1-6) permite elegir el número antes de confirmar.
 */
public class BackgammonGUI extends JFrame {

    // ── Colores ───────────────────────────────────────────────
    private static final Color C_BG      = new Color(0x0D0D0D);
    private static final Color C_PANEL   = new Color(0x1A1200);
    private static final Color C_ACCENT  = new Color(0xD4A017);
    private static final Color C_WHITE   = new Color(0xF5F0E8);
    private static final Color C_MUTED   = new Color(0x888888);
    private static final Color C_SUCCESS = new Color(0x4CAF50);
    private static final Color C_DANGER  = new Color(0xE53935);

    // ── Componentes ───────────────────────────────────────────
    private BackgammonPanel boardPanel;
    private JLabel   lblTurn, lblDie, lblScoreW, lblScoreB, lblStatus;
    private JSpinner spinnerDie;        // ← selector 1-6 para el dado
    private JButton  btnConfirmDie;     // ← "Confirmar dado" (reemplaza al aleatorio)
    private JTextArea logArea;

    // ── Estado ────────────────────────────────────────────────
    private GameState state;
    private boolean waitingForHuman = false;
    // Indica si el dado ya fue confirmado y hay que esperar el movimiento
    private boolean dieConfirmed = false;

    public BackgammonGUI() {
        super("Backgamón – Taller 4 IA  |  MIN-MAX Depth 2  |  Dado Manual");
        state = new GameState();
        buildUI();
        setVisible(true);
        log("Juego iniciado. Dado MANUAL activado.");
        log("Introduce el valor del dado antes de cada turno.");
        log("─────────────────────────────────");
        updateUI();
        SwingUtilities.invokeLater(this::nextTurn);
    }

    // ─── Construcción del UI ──────────────────────────────────
    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(C_BG);
        setLayout(new BorderLayout(0, 0));
        setResizable(false);

        boardPanel = new BackgammonPanel(state, this);
        boardPanel.setBorder(BorderFactory.createLineBorder(C_ACCENT, 2));
        add(boardPanel, BorderLayout.CENTER);

        JPanel sidebar = buildSidebar();
        sidebar.setPreferredSize(new Dimension(230, BackgammonPanel.BOARD_H));
        add(sidebar, BorderLayout.EAST);

        add(buildHeader(), BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(C_BG);
        header.setBorder(new EmptyBorder(6, 0, 4, 0));
        JLabel title = new JLabel("⬡  BACKGAMÓN  —  IA vs HUMANO  |  DADO MANUAL  ⬡");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(C_ACCENT);
        header.add(title);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(C_PANEL);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(C_ACCENT, 1),
                new EmptyBorder(12, 12, 12, 12)));

        // ── Turno ─────────────────────────────────────────────
        side.add(sectionLabel("TURNO"));
        lblTurn = styledLabel("—", 15, C_WHITE);
        lblTurn.setAlignmentX(LEFT_ALIGNMENT);
        side.add(lblTurn);
        side.add(Box.createVerticalStrut(12));

        // ── Dado actual ───────────────────────────────────────
        side.add(sectionLabel("DADO ACTUAL"));
        lblDie = new JLabel("?");
        lblDie.setFont(new Font("Monospaced", Font.BOLD, 36));
        lblDie.setForeground(C_ACCENT);
        lblDie.setAlignmentX(LEFT_ALIGNMENT);
        side.add(lblDie);
        side.add(Box.createVerticalStrut(10));

        // ── Selector de dado manual ───────────────────────────
        side.add(sectionLabel("INGRESAR DADO (1-6)"));

        // Panel horizontal: spinner + botón
        JPanel dieInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dieInputPanel.setBackground(C_PANEL);
        dieInputPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Spinner para elegir número del dado
        SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 1, 6, 1);
        spinnerDie = new JSpinner(spinModel);
        spinnerDie.setFont(new Font("Georgia", Font.BOLD, 18));
        spinnerDie.setPreferredSize(new Dimension(70, 36));
        // Estilo del spinner
        JComponent editor = spinnerDie.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(new Color(0x2A2000));
            tf.setForeground(C_ACCENT);
            tf.setCaretColor(C_ACCENT);
            tf.setHorizontalAlignment(JTextField.CENTER);
        }
        dieInputPanel.add(spinnerDie);

        // Botón confirmar dado
        btnConfirmDie = buildButton("✓ Confirmar", C_ACCENT, C_BG);
        btnConfirmDie.setPreferredSize(new Dimension(110, 36));
        btnConfirmDie.setMaximumSize(new Dimension(110, 36));
        btnConfirmDie.addActionListener(e -> onConfirmDie());
        dieInputPanel.add(btnConfirmDie);

        side.add(dieInputPanel);
        side.add(Box.createVerticalStrut(14));

        // ── Marcador ──────────────────────────────────────────
        side.add(sectionLabel("FICHAS FUERA"));
        JPanel scores = new JPanel(new GridLayout(2, 1, 2, 4));
        scores.setBackground(C_PANEL);
        scores.setAlignmentX(LEFT_ALIGNMENT);
        lblScoreW = styledLabel("⬜ Blanco (IA):  0/15", 12, C_WHITE);
        lblScoreB = styledLabel("⬛ Negro  (Hu):  0/15", 12, new Color(0xBBBBBB));
        scores.add(lblScoreW);
        scores.add(lblScoreB);
        side.add(scores);
        side.add(Box.createVerticalStrut(14));

        // ── Estado ────────────────────────────────────────────
        side.add(sectionLabel("ESTADO"));
        lblStatus = styledLabel("Iniciando...", 11, C_MUTED);
        lblStatus.setAlignmentX(LEFT_ALIGNMENT);
        side.add(lblStatus);
        side.add(Box.createVerticalStrut(14));

        // ── Log ───────────────────────────────────────────────
        side.add(sectionLabel("HISTORIAL"));
        logArea = new JTextArea(10, 16);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        logArea.setBackground(new Color(0x0A0A0A));
        logArea.setForeground(new Color(0x88CC88));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(4, 4, 4, 4));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createLineBorder(C_ACCENT.darker(), 1));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        side.add(scroll);
        side.add(Box.createVerticalStrut(10));

        // ── Nuevo juego ───────────────────────────────────────
        JButton btnNew = buildButton("↺  Nuevo juego", new Color(0x444444), C_WHITE);
        btnNew.setAlignmentX(LEFT_ALIGNMENT);
        btnNew.addActionListener(e -> newGame());
        side.add(btnNew);

        return side;
    }

    // ─── Helpers UI ──────────────────────────────────────────
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.BOLD, 10));
        lbl.setForeground(C_ACCENT);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 2, 0));
        return lbl;
    }

    private JLabel styledLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.PLAIN, size));
        lbl.setForeground(color);
        return lbl;
    }

    private JButton buildButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Georgia", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(196, 34));
        btn.setPreferredSize(new Dimension(196, 34));
        return btn;
    }

    // ─── Actualizar UI ───────────────────────────────────────
    private void updateUI() {
        lblTurn.setText(state.turn.displayName);
        lblTurn.setForeground(state.turn == Player.WHITE ? C_WHITE : new Color(0x999999));

        if (state.die > 0) {
            lblDie.setText(dieFace(state.die));
            lblDie.setForeground(state.turn == Player.WHITE ? C_ACCENT : new Color(0xCCCCCC));
        } else {
            lblDie.setText("?");
            lblDie.setForeground(C_MUTED);
        }

        lblScoreW.setText("⬜ Blanco (IA):  " + state.offWhite + "/15");
        lblScoreB.setText("⬛ Negro  (Hu):  " + state.offBlack + "/15");
        boardPanel.setState(state);
    }

    private String dieFace(int n) {
        return switch (n) {
            case 1 -> "⚀"; case 2 -> "⚁"; case 3 -> "⚂";
            case 4 -> "⚃"; case 5 -> "⚄"; case 6 -> "⚅";
            default -> String.valueOf(n);
        };
    }

    // ─── Flujo principal ─────────────────────────────────────
    private void nextTurn() {
        if (BackgammonRules.isTerminal(state)) { endGame(); return; }

        // Habilitar siempre el spinner y botón para que el usuario ingrese el dado
        spinnerDie.setEnabled(true);
        btnConfirmDie.setEnabled(true);
        dieConfirmed = false;

        String player = state.turn.displayName;
        String instruction = state.turn == Player.WHITE
                ? "Ingresa el dado de la IA y confirma"
                : "Ingresa tu dado y confirma";

        lblStatus.setText("<html><b>" + player + "</b><br>" + instruction + "</html>");
        lblStatus.setForeground(state.turn == Player.WHITE ? C_ACCENT : C_SUCCESS);

        // Resetear el dado en la UI
        state.die = 0;
        updateUI();
    }

    /**
     * Se ejecuta cuando el usuario hace clic en "✓ Confirmar".
     * Aplica el valor del spinner como dado del turno actual.
     */
    private void onConfirmDie() {
        if (dieConfirmed) return;  // Evitar doble confirmación

        int dieValue = (Integer) spinnerDie.getValue();
        state.die = dieValue;
        dieConfirmed = true;

        // Deshabilitar hasta el próximo turno
        spinnerDie.setEnabled(false);
        btnConfirmDie.setEnabled(false);

        String who = state.turn == Player.WHITE ? "IA" : "Humano";
        log(who + " confirma dado: " + dieValue + " " + dieFace(dieValue));
        updateUI();

        if (state.turn == Player.WHITE) {
            // Turno de la IA: calcular con MIN-MAX tras un pequeño delay visual
            lblStatus.setText("<html>IA calculando con dado = " + dieValue + "...</html>");
            lblStatus.setForeground(C_ACCENT);
            Timer t = new Timer(600, e -> doAIMove());
            t.setRepeats(false);
            t.start();
        } else {
            // Turno del humano: generar movimientos válidos
            List<GameState> moves = BackgammonRules.getSuccessors(state);
            if (moves.size() == 1 && moves.get(0).isPassed) {
                log("Sin movimientos válidos → turno pasado.");
                state = moves.get(0);
                state.die = 0;
                updateUI();
                Timer t = new Timer(500, e -> nextTurn());
                t.setRepeats(false);
                t.start();
                return;
            }
            boardPanel.setValidMoves(moves);
            waitingForHuman = true;
            lblStatus.setText("<html>Clic en tu ficha → clic en destino</html>");
            lblStatus.setForeground(C_SUCCESS);
        }
    }

    /** Llamado por BackgammonPanel cuando el humano elige un movimiento. */
    public void applyHumanMove(GameState chosen) {
        waitingForHuman = false;
        boardPanel.setValidMoves(java.util.Collections.emptyList());
        if (chosen.lastMove != null) log("Mueves: " + chosen.lastMove);
        state = chosen;
        state.die = 0;
        updateUI();
        if (BackgammonRules.isTerminal(state)) { endGame(); return; }
        Timer t = new Timer(400, e -> nextTurn());
        t.setRepeats(false);
        t.start();
    }

    private void doAIMove() {
        SwingWorker<GameState, Void> worker = new SwingWorker<>() {
            @Override
            protected GameState doInBackground() {
                return MinMax.bestMove(state);
            }
            @Override
            protected void done() {
                try {
                    GameState next = get();
                    if (next.isPassed) {
                        log("IA pasa turno (sin movimientos válidos).");
                    } else if (next.lastMove != null) {
                        log("IA juega: " + next.lastMove);
                    }
                    state = next;
                    state.die = 0;
                    updateUI();
                    if (BackgammonRules.isTerminal(state)) { endGame(); return; }
                    Timer t = new Timer(500, e -> nextTurn());
                    t.setRepeats(false);
                    t.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void endGame() {
        spinnerDie.setEnabled(false);
        btnConfirmDie.setEnabled(false);
        String winner = state.offWhite == 15
                ? "🏆  ¡Gana BLANCO (IA)!"
                : "🏆  ¡Gana NEGRO (Humano)!";
        Color wColor = state.offWhite == 15 ? C_ACCENT : C_SUCCESS;
        log("════════════════════");
        log(winner);
        log("════════════════════");
        lblStatus.setText("<html><b>" + winner + "</b></html>");
        lblStatus.setForeground(wColor);
        updateUI();
        Timer t = new Timer(500, e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    winner + "\n\n¿Jugar de nuevo?", "Fin del juego",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) newGame();
        });
        t.setRepeats(false);
        t.start();
    }

    private void newGame() {
        state = new GameState();
        dieConfirmed = false;
        logArea.setText("");
        log("── Nuevo juego iniciado ──");
        updateUI();
        SwingUtilities.invokeLater(this::nextTurn);
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
