package si.uni_lj.fe.tnuv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;

public class AddPlayerActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private List<String> playerList;
    private List<String> selectedPlayers;
    private ArrayAdapter<String> playerAdapter;
    private int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        dbHelper = new MyDatabaseHelper(this);
        selectedPlayers = new ArrayList<>();

        // Get the game ID from the intent extras
        gameId = getIntent().getIntExtra("game_id", -1);

        // Get the list of player nicknames from the Player table
        playerList = dbHelper.getAllPlayerNicknames();

        // Find the list view and create an adapter for it
        ListView playerListView = findViewById(R.id.player_list_view);
        playerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, playerList);
        playerListView.setAdapter(playerAdapter);
        playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Set the on item click listener for the list view to update the selected players list
        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gameId == -1) {
                    // Game id is invalid, show an error message
                    Toast.makeText(getApplicationContext(), "Please select a game first", Toast.LENGTH_SHORT).show();
                    return;
                }

                String playerNickname = playerList.get(position);
                if (selectedPlayers.contains(playerNickname)) {
                    selectedPlayers.remove(playerNickname);
                } else {
                    selectedPlayers.add(playerNickname);
                }
            }
        });


        // Find the button to add the selected players to the game and set its click listener
        Button addButton = findViewById(R.id.add_players_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlayers.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select at least one player", Toast.LENGTH_SHORT).show();
                } else {
                    for (String playerNickname : selectedPlayers) {
                        dbHelper.insertPlayerToGame(gameId, dbHelper.getPlayerIdByNickname(playerNickname));
                    }
                    Toast.makeText(getApplicationContext(), "Players added to game", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        // Find the button to insert a new player and set its click listener
        Button insertButton = findViewById(R.id.insert_player_button);
        insertButton.setVisibility(View.VISIBLE);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InsertPlayerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
