package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dotnboxes.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameView extends View {

    private Paint paintDot;
    private Paint paintTouch;
    private Paint paintText;
    private Paint paintLine;
    private Paint paintBox;

    private int boxWidth;
    private int boxHeight;

    private int screenWith;
    private int screenHeight;
    private int screenWidthHalf;

    private int offsetY;
    private int offsetX;

    private float touchX;
    private float touchY;

    public GameView(Context context) {
        super(context);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        if (isInEditMode()) return;
        initializePaints();
    }

    private void initializePaints() {
        paintDot = new Paint();
        paintDot.setColor(Color.WHITE);
        paintDot.setStyle(Paint.Style.FILL);
        paintDot.setAntiAlias(true);

        paintTouch = new Paint();
        paintTouch.setColor(Color.RED);
        paintTouch.setStyle(Paint.Style.FILL);
        paintTouch.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#4444ff"));
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setStrokeWidth(10);
        paintLine.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setAntiAlias(true);
        paintText.setTextSize(30);
        paintText.setTextAlign(Paint.Align.CENTER);

        paintBox = new Paint();
        paintBox.setColor(Color.WHITE);
        paintBox.setStyle(Paint.Style.FILL);
        paintBox.setAntiAlias(true);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) return;

        drawBackground(canvas);
        drawConnectedLines(canvas);
        drawBoxes(canvas);
        drawDots(canvas);
        drawScores(canvas);
        drawDebugTouchPosition(canvas);
        drawDebugNaming(canvas);

        if (State.isGameOver) {
            drawFinishMessage(canvas);
        }
    }

    private static void drawBackground(@NonNull Canvas canvas) {
        canvas.drawColor(Theme.backgroundColor);
    }

    private void drawConnectedLines(@NonNull Canvas canvas) {
        for (Line line : State.lines) {
            drawLine(canvas, line);
        }
    }

    private void drawLine(Canvas canvas, Line line) {
        Position p1 = getPointPosition(line.i1, line.j1);
        Position p2 = getPointPosition(line.i2, line.j2);
        paintLine.setColor(getPlayerColor(line.playerIndex));
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paintLine);
    }

    private void drawBoxes(@NonNull Canvas canvas) {
        for (Box box : State.boxes) {
            paintBox.setColor(getPlayerColor(box.playerIndex));
            Position boxPosition = getPointPosition(box.i, box.j);
            canvas.drawCircle(boxPosition.x + (float) Theme.space / 2, boxPosition.y - (float) Theme.space / 2, 30, paintBox);
        }
    }

    private void drawDots(@NonNull Canvas canvas) {
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position point = getPointPosition(i, j);
                canvas.drawCircle(point.x, point.y, Theme.radius, paintDot);
            }
        }
    }

    private void drawScores(Canvas canvas) {
        drawPlayerScore(canvas, 1, screenWidthHalf - 100, 100);
        drawPlayerScore(canvas, 2, screenWidthHalf + 100, 100);
    }

    private void drawPlayerScore(@NonNull Canvas canvas, int playerIndex, int x, int y) {
        paintBox.setColor(getPlayerColor(playerIndex));
        canvas.drawCircle(x, y, 40, paintBox);
        canvas.drawText("" + getPlayerScore(playerIndex), x, y + 10, paintText);
        canvas.drawText(getPlayerName(playerIndex), x, y + 80, paintText);
    }

    private void drawDebugTouchPosition(@NonNull Canvas canvas) {
        if (!Debug.isDebugMode || !Debug.drawTouch) {
            return;
        }
        canvas.drawCircle(touchX, touchY, 10, paintTouch);
    }

    private void drawDebugNaming(Canvas canvas) {
        if (!Debug.isDebugMode || !Debug.drawDotName) {
            return;
        }
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                String name = i + "," + j;
                Position point = getPointPosition(i, j);
                canvas.drawText(name, point.x, point.y + 50, paintText);
            }
        }
    }

    private Position getPointPosition(int i, int j) {
        int x = offsetX + (i * Theme.space);
        int y = offsetY + ((Options.rows - 1 - j) * Theme.space);
        return new Position(x, y);
    }

    private void drawFinishMessage(@NonNull Canvas canvas) {
        canvas.drawText(getNameFinishMessage(), screenWidthHalf, getHeight() - 100, paintText);
    }

    private String getNameFinishMessage() {
        String message = "";
        if (getPlayerScore(1) == getPlayerScore(2)) {
            message = G.context.getString(R.string.gameDraw);
        } else if (getPlayerScore(1) > getPlayerScore(2)) {
            message = getPlayerName(1) + G.context.getString(R.string.playerWonGame);
        } else {
            message = getPlayerName(2) + G.context.getString(R.string.playerWonGame);
        }
        return message;
    }

    private int getPlayerColor(int playerIndex) {
        return Theme.playerColors[playerIndex - 1];
    }

    private String getPlayerName(int playerIndex) {
        return Options.playerNames[playerIndex - 1];
    }

    private int getPlayerIndex() {
        return State.isSide1 ? 1 : 2;
    }

    private int getPlayerScore(int playerIndex) {
        return State.playerScores[playerIndex - 1];
    }

    private void increasePlayerScore(int playerIndex) {
        State.playerScores[playerIndex - 1]++;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        boxWidth = (Options.cols - 1) * Theme.space;
        boxHeight = (Options.rows - 1) * Theme.space;

        screenWith = w - getPaddingLeft() - getPaddingRight();
        screenHeight = h - getPaddingTop() - getPaddingBottom();
        screenWidthHalf = screenWith / 2;

        offsetX = getPaddingLeft() + (screenWith - boxWidth) / 2;
        offsetY = getPaddingTop() + (screenHeight - boxHeight) / 2;

        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (State.isGameOver) return true;
        touchX = event.getX();
        touchY = event.getY();

        connectLine();
        refresh();
        return super.onTouchEvent(event);
    }

    private void connectLine() {
        ArrayList<Diff> diffs = getDiffsByOrder();

        Diff point1 = diffs.get(0);
        Diff point2 = diffs.get(1);

        Diff firstPoint;
        Diff secondPoint;

        Box box1;
        Box box2 = null;
        if (point1.i == point2.i) {
            //vertical
            if (point1.j < point2.j) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }
            box1 = new Box(firstPoint.i, firstPoint.j);
            if (firstPoint.i > 0) {
                box2 = new Box(firstPoint.i - 1, firstPoint.j);
            }
        } else {
            //horizontal
            if (point1.i < point2.i) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }
            box1 = new Box(firstPoint.i, firstPoint.j);
            if (firstPoint.j > 0) {
                box2 = new Box(firstPoint.i, firstPoint.j - 1);
            }
        }

        //if this line is already connected
        for (Line line : State.lines) {
            if (line.i1 == firstPoint.i && line.j1 == firstPoint.j && line.i2 == secondPoint.i && line.j2 == secondPoint.j) {
                return;
            }
        }

        //add line to list of connected lines
        Line line = new Line(firstPoint.i, firstPoint.j, secondPoint.i, secondPoint.j, getPlayerIndex());
        State.lines.add(line);

        //check if player get award
        boolean wonBox1 = checkBox(box1);
        boolean wonBox2 = false;

        if (box2 != null) {
            wonBox2 = checkBox(box2);
        }

        boolean mustPlayerNextPlayer = !wonBox1 && !wonBox2;

        //if switching side required
        if (mustPlayerNextPlayer) {
            State.isSide1 = !State.isSide1;
        }
    }

    private ArrayList<Diff> getDiffsByOrder() {
        ArrayList<Diff> diffs = new ArrayList<>();
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position position = getPointPosition(i, j);
                float diff = computeDiff(touchX, touchY, position.x, position.y);
                diffs.add(new Diff(i, j, diff));
            }
        }
        Collections.sort(diffs, new Comparator<Diff>() {
            @Override
            public int compare(Diff o1, Diff o2) {
                return o1.diff.compareTo(o2.diff);
            }
        });
        return diffs;
    }

    private float computeDiff(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public void resetGame() {
        State.playerScores[0] = 0;
        State.playerScores[1] = 0;

        Options.cols = 4;
        Options.rows = 4;
        Theme.space = 150;
        Theme.radius = 15;

        Debug.isDebugMode = false;
        State.isSide1 = true;

        State.isGameOver = false;

        State.lines.clear();
        State.boxes.clear();
        refresh();
    }

    private void refresh() {
        if (State.boxes.size() == (Options.cols - 1) * (Options.rows - 1)) {
            State.isGameOver = true;
        }
        invalidate();
    }

    private boolean checkBox(Box box) {
        int i = box.i;
        int j = box.j;

        boolean hasLeft = false;
        boolean hasRight = false;
        boolean hasTop = false;
        boolean hasBottom = false;

        for (Line line : State.lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j + 1) {
                hasLeft = true;
            }

            if (line.i1 == i + 1 && line.j1 == j && line.i2 == i + 1 && line.j2 == j + 1) {
                hasRight = true;
            }

            if (line.i1 == i && line.j1 == j + 1 && line.i2 == i + 1 && line.j2 == j + 1) {
                hasTop = true;
            }

            if (line.i1 == i && line.j1 == j && line.i2 == i + 1 && line.j2 == j) {
                hasBottom = true;
            }
        }

        boolean isFullConnected = hasLeft && hasRight && hasTop && hasBottom;
        if (isFullConnected) {
            box.playerIndex = getPlayerIndex();
            State.boxes.add(box);
            increasePlayerScore(box.playerIndex);
            return true;
        }
        return false;
    }

    private static class Position {
        public int x;
        public int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Diff {
        public int i;
        public int j;
        public Float diff;

        public Diff(int i, int j, float diff) {
            this.i = i;
            this.j = j;
            this.diff = diff;
        }
    }

    private static class Box {
        public int i;
        public int j;
        public int playerIndex;

        public Box(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private static class Line {
        public int i1;
        public int j1;
        public int i2;
        public int j2;
        public int playerIndex;

        public Line(int i1, int j1, int i2, int j2, int playerIndex) {
            this.i1 = i1;
            this.j1 = j1;
            this.i2 = i2;
            this.j2 = j2;
            this.playerIndex = playerIndex;
        }
    }

    public static class Theme {
        private static int[] playerColors = new int[]{Color.parseColor("#4444ff"), Color.parseColor("#ff4444")};
        private static int space = 150;
        private static int radius = 15;
        private static int backgroundColor = Color.parseColor("#222222");
    }

    public static class State {
        private static ArrayList<Line> lines = new ArrayList<>();
        private static ArrayList<Box> boxes = new ArrayList<>();
        private static int[] playerScores = new int[]{0, 0};
        private static boolean isGameOver = false;
        private static boolean isSide1 = true;
    }

    public static class Options {
        private static String[] playerNames = new String[]{"Player 1", "Player 2"};
        private static int cols = 4;
        private static int rows = 4;
    }

    public static class Debug {
        private static boolean isDebugMode = false;
        private static boolean drawTouch = false;
        private static boolean drawDotName = false;
    }
}
