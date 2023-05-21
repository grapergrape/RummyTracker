package si.uni_lj.fe.tnuv;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
        Log.d("Tag","[AverageScores] {onCreate()}");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

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


    @Override
    protected void onStart() {
        Log.d("Tag","[AverageScores] {onStart()}");
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //tale case MORAM NUJNO DEFINIRATI zato, da overwrite-am vgrajeno funkcijo za up action/go back button iz orodne vrstice (ta orodna vrstica je že vgrajeno v to mojo temo) (ne vem kako priti do te vgrajene/default funkcije za to, kaj naredi ta go back toolbar button
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d("Tag","[AverageScores] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag","[AverageScores] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag","[AverageScores] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("Tag","[AverageScores] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag","[AverageScores] {onRestart()}");
        super.onRestart();
    }

}
