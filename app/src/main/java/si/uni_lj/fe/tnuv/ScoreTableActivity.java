package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected void onStart() {
        super.onStart();

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
                playerTextView.setGravity(Gravity.CENTER);
                playerTextView.setTextColor(Color.BLACK);
                playerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                playerTextView.setTypeface(null, Typeface.BOLD);
                playerInfoLayout.addView(playerTextView);

                Button scoreButton = new Button(this);
                scoreButton.setText("Insert Score");
                scoreButton.setTextSize(14);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(150), LinearLayout.LayoutParams.WRAP_CONTENT);
                scoreButton.setLayoutParams(params);
                scoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle score insertion here
                        showInsertScoreDialog(gameId, player.getId());
                    }
                });
                playerInfoLayout.addView(scoreButton);

                // Get the player's scores for the game
                List<HashMap<String, Object>> scores = dbHelper.getPlayerScoresForGame(player.getId(), gameId);

                // Display the player's scores or a message if no scores have been inserted yet
                if (scores.size() == 0) {
                    TextView messageTextView = new TextView(this);
                    messageTextView.setText("Please insert score.");
                    messageTextView.setTypeface(null, Typeface.ITALIC);
                    messageTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    playerInfoLayout.addView(messageTextView);
                } else {
                    for (HashMap<String, Object> score : scores) {
                        // Create a vertical layout to display the session's score and button
                        LinearLayout sessionLayout = new LinearLayout(this);
                        sessionLayout.setOrientation(LinearLayout.VERTICAL);

                        TextView sessionTextView = new TextView(this);
                        sessionTextView.setText(" ");
                        sessionTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        sessionLayout.addView(sessionTextView);

                        TextView scoreTextView = new TextView(this);
                        int scoreValue = (int) score.get("score");
                        scoreTextView.setText(Integer.toString(scoreValue));
                        scoreTextView.setTextSize(20);
                        scoreTextView.setGravity(Gravity.CENTER);

                        // Set the color of the score to red if it's 0
                        if (scoreValue == 0) {
                            scoreTextView.setTextColor(Color.RED);
                        }

                        sessionLayout.addView(scoreTextView);

                        // Set the onClickListener for the scoreTextView
                        scoreTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int scoreId = (int) score.get("score_id");
                                showDeleteScoreDialog(scoreId);
                            }
                        });

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

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void showDeleteScoreDialog(int scoreId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Action");
        builder.setMessage("Do you want to delete or edit this score?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showConfirmDeletionDialog(scoreId);
            }
        });

        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditScoreDialog(scoreId);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditScoreDialog(int scoreId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Score");
        builder.setMessage("Enter the new score:");

        // Create an input field for the new score
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the new score value from the input field
                String newScoreString = input.getText().toString();
                int newScore = Integer.parseInt(newScoreString);

                // Update the score in the database
                dbHelper.editScore(scoreId, newScore);

                // Refresh the score table
                refreshScoreTable();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConfirmDeletionDialog(int scoreId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this score?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the score from the database
                dbHelper.removeScore(scoreId);
                // Refresh the score table
                refreshScoreTable();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    // Method to show the dialog for inserting a score
    private void showInsertScoreDialog(int gameId, int playerId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the score");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3), new InputFilterMinMax("0", "299")});
        builder.setView(input);

        input.requestFocus(); //da je cursor v text boxu že takoj ob odprtju tega dialog boxa

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String scoreString = input.getText().toString();
                if (!scoreString.isEmpty()) {
                    int score = Integer.parseInt(scoreString);
                    dbHelper.insertGameScore(gameId, playerId, score); // Pass the consecutive tracker to the insertGameScore method
                    refreshScoreTable();
                } else {
                    Toast.makeText(ScoreTableActivity.this, "You haven't entered any number.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }




    // Method to refresh the score table after a change is made
    private void refreshScoreTable() {
        onStart(); // Re-fetch the data and redraw the score table
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
