package si.uni_lj.fe.tnuv;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;

public class AverageScoresActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new MyDatabaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_average_scores);

        // Get a reference to the TextView to display the player stats
        TextView playerStatsTextView = findViewById(R.id.player_stats_text_view);

        // Get the player stats sorted by average score
        ArrayList<String> playerStatsList = dbHelper.getPlayersSortedByAverageScore();

        // Display the player stats
        StringBuilder playerStatsStringBuilder = new StringBuilder();
        for (String playerStats : playerStatsList) {
            playerStatsStringBuilder.append(playerStats).append("\n");
        }
        playerStatsTextView.setText(playerStatsStringBuilder.toString());
    }
}
