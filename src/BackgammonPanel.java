import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BackgammonPanel extends JPanel implements MouseListener {

    static final int BOARD_W    = 840;
    static final int BOARD_H    = 520;
    static final int PT_W       = 60;
    static final int TRI_H      = 210;
    static final int BAR_W      = 40;
    static final int BEAR_W     = 60;
    static final int PIECE_R    = 22;
    static final int LEFT_MARGIN = 10;

    static final Color C_BOARD     = new Color(0x1A0A00);
    static final Color C_FELT      = new Color(0x1B5E20);
    static final Color C_TRI_DARK  = new Color(0x8B0000);
    static final Color C_TRI_LIGHT = new Color(0xD4A017);
    static final Color C_BAR       = new Color(0x3E2000);
    static final Color C_W_PIECE   = new Color(0xF5F0E8);
    static final Color C_B_PIECE   = new Color(0x1A1A1A);
    static final Color C_W_BORDER  = new Color(0xCCBB88);
    static final Color C_B_BORDER  = new Color(0x555555);
    static final Color C_SELECTED  = new Color(0xFF, 0xD7, 0x00, 180);
    static final Color C_VALID     = new Color(0x00, 0xFF, 0x88, 120);
    static final Color C_TEXT      = new Color(0xF5DEB3);
    static final Color C_BEAR_BG   = new Color(0x2D1500);

    private GameState state;
    private List<GameState> validMoves = new ArrayList<>();
    private Integer selectedPoint = null;
    private BackgammonGUI gui;
    private final int[] ptX = new int[25];

    public BackgammonPanel(GameState initialState, BackgammonGUI gui) {
        this.state = initialState;
        this.gui   = gui;
        setPreferredSize(new Dimension(BOARD_W, BOARD_H));
        setBackground(C_BOARD);
        addMouseListener(this);
        recalcCoords();
    }

    private void recalcCoords() {
        int barStart = LEFT_MARGIN + 6 * PT_W;
        for (int p = 13; p <= 18; p++)
            ptX[p] = LEFT_MARGIN + (p - 13) * PT_W + PT_W / 2;
        for (int p = 19; p <= 24; p++)
            ptX[p] = barStart + BAR_W + (p - 19) * PT_W + PT_W / 2;
        for (int i = 0; i < 6; i++) ptX[12 - i] = ptX[13 + i];
        for (int i = 0; i < 6; i++) ptX[6  - i] = ptX[19 + i];
    }

    public void setState(GameState s) {
        this.state = s;
        selectedPoint = null;
        validMoves.clear();
        repaint();
    }

    public void setValidMoves(List<GameState> moves) {
        this.validMoves = moves;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawFelt(g2);
        drawTriangles(g2);
        drawBar(g2);
        drawBearOffZone(g2);
        drawAllPieces(g2);
        drawBarPieces(g2);
        drawHighlights(g2);
        drawPointNumbers(g2);
    }

    private void drawFelt(Graphics2D g2) {
        int barStart = LEFT_MARGIN + 6 * PT_W;
        g2.setColor(C_FELT);
        g2.fillRect(LEFT_MARGIN, 0, 6 * PT_W, BOARD_H);
        g2.fillRect(barStart + BAR_W, 0, 6 * PT_W, BOARD_H);
        g2.setColor(C_BEAR_BG);
        g2.fillRect(LEFT_MARGIN + BAR_W + 12 * PT_W, 0, BEAR_W, BOARD_H);
    }

    private void drawTriangles(Graphics2D g2) {
        for (int p = 1; p <= 24; p++) {
            Color fill = (p % 2 == 0) ? C_TRI_DARK : C_TRI_LIGHT;
            g2.setColor(fill);
            int cx = ptX[p];
            boolean isTop = (p >= 13);
            int baseY = isTop ? 0 : BOARD_H;
            int tipY  = isTop ? TRI_H : BOARD_H - TRI_H;
            int[] xs = { cx - PT_W/2 + 2, cx + PT_W/2 - 2, cx };
            int[] ys = { baseY, baseY, tipY };
            g2.fillPolygon(xs, ys, 3);
            g2.setColor(fill.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawPolygon(xs, ys, 3);
        }
    }

    private void drawBar(Graphics2D g2) {
        int barX = LEFT_MARGIN + 6 * PT_W;
        g2.setColor(C_BAR);
        g2.fillRect(barX, 0, BAR_W, BOARD_H);
        g2.setColor(new Color(0x8B6914));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(barX, 0, BAR_W, BOARD_H);
        g2.setColor(C_TEXT);
        g2.setFont(new Font("Georgia", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("BAR", barX + (BAR_W - fm.stringWidth("BAR")) / 2, BOARD_H / 2 - 5);
    }

    private void drawBearOffZone(Graphics2D g2) {
        int bearX = LEFT_MARGIN + BAR_W + 12 * PT_W;
        g2.setColor(C_BEAR_BG);
        g2.fillRect(bearX, 0, BEAR_W, BOARD_H);
        g2.setColor(new Color(0x8B6914));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(bearX, 0, BEAR_W, BOARD_H);
        g2.setColor(C_TEXT);
        g2.setFont(new Font("Georgia", Font.BOLD, 10));
        g2.drawString("OUT", bearX + 12, BOARD_H / 2 - 8);

        for (int i = 0; i < state.offWhite; i++) {
            int y = BOARD_H - 20 - i * 12;
            g2.setColor(C_W_PIECE);
            g2.fillOval(bearX + 6, y - 8, 18, 8);
            g2.setColor(C_W_BORDER);
            g2.drawOval(bearX + 6, y - 8, 18, 8);
        }
        for (int i = 0; i < state.offBlack; i++) {
            int y = 20 + i * 12;
            g2.setColor(C_B_PIECE);
            g2.fillOval(bearX + 6, y, 18, 8);
            g2.setColor(C_B_BORDER);
            g2.drawOval(bearX + 6, y, 18, 8);
        }

        g2.setColor(C_W_PIECE);
        g2.setFont(new Font("Consolas", Font.BOLD, 12));
        g2.drawString("W:" + state.offWhite, bearX + 4, BOARD_H - 8);
        g2.setColor(new Color(0xAAAAAA));
        g2.drawString("B:" + state.offBlack, bearX + 4, 14);
    }

    private void drawAllPieces(Graphics2D g2) {
        for (int p = 1; p <= 24; p++) {
            int idx   = p - 1;
            int count = Math.abs(state.board[idx]);
            if (count == 0) continue;
            boolean isWhite = state.board[idx] > 0;
            boolean isTop   = (p >= 13);
            drawStack(g2, ptX[p], isTop, count, isWhite);
        }
    }

    private void drawStack(Graphics2D g2, int cx, boolean fromTop, int count, boolean white) {
        int startY    = fromTop ? PIECE_R + 4 : BOARD_H - PIECE_R - 4;
        int step      = fromTop ? PIECE_R * 2 - 4 : -(PIECE_R * 2 - 4);
        int maxVisible = 5;
        for (int i = 0; i < Math.min(count, maxVisible); i++) {
            drawPiece(g2, cx, startY + i * step, white);
        }
        if (count > maxVisible) {
            int labelY = startY + maxVisible * step;
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Georgia", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String s = "+" + (count - maxVisible);
            g2.drawString(s, cx - fm.stringWidth(s) / 2, labelY + (fromTop ? 6 : -2));
        }
    }

    private void drawPiece(Graphics2D g2, int cx, int cy, boolean white) {
        Color fill   = white ? C_W_PIECE : C_B_PIECE;
        Color border = white ? C_W_BORDER : C_B_BORDER;
        Color shine  = white ? new Color(255, 255, 255, 80) : new Color(255, 255, 255, 30);
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(cx - PIECE_R + 2, cy - PIECE_R + 2, PIECE_R * 2, PIECE_R * 2);
        g2.setColor(fill);
        g2.fillOval(cx - PIECE_R, cy - PIECE_R, PIECE_R * 2, PIECE_R * 2);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - PIECE_R, cy - PIECE_R, PIECE_R * 2, PIECE_R * 2);
        g2.setColor(shine);
        g2.fillOval(cx - PIECE_R / 2, cy - PIECE_R + 3, PIECE_R, PIECE_R / 2);
    }

    private void drawBarPieces(Graphics2D g2) {
        int barCX = LEFT_MARGIN + 6 * PT_W + BAR_W / 2;
        for (int i = 0; i < state.barWhite; i++)
            drawPiece(g2, barCX, BOARD_H / 2 + PIECE_R + 4 + i * (PIECE_R * 2 + 2), true);
        for (int i = 0; i < state.barBlack; i++)
            drawPiece(g2, barCX, BOARD_H / 2 - PIECE_R - 4 - i * (PIECE_R * 2 + 2), false);
    }

    private void drawHighlights(Graphics2D g2) {
        if (selectedPoint == null) return;
        drawPointHighlight(g2, selectedPoint, C_SELECTED);
        for (GameState move : validMoves) {
            if (move.lastMove != null && move.lastMove.from == selectedPoint)
                drawPointHighlight(g2, move.lastMove.to, C_VALID);
        }
    }

    private void drawPointHighlight(Graphics2D g2, int point, Color color) {
        if (point == 0) {
            int bearX = LEFT_MARGIN + BAR_W + 12 * PT_W;
            g2.setColor(color);
            g2.fillRect(bearX + 2, 2, BEAR_W - 4, BOARD_H - 4);
        } else {
            boolean isTop = (point >= 13);
            int cx = ptX[point];
            int baseY = isTop ? 0 : BOARD_H;
            int tipY  = isTop ? TRI_H : BOARD_H - TRI_H;
            int[] xs = { cx - PT_W / 2 + 2, cx + PT_W / 2 - 2, cx };
            int[] ys = { baseY, baseY, tipY };
            g2.setColor(color);
            g2.fillPolygon(xs, ys, 3);
        }
    }

    private void drawPointNumbers(Graphics2D g2) {
        g2.setFont(new Font("Consolas", Font.BOLD, 11));
        for (int p = 1; p <= 24; p++) {
            g2.setColor(C_TEXT);
            boolean isTop = (p >= 13);
            FontMetrics fm = g2.getFontMetrics();
            String label = String.valueOf(p);
            int textX = ptX[p] - fm.stringWidth(label) / 2;
            int textY = isTop ? BOARD_H - 6 : 14;
            g2.drawString(label, textX, textY);
        }
        g2.setFont(new Font("Georgia", Font.ITALIC, 10));
        g2.setColor(new Color(0xFF, 0xD7, 0x00, 150));
        g2.drawString("← Blanco (IA)", LEFT_MARGIN + 5, BOARD_H / 2 - 4);
        g2.setColor(new Color(0xAA, 0xAA, 0xAA, 150));
        g2.drawString("Negro →", LEFT_MARGIN + 5, BOARD_H / 2 + 14);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (state.turn != Player.BLACK) return;
        if (validMoves.isEmpty()) return;
        int clicked = getPointAt(e.getX(), e.getY());
        if (clicked == -1) return;

        if (selectedPoint == null) {
            if (hasBlackPiece(clicked)) { selectedPoint = clicked; repaint(); }
        } else {
            GameState chosen = findMove(selectedPoint, clicked);
            if (chosen != null) {
                selectedPoint = null;
                validMoves.clear();
                gui.applyHumanMove(chosen);
            } else if (hasBlackPiece(clicked)) {
                selectedPoint = clicked; repaint();
            } else {
                selectedPoint = null; repaint();
            }
        }
    }

    private boolean hasBlackPiece(int point) {
        if (point == 0) return state.barBlack > 0;
        return state.board[point - 1] < 0;
    }

    private GameState findMove(int from, int to) {
        for (GameState s : validMoves)
            if (s.lastMove != null && s.lastMove.from == from && s.lastMove.to == to)
                return s;
        return null;
    }

    private int getPointAt(int mx, int my) {
        int barX  = LEFT_MARGIN + 6 * PT_W;
        int bearX = LEFT_MARGIN + BAR_W + 12 * PT_W;
        if (mx >= bearX && mx <= bearX + BEAR_W) return 0;
        if (mx >= barX  && mx <= barX  + BAR_W)  return -1;
        for (int p = 1; p <= 24; p++) {
            int cx = ptX[p];
            if (mx >= cx - PT_W / 2 && mx <= cx + PT_W / 2) {
                boolean top = (p >= 13);
                if (top  && my <= TRI_H)           return p;
                if (!top && my >= BOARD_H - TRI_H) return p;
            }
        }
        return -1;
    }

    @Override public void mousePressed(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}
}
