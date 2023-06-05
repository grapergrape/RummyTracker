package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.List;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;

public class GameMenuActivity extends AppCompatActivity {
    private ImageView imageMainWallpaper;
    private MyDatabaseHelper dbHelper;
    public static String game_name_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent_incoming = getIntent();
        game_name_string = intent_incoming.getStringExtra("message_key_1");
        setTitle(game_name_string);

        Button ButtonWriteScore = findViewById(R.id.button_write_score);
        Button ButtonLeaderboards = findViewById(R.id.button_leaderboards);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int height_image_game_menu = screenWidth * 1583/1964;

        int height_button_game_menu = (screenHeight - height_image_game_menu - 3*80)/2; //80px = okoli 10dp

        ButtonWriteScore.getLayoutParams().height = height_button_game_menu;
        ButtonLeaderboards.getLayoutParams().height = height_button_game_menu;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button writeScoreButton = findViewById(R.id.button_write_score);
        Button leaderboardsButton = findViewById(R.id.button_leaderboards);
        imageMainWallpaper = findViewById(R.id.game_menu_image);
        imageMainWallpaper.setImageResource(R.drawable.rummy_tracker_game_menu_stroke);

        dbHelper = new MyDatabaseHelper(this);

        Intent intent_incoming = getIntent();
        TableRow row = (TableRow) intent_incoming.getCharSequenceExtra("message_key_3");
        writeScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("Tag","{onStart()} WrtScrBtn: game_name_string: " + game_name_string);
                int gameId = dbHelper.getGameId(game_name_string);
                displayPlayersInGame(gameId);

            }
        });


        leaderboardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gameId = dbHelper.getGameId(game_name_string);

                Intent intent_leaderboards = new Intent(GameMenuActivity.this, ViewStatsActivity.class);
                intent_leaderboards.putExtra("gameId", gameId); // Add the gameId as an extra
                startActivity(intent_leaderboards);
            }
        });


    }

    private void displayPlayersInGame(int gameId) {   // Display all players in the selected game
        Intent intent = new Intent(GameMenuActivity.this, ScoreTableActivity.class);
        List<Player> players = dbHelper.getPlayersInGame(gameId);
        if (players.size() == 0) {
            intent.putExtra("noPlayers", true);
        } else {
            intent.putExtra("playersList", (java.io.Serializable) players);
        }
        intent.putExtra("game_id", gameId); // Set the game ID as an extra
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //za prikaz trash/delete button-a v toolbar-u
        getMenuInflater().inflate(R.menu.menu_main, menu); //(izbere menu xml file v mapi res/menu/menu_main.xml)
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:  //tukaj pred R.id ne potrebuješ napisati še "android", ker je "R.id.action_delete" id od enega item-a (trash icon button) iz xml definiranega menuja (sem ga sam definiral/ustvaril)

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete this game?");

                // Set positive button and its click listener
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Yes"
                        // Perform the desired action
                        // Perform delete operation here
                        int gameId = dbHelper.getGameId(game_name_string);
                        dbHelper.deleteGameAndScores(gameId);
//            gameTableLayout.removeView(row);  //TEGA za brisanje trenutne igre iz sql databaze SPLOH NE RABIM  :) :) :)
                        Intent intent_back = new Intent(GameMenuActivity.this, MainActivity.class);
                        startActivity(intent_back);

                    }
                });

                // Set negative button and its click listener
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No"
                        // Perform any necessary action or simply dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}