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
                playerTextView.setGravity(Gravity.CENTER);
//                playerTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));  //tega ne sme bit, drugače gravity center ne dela
                playerTextView.setTextColor(Color.BLACK);
                playerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                playerTextView.setTypeface(null, Typeface.BOLD);
                playerInfoLayout.addView(playerTextView);

                Button scoreButton = new Button(this);
                scoreButton.setText("Insert Score");
//                scoreButton.setTextColor(Color.WHITE);
//                scoreButton.setTextColor(Color.BLACK);
                scoreButton.setTextSize(14);
//                scoreButton.setBackgroundColor(getResources().getColor(R.color.lighter_red));  //piše da je getColor() depricated
//                scoreButton.setBackgroundColor(ContextCompat.getColor(this, R.color.lighter_red));
//                scoreButton.setBackgroundColor(Color.parseColor("#f06e65"));
//                scoreButton.setBackgroundColor(Color.GRAY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(150), LinearLayout.LayoutParams.WRAP_CONTENT);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
//                    messageTextView.setText("Please insert score for this player."); //je predolg tekst in pol je velka luknja v tabeli
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
//                        scoreTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));   //tega ne sme bit, drugače gravity center ne dela

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
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    // Method to show the dialog for editing a score
    private void showEditScoreDialog(int gameId, int playerId, int consecutiveTracker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Score");
        builder.setMessage("Enter the new score:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3), new InputFilterMinMax("0", "299")});
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newScoreString = input.getText().toString();
                if (!newScoreString.isEmpty()) {
                    int newScore = Integer.parseInt(newScoreString);
                    boolean isSessionZeroScored = dbHelper.isSessionZeroScored(gameId);

                    if (newScore == 0 && isSessionZeroScored) {
                        // Prompt the user to rewrite the score because someone else has a score of 0 for the session
                        Toast.makeText(ScoreTableActivity.this, "Another player already has a score of 0 for this session. Please rewrite the score.", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.editGameScore(gameId, playerId, consecutiveTracker, newScore);
                        refreshScoreTable();
                    }
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

    // Method to show the dialog for confirming score deletion
    private void showConfirmDeleteDialog(int gameId, int playerId, int consecutiveTracker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this score?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.removeGameScore(gameId, playerId, consecutiveTracker);
                refreshScoreTable();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
//        builder.setTitle("Insert Score");
//        builder.setMessage("Enter the score:");
        builder.setTitle("Enter the score");


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3), new InputFilterMinMax("0", "299")});
        builder.setView(input);

        // Show the keyboard
        input.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  //to dela le če to dodam v dodaten gumb
//        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);  //to dela le če to dodam v dodaten gumb
//        UIUtil.showKeyboard(this, input);  //to tud ne dela
//        UIUtil.showKeyboardInDialog(, input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String scoreString = input.getText().toString();
                if (!scoreString.isEmpty()) {
                    int score = Integer.parseInt(scoreString);
                    boolean isSessionZeroScored = dbHelper.isSessionZeroScored(gameId);
                    boolean isMaxConsecutiveTrackerFullyScored = dbHelper.isMaxConsecutiveTrackerFullyScored(gameId);
                    if (score == 0) {
                        if (isSessionZeroScored){
                        // Prompt the user to rewrite the score because someone else has a score of 0 for the session
                            Toast.makeText(ScoreTableActivity.this, "Another player already has a score of 0 for this session. Please rewrite the score.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!isMaxConsecutiveTrackerFullyScored){
                            // Prompt the user to rewrite the score because someone else has a score of 0 for the session
                            Toast.makeText(ScoreTableActivity.this, "Please finish inserting the previous session first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    dbHelper.insertGameScore(gameId, playerId, score);
                    refreshScoreTable();
                }else{
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
