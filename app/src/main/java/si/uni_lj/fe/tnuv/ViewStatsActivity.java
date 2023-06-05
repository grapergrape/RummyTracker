package si.uni_lj.fe.tnuv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;

public class ViewStatsActivity extends AppCompatActivity {
    private int gameId;
    private ListView scoreListView;
    private ScoreAdapter scoreAdapter;

    private MyDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner statsSpinner = findViewById(R.id.stats_spinner);
        scoreListView = findViewById(R.id.score_list_view);

        databaseHelper = new MyDatabaseHelper(this);
        // Retrieve the gameId from the intent extras
        Intent intent = getIntent();
        gameId = intent.getIntExtra("gameId", -1);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.stats_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statsSpinner.setAdapter(spinnerAdapter);

        statsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                retrieveStats(selectedOption, gameId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void retrieveStats(String selectedOption, int gameId) {
        List<String> statsList;

        if (selectedOption.equals("Top 10 Worst Scores in This Game")) {
            statsList = databaseHelper.getTop10HighestScoreInstancesForGame(gameId);
        } else if (selectedOption.equals("Players Sorted by Wins in This Game")) {
            statsList = databaseHelper.getPlayersSortedByWinsForGame(gameId);
        } else if (selectedOption.equals("Players Sorted by Average Scores in This Game")) {
            statsList = databaseHelper.getPlayersSortedByAverageScoreForGame(gameId);
        } else if (selectedOption.equals("Top 10 Worst Scores All Time")) {
            statsList = databaseHelper.getTop10HighestScoreInstances();
        } else if (selectedOption.equals("Players Sorted by Wins All Time")) {
            statsList = databaseHelper.getPlayersSortedByWins();
        } else if (selectedOption.equals("Players Sorted by Average Scores All Time")) {
            statsList = databaseHelper.getPlayersSortedByAverageScore();
        } else {
            statsList = new ArrayList<>(); // Default empty list
        }

        // Create and set the adapter to display the data
        scoreAdapter = new ScoreAdapter(this, statsList);
        scoreListView.setAdapter(scoreAdapter);
    }

    // Custom adapter class for displaying the score instances
    private class ScoreAdapter extends ArrayAdapter<String> {

        public ScoreAdapter(Context context, List<String> statsList) {
            super(context, 0, statsList);
        }

        // Modify the getView() method to display the stats in the table view item
        // You can customize this method to format the stats as desired
        // For example, split the string to display different parts in separate TextViews
        // You can also modify the layout file (score_item.xml) accordingly
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_item, parent, false);
            }

            String stats = getItem(position);

            // Set the stats in the table view item
            TextView statsTextView = convertView.findViewById(R.id.stats_text_view);
            statsTextView.setText(stats);
            statsTextView.setTextSize(19);

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
