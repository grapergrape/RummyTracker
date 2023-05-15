package si.uni_lj.fe.tnuv;

import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class BarChartView extends View {
    private static final int BAR_COLOR = Color.BLUE;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BAR_PADDING = 20;
    private static final int TEXT_SIZE = 20;

    private ArrayList<String> playerStatsList;

    public BarChartView(Context context) {
        super(context);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPlayerStatsList(ArrayList<String> playerStatsList) {
        this.playerStatsList = playerStatsList;
        invalidate(); // redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (playerStatsList == null || playerStatsList.size() == 0) {
            return;
        }

        // Find the maximum win count to scale the bars
        int maxWinCount = 0;
        for (String playerStats : playerStatsList) {
            int winCount = Integer.parseInt(playerStats.split(": ")[1]);
            maxWinCount = Math.max(maxWinCount, winCount);
        }

        // Define the dimensions and position of the chart
        int chartWidth = getWidth();
        int chartHeight = getHeight();
        int barWidth = (chartWidth - (BAR_PADDING * (playerStatsList.size() + 1))) / playerStatsList.size();
        int x = BAR_PADDING;

        // Draw the bars and player names
        Paint paint = new Paint();
        paint.setColor(BAR_COLOR);
        paint.setTextSize(TEXT_SIZE);
        paint.setTextAlign(Paint.Align.CENTER);

        for (String playerStats : playerStatsList) {
            // Get the player name and win count
            String[] tokens = playerStats.split(": ");
            String playerName = tokens[0];
            int winCount = Integer.parseInt(tokens[1]);

            // Calculate the height of the bar based on the win count
            int barHeight = (winCount * chartHeight) / maxWinCount;

            // Draw the bar
            RectF barRect = new RectF(x, chartHeight - barHeight, x + barWidth, chartHeight);
            canvas.drawRect(barRect, paint);

            // Draw the player name
            canvas.drawText(playerName, x + (barWidth / 2), chartHeight + TEXT_SIZE, paint);

            // Move to the next position
            x += barWidth + BAR_PADDING;
        }
    }
}

