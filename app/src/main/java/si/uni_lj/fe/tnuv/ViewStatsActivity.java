package si.uni_lj.fe.tnuv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;

public class ViewStatsActivity extends AppCompatActivity {

    private Button viewAverageScoresButton;
    private Button mostWinsButton;
    private ListView scoreListView;
    private ScoreAdapter scoreAdapter;

    private MyDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);
        Log.d("Tag", "[ViewStats] {onCreate()}");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewAverageScoresButton = findViewById(R.id.view_average_scores_button);
        mostWinsButton = findViewById(R.id.most_wins_button);
        scoreListView = findViewById(R.id.score_list_view);

        databaseHelper = new MyDatabaseHelper(this);

        viewAverageScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewStatsActivity.this, AverageScoresActivity.class);
                startActivity(intent);
            }
        });

        mostWinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewStatsActivity.this, MostWinsActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve the top ten highest score instances
        List<MyDatabaseHelper.ScoreInstance> scoreInstances = databaseHelper.getTop10HighestScoreInstances();

        // Create and set the adapter to display the data
        scoreAdapter = new ScoreAdapter(this, scoreInstances);
        scoreListView.setAdapter(scoreAdapter);
    }

    // Custom adapter class for displaying the score instances
    private class ScoreAdapter extends ArrayAdapter<MyDatabaseHelper.ScoreInstance> {

        public ScoreAdapter(Context context, List<MyDatabaseHelper.ScoreInstance> scoreInstances) {
            super(context, 0, scoreInstances);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_item, parent, false);
            }

            // Get the current score instance
            MyDatabaseHelper.ScoreInstance scoreInstance = getItem(position);

            // Set the player nickname and score in the table view item
            TextView nicknameTextView = convertView.findViewById(R.id.nickname_text_view);
            TextView scoreTextView = convertView.findViewById(R.id.score_text_view);

            if (position == 0) {
                TextView wallOfShameTextView = convertView.findViewById(R.id.wall_of_shame_text_view);
                wallOfShameTextView.setVisibility(View.VISIBLE);
                TextView playerNameTextView = convertView.findViewById(R.id.player_name_text_view);
                playerNameTextView.setVisibility(View.VISIBLE);
            } else {
                TextView wallOfShameTextView = convertView.findViewById(R.id.wall_of_shame_text_view);
                wallOfShameTextView.setVisibility(View.GONE);
                TextView playerNameTextView = convertView.findViewById(R.id.player_name_text_view);
                playerNameTextView.setVisibility(View.GONE);
            }
            TextView playerNameTextView = convertView.findViewById(R.id.player_name_text_view);
            playerNameTextView.setVisibility(View.GONE);
            nicknameTextView.setText(scoreInstance.getNickname());
            scoreTextView.setText(String.valueOf(scoreInstance.getScore()));

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

    @Override
    protected void onStart() {
        Log.d("Tag", "[ViewStats] {onStart()}");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("Tag", "[ViewStats] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag", "[ViewStats] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag", "[ViewStats] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("Tag", "[ViewStats] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag", "[ViewStats] {onRestart()}");
        super.onRestart();
    }
}
