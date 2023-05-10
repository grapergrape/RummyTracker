package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

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
            for (Player player : playerList) {
                TextView playerTextView = new TextView(this);
                playerTextView.setText(player.getNickname());
                playerLayout.addView(playerTextView);
            }
        }

        Button addPlayerButton = new Button(this);
        addPlayerButton.setText("Add Player");
        addPlayerButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

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
        dbHelper.close();
    }
}

