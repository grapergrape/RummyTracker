package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;

public class GameMenuActivity extends AppCompatActivity {
    private ImageView imageMainWallpaper;    // za glavno ozadje aplikacije
    private MyDatabaseHelper dbHelper;
    private TableLayout gameTableLayout;
    private int table_layout_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        dbHelper = new MyDatabaseHelper(this);

        // ZA glavno ozadje aplikacije
        imageMainWallpaper = findViewById(R.id.game_menu_image);
        imageMainWallpaper.setImageResource(R.drawable.rummy_tracker_game_menu_stroke);

        Intent intent_incoming = getIntent();
        String game_name_string = intent_incoming.getStringExtra("message_key_1");
//        TextView textViewGameNameTitle = findViewById(R.id.game_name_title);
//        textViewGameNameTitle.setText(game_name_string);
        ((TextView) findViewById(R.id.game_name_title)).setText(game_name_string);


////        gameTableLayout = (TableLayout) intent.getCharExtra("message_key_2");
//        table_layout_id = intent.getIntExtra("message_key_2",0);
//        gameTableLayout = findViewById(table_layout_id);

        TableRow row = (TableRow) intent_incoming.getCharSequenceExtra("message_key_3");

        Button deleteButton = findViewById(R.id.game_menu_trash_icon);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteGame(game_name_string);
//                gameTableLayout.removeView(row);  //TEGA za brisanje SPLOH NE RABIM  :) :) :)

                Intent intent_back = new Intent(GameMenuActivity.this, MainActivity.class);
                startActivity(intent_back);
            }
        });

        Button writeScoreButton = findViewById(R.id.button_write_score);
        writeScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gameId = dbHelper.getGameId(game_name_string);
                displayPlayersInGame(gameId);

//                Intent intent_write_score = new Intent(GameMenuActivity.this, PlayerListActivity.class);   //Intent(kdo kliče, koga kliče)
//                intent_write_score.putExtra("message_key_4", )
//                startActivity(intent_write_score);
            }
        });

        Button leaderboardsButton = findViewById(R.id.button_leaderboards);
        leaderboardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_leaderboards = new Intent(GameMenuActivity.this, ViewStatsActivity.class);   //Intent(kdo kliče, koga kliče)
                startActivity(intent_leaderboards);
            }
        });
    }


    private void displayPlayersInGame(int gameId) {   // Display all players in the selected game
        Intent intent = new Intent(GameMenuActivity.this, PlayerListActivity.class);
        List<Player> players = dbHelper.getPlayersInGame(gameId);
        if (players.size() == 0) {
            intent.putExtra("noPlayers", true);
        } else {
            intent.putExtra("playersList", (java.io.Serializable) players);
        }
        intent.putExtra("game_id", gameId); // Set the game ID as an extra
        startActivity(intent);
    }
}