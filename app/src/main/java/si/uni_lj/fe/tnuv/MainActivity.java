package si.uni_lj.fe.tnuv;


import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import si.uni_lj.fe.tnuv.database.Game;
import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;

public class MainActivity extends AppCompatActivity {

//    private EditText gameNameEditText;
//    private Button insertButton;
//    private Button allGamesButton;
    private Button addGameActivityButton;
    private MyDatabaseHelper dbHelper;
    private TableLayout gameTableLayout;
    private Button viewStatsButton;

    private ImageView imageMainWallpaper;    // za glavno ozadje aplikacije

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ZA glavno ozadje aplikacije
        imageMainWallpaper = findViewById(R.id.menu_image);
        imageMainWallpaper.setImageResource(R.drawable.rummy_tracker_wallpaper);


        dbHelper = new MyDatabaseHelper(this);

        addGameActivityButton = findViewById(R.id.btn_add_game_activity);
        addGameActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddGameActivity.class);
            startActivity(intent);
        });

        gameTableLayout = findViewById(R.id.table_layout);
//        Log.d("Tag", "gameTableLayout: " + gameTableLayout);
        List<Game> gameList = dbHelper.getAllGames();

        for (Game game : gameList) {
            TableRow row = new TableRow(this);

//            TextView gameNameView = new TextView(this);
////            gameNameView.setGravity(Gravity.CENTER);
//            String statusText = game.getStatus() == 0 ? "Active" : "Finished";
////            gameNameView.setText(game.getName() + " (" + statusText + ")");
//            gameNameView.setText(game.getName());
//            gameNameView.setTextSize(20); // Set the text size in pixels
////            gameNameView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
//            TableRow.LayoutParams tableRowParams  = new TableRow.LayoutParams();
////            tableRowParams.column = 2;  // Set the view to be in the second column
////            tableRowParams.span = 2;    // Span the view across two columns
////            tableRowParams.weight = 1;  // Assign a weight to the view
//            tableRowParams.height=130;
////            tableRowParams.width=params.WRAP_CONTENT;
//            tableRowParams.gravity = Gravity.CENTER;  // Set the gravity of the view within its cell
//            gameNameView.setLayoutParams(tableRowParams);
//
//            // Add an OnClickListener to the game name view to display players in the game
//            gameNameView.setOnClickListener(v -> {
//                int gameId = dbHelper.getGameId(game.getName());
//                displayPlayersInGame(gameId);
//            });
//            row.addView(gameNameView);

            Button gameNameButton = new Button(this);
            gameNameButton.setText(game.getName());
            gameNameButton.setTextSize(15);  //set the text size in pixels
            gameNameButton.setBackgroundResource(R.drawable.game_buttons_style);
            gameNameButton.setTextColor(Color.WHITE);
            gameNameButton.setAllCaps(false);
            gameNameButton.setPadding(40, 0, 40, 0);

            TableRow.LayoutParams tableRowParams  = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            tableRowParams.height=130;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            int screenWidth = displayMetrics.widthPixels; //je 1080
//            Log.d("Tag", "screenWidth: " + String.valueOf(screenWidth));
//            tableRowParams.setMargins(200, 5, 0, 5); // Set bottom margin of 16 pixels
            tableRowParams.setMargins(250, 5, 0, 5);

            tableRowParams.gravity = Gravity.CENTER;  //set the gravity of the view within its cell
//            tableRowParams.column=3;
            gameNameButton.setLayoutParams(tableRowParams);
            gameNameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, GameMenuActivity.class);   //Intent(kdo kliče, koga kliče)
                    intent.putExtra("message_key_1", game.getName());
                    intent.putExtra("message_key_2", R.id.table_layout);
//                    intent.putExtra("message_key_3", (CharSequence) row);  //TOLE POVZORČI IMMEDIATE CRASHDOWN
//                    Log.d("Tag", "row: " + row);
                    startActivity(intent);

//                    int gameId = dbHelper.getGameId(game.getName());
//                    displayPlayersInGame(gameId);
                }
            });
            row.addView(gameNameButton);


//            Button deleteButton = new Button(this);
//            deleteButton.setText("D");
//            deleteButton.setLayoutParams(new TableRow.LayoutParams());
//            deleteButton.setOnClickListener(v -> {
//                dbHelper.deleteGame(game.getName());
//                gameTableLayout.removeView(row);
//            });
//            row.addView(deleteButton);




//            Button statusButton = new Button(this);
//            statusButton.setText("Change Status");
//            statusButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

//            statusButton.setOnClickListener(v -> {
//                dbHelper.changeStatus(game.getName());
//                int newStatus = game.getStatus() == 0 ? 1 : 0;
//                game.setStatus(newStatus);
//                String newStatusText = newStatus == 0 ? "Active" : "Finished";
//                gameNameView.setText(game.getName() + " (" + newStatusText + ")");
//            });

//            row.addView(statusButton);

            gameTableLayout.addView(row);
        }
    }




//    // Display all players in the selected game
//    private void displayPlayersInGame(int gameId) {
//        Intent intent = new Intent(MainActivity.this, PlayerListActivity.class);
//        List<Player> players = dbHelper.getPlayersInGame(gameId);
//        if (players.size() == 0) {
//            intent.putExtra("noPlayers", true);
//        } else {
//            intent.putExtra("playersList", (java.io.Serializable) players);
//        }
//        intent.putExtra("game_id", gameId); // Set the game ID as an extra
//        startActivity(intent);
//    }





    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }



//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        dbHelper = new MyDatabaseHelper(this);
//
//        gameNameEditText = findViewById(R.id.game_name_edittext);
//        insertButton = findViewById(R.id.insert_button);
//        allGamesButton = findViewById(R.id.all_games_button);
//        viewStatsButton = findViewById(R.id.stats_button);
//
//        insertButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String gameName = gameNameEditText.getText().toString().trim();
//
//                if (gameName.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Please enter a game name", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (!dbHelper.isGameNameUnique(gameName)) {
//                    Toast.makeText(MainActivity.this, "A game with that name already exists. Please choose a different name.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                dbHelper.insertGame(gameName);
//
//                Toast.makeText(MainActivity.this, "Game inserted into database", Toast.LENGTH_SHORT).show();
//            }
//        });
//
////        allGamesButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(MainActivity.this, GameListActivity.class);
////                startActivity(intent);
////            }
////        });
//
//        viewStatsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ViewStatsActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        dbHelper.close();
//        super.onDestroy();
//    }


}

