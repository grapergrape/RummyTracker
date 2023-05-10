package si.uni_lj.fe.tnuv;

import android.os.Bundle;
import android.text.InputFilter;
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

        dbHelper = new MyDatabaseHelper(this);

        // Get the player and game IDs from the intent extras
        playerId = getIntent().getIntExtra("player_id", -1);
        gameId = getIntent().getIntExtra("game_id", -1);

        // Find the EditText view for inputting the score
        EditText scoreEditText = findViewById(R.id.score_edittext);

        // Set a filter on the EditText to only allow integers as input
        scoreEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2), new InputFilterMinMax("0", "99") });

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

                // Insert the score into the database
                dbHelper.insertGameScore(gameId, playerId, score);
                // Finish the activity
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //dbHelper.close();
    }
}

