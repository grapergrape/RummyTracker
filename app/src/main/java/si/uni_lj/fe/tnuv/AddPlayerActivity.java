package si.uni_lj.fe.tnuv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels; //od Huawei-ja je 1080
        double game_btn_screen_ratio = 0.8;

        Button buttonInsertPlayer = findViewById(R.id.insert_player_button);
        Button buttonAddPlayer = findViewById(R.id.add_players_button);
        Button buttonRemovePlayer = findViewById(R.id.remove_players_button);
        Button buttonDeletePlayer = findViewById(R.id.delete_player_button);

        buttonInsertPlayer.getLayoutParams().width = (int) Math.round(screenWidth*game_btn_screen_ratio);
        buttonAddPlayer.getLayoutParams().width = (int) Math.round(screenWidth * game_btn_screen_ratio);
        buttonRemovePlayer.getLayoutParams().width = (int) Math.round(screenWidth * game_btn_screen_ratio);
        buttonDeletePlayer.getLayoutParams().width = (int) Math.round(screenWidth * game_btn_screen_ratio);

    }


    @Override
    protected void onStart() {
        super.onStart();

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

        // Find the button to remove the selected players from the game and set its click listener
        Button removeButton = findViewById(R.id.remove_players_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlayers.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select at least one player to remove", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmRemovingPlayer(selectedPlayers, gameId);
                }
            }
        });

        // Find the button to delete a player and set its click listener
        Button deleteButton = findViewById(R.id.delete_player_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlayers.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select at least one player to delete", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmDeletingPlayer(selectedPlayers);
                }
            }
        });
    }

    private void showConfirmRemovingPlayer(List<String> selectedPlayers, int scoreId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Removal");
        builder.setMessage("Are you sure you want to remove this player from this game?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.removePlayersFromGame(selectedPlayers, gameId);
                Toast.makeText(getApplicationContext(), "Selected players removed from the game", Toast.LENGTH_SHORT).show();
                finish();
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

    private void showConfirmDeletingPlayer(List<String> selectedPlayers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this player from all games?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deletePlayersAndScores(selectedPlayers);
                Toast.makeText(getApplicationContext(), "Players and their scores deleted", Toast.LENGTH_SHORT).show();
                finish();
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
