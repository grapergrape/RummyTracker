package si.uni_lj.fe.tnuv;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;
public class PlayerListActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        dbHelper = new MyDatabaseHelper(this);

        // Get the game ID from the intent extras
        int gameId = getIntent().getIntExtra("game_id", -1);

        // Get the list of players for the game
        List<Player> playerList = dbHelper.getPlayersInGame(gameId);

        // Find the layout for adding players
        LinearLayout playerLayout = findViewById(R.id.players_list_linearlayout);

        // If there are no players in the game, display a message
        if (playerList.isEmpty()) {
            TextView noPlayersTextView = new TextView(this);
            noPlayersTextView.setText("No players in the game, please add players.");
            playerLayout.addView(noPlayersTextView);
        } else {
            // Add each player to the layout
            playerLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (Player player : playerList) {
                // Create a horizontal layout to display the player's nickname and scores
                LinearLayout playerInfoLayout = new LinearLayout(this);
                playerInfoLayout.setOrientation(LinearLayout.VERTICAL);

                TextView playerTextView = new TextView(this);
                playerTextView.setText(player.getNickname());
                playerTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                playerInfoLayout.addView(playerTextView);

                Button insertScoreButton = new Button(this);
                insertScoreButton.setText("Insert Score");
                insertScoreButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                insertScoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PlayerListActivity.this, InsertScoreActivity.class);
                        intent.putExtra("game_id", gameId);
                        intent.putExtra("player_id", player.getId());
                        intent.putExtra("consecutive_tracker", 1);
                        startActivity(intent);
                    }
                });
                playerInfoLayout.addView(insertScoreButton);

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

        Button addPlayerButton = new Button(this);
        addPlayerButton.setText("Add Player");
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLayoutParams.gravity = Gravity.BOTTOM;
        addPlayerButton.setLayoutParams(buttonLayoutParams);

        // Set the onClickListener for the button
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerListActivity.this, AddPlayerActivity.class);
                intent.putExtra("game_id", gameId);
                startActivity(intent);
            }
        });


        // Add the button to the layout
        playerLayout.addView(addPlayerButton);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


