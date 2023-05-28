package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import si.uni_lj.fe.tnuv.AddPlayerActivity;
import si.uni_lj.fe.tnuv.R;
import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;

public class ScoreTableActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);
        Log.d("Tag", "[ScoreTable] {onCreate()}");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // For up action (go back) button in the toolbar/app bar/action bar
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Tag", "[ScoreTable] {onStart()}");

        dbHelper = new MyDatabaseHelper(this);

        // Get the game ID from the intent extras
        int gameId = getIntent().getIntExtra("game_id", -1);

        // Get the list of players for the game
        List<Player> playerList = dbHelper.getPlayersInGame(gameId);

        // Find the layout for adding players
        LinearLayout playerLayout = findViewById(R.id.score_table_linearlayout);
        HorizontalScrollView scrollView = findViewById(R.id.score_table_scrollview);
        scrollView.setHorizontalScrollBarEnabled(true);

        // If there are no players in the game, display a message
        if (playerList.isEmpty()) {
            TextView noPlayersTextView = new TextView(this);
            noPlayersTextView.setText("No players in the game, please add players.");
            playerLayout.addView(noPlayersTextView);
        } else {
            // Clear the existing player layout
            playerLayout.removeAllViews();

            // Add each player to the layout
            playerLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (Player player : playerList) {
                // Create a horizontal layout to display the player's nickname and scores
                LinearLayout playerInfoLayout = new LinearLayout(this);
                playerInfoLayout.setOrientation(LinearLayout.VERTICAL);

                TextView playerTextView = new TextView(this);
                playerTextView.setText(player.getNickname());
                playerTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                playerTextView.setTextColor(Color.BLACK);
                playerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                playerTextView.setTypeface(null, Typeface.BOLD);
                playerInfoLayout.addView(playerTextView);

                TextView scoreField = new TextView(this);
                scoreField.setText("Click to Insert Score");
                scoreField.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                scoreField.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle score insertion here
                        Intent intent = new Intent(ScoreTableActivity.this, InsertScoreActivity.class);
                        intent.putExtra("game_id", gameId);
                        intent.putExtra("player_id", player.getId());
                        intent.putExtra("consecutive_tracker", 1);
                        startActivity(intent);
                    }
                });
                playerInfoLayout.addView(scoreField);

                // Get the player's scores for the game
                List<HashMap<String, Object>> scores = dbHelper.getPlayerScoresForGame(player.getId(), gameId);

                // Display the player's scores or a message if no scores have been inserted yet
                if (scores.size() == 0) {
                    TextView messageTextView = new TextView(this);
                    messageTextView.setText("Please insert score for this player.");
                    messageTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    playerInfoLayout.addView(messageTextView);
                } else {
                    for (HashMap<String, Object> score : scores) {
                        // Create a vertical layout to display the session's score and button
                        LinearLayout sessionLayout = new LinearLayout(this);
                        sessionLayout.setOrientation(LinearLayout.VERTICAL);

                        TextView sessionTextView = new TextView(this);
                        sessionTextView.setText("Session " + score.get("consecutive_tracker") + ": ");
                        sessionTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        sessionLayout.addView(sessionTextView);

                        TextView scoreTextView = new TextView(this);
                        int scoreValue = (int) score.get("score");
                        scoreTextView.setText(Integer.toString(scoreValue));
                        scoreTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                        // Set the color of the score to red if it's 0
                        if (scoreValue == 0) {
                            scoreTextView.setTextColor(Color.RED);
                        }

                        sessionLayout.addView(scoreTextView);

                        playerInfoLayout.addView(sessionLayout);
                    }
                }

                playerLayout.addView(playerInfoLayout);
            }
        }

        Button addPlayerButton = findViewById(R.id.add_player_button);

        // Set the onClickListener for the button
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreTableActivity.this, AddPlayerActivity.class);
                intent.putExtra("game_id", gameId);
                startActivity(intent);
            }
        });
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //tale case MORAM NUJNO DEFINIRATI zato, da overwrite-am vgrajeno funkcijo za up action/go back button iz orodne vrstice (ta orodna vrstica je že vgrajeno v to mojo temo) (ne vem kako priti do te vgrajene/default funkcije za to, kaj naredi ta go back toolbar button
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d("Tag", "[ScoreTable] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag", "[ScoreTable] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag", "[ScoreTable] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("Tag", "[ScoreTable] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag", "[ScoreTable] {onRestart()}");
        super.onRestart();
    }

}
