package si.uni_lj.fe.tnuv;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;

public class InsertScoreActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private int playerId;
    private int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_score);
        Log.d("Tag", "[InsertScore] {onCreate()}");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // For up action (go back) button in the toolbar

        dbHelper = new MyDatabaseHelper(this);

        // Get the player and game IDs from the intent extras
        playerId = getIntent().getIntExtra("player_id", -1);
        gameId = getIntent().getIntExtra("game_id", -1);

        // Find the EditText view for inputting the score
        EditText scoreEditText = findViewById(R.id.score_edittext);

        // Set a filter on the EditText to only allow integers as input
        scoreEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3), new InputFilterMinMax("0", "299")});

        // Find the submit button and set the onClickListener
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the score from the EditText view
                String scoreString = scoreEditText.getText().toString();

                // Check if the score is empty
                if (scoreString.isEmpty()) {
                    Toast.makeText(InsertScoreActivity.this, "Please enter a score", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse the score as an integer
                int score = Integer.parseInt(scoreString);

                // Check if the player and game IDs are valid
                if (playerId == -1 || gameId == -1) {
                    Toast.makeText(InsertScoreActivity.this, "Invalid player or game ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if any other player has a score of 0 for the session
                boolean isSessionZeroScored = dbHelper.isSessionZeroScored(gameId);
                if (score == 0) {
                    if (isSessionZeroScored) {
                        // Prompt the user to rewrite the score because someone else has a score of 0 for the session
                        Toast.makeText(InsertScoreActivity.this, "Another player already has a score of 0 for this session. Please rewrite the score.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                // Insert the score into the database
                dbHelper.insertGameScore(gameId, playerId, score);
                // Finish the activity
                finish();
            }
        });
    }



    @Override
    protected void onStart() {
        Log.d("Tag","[InsertScore] {onStart()}");
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
        Log.d("Tag","[InsertScore] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag","[InsertScore] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag","[InsertScore] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        dbHelper.close();
        Log.d("Tag","[InsertScore] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag","[InsertScore] {onRestart()}");
        super.onRestart();
    }
}

