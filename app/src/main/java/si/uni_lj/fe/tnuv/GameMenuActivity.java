package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
    public String game_name_string;
    public int game_menu_image_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

        Button writeScoreButton = findViewById(R.id.button_write_score);
        Button leaderboardsButton = findViewById(R.id.button_leaderboards);
        imageMainWallpaper = findViewById(R.id.game_menu_image);
        imageMainWallpaper.setImageResource(R.drawable.rummy_tracker_game_menu_stroke);


        dbHelper = new MyDatabaseHelper(this);


        Intent intent_incoming = getIntent();
        game_name_string = intent_incoming.getStringExtra("message_key_1");
////        TextView textViewGameNameTitle = findViewById(R.id.game_name_title);
////        textViewGameNameTitle.setText(game_name_string);
//        ((TextView) findViewById(R.id.game_name_title)).setText(game_name_string);
        setTitle(game_name_string);



////        gameTableLayout = (TableLayout) intent.getCharExtra("message_key_2");
//        table_layout_id = intent.getIntExtra("message_key_2",0);
//        gameTableLayout = findViewById(table_layout_id);

        TableRow row = (TableRow) intent_incoming.getCharSequenceExtra("message_key_3");

//        Button deleteButton = findViewById(R.id.game_menu_trash_icon);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dbHelper.deleteGame(game_name_string);
////                gameTableLayout.removeView(row);  //TEGA za brisanje SPLOH NE RABIM  :) :) :)
//
//                Intent intent_back = new Intent(GameMenuActivity.this, MainActivity.class);
//                startActivity(intent_back);
//            }
//        });


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


        leaderboardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_leaderboards = new Intent(GameMenuActivity.this, ViewStatsActivity.class);   //Intent(kdo kliče, koga kliče)
                startActivity(intent_leaderboards);
            }
        });


//        //Za izračun in nastavitev pravilnih dimenzij obeh gumbov v game menu-ju (se prilagajajo na katerikoli telefon)
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenHeight = displayMetrics.heightPixels; //od Huawei-ja je 2139
////        Log.d("Tag", "screenHeight: " + String.valueOf(screenHeight));
////        int game_menu_image_height = imageMainWallpaper.getHeight();
//        int toolbar_height = getSupportActionBar().getHeight();
//
//        imageMainWallpaper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int game_menu_image_height = imageMainWallpaper.getHeight();
//                // Use the height value as needed
//                // Remove the listener to avoid multiple callbacks
//                imageMainWallpaper.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });
//
////        getSupportActionBar().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
////            @Override
////            public void onGlobalLayout() {
////                int game_menu_image_height = imageMainWallpaper.getHeight();
////                // Use the height value as needed
////                // Remove the listener to avoid multiple callbacks
////                imageMainWallpaper.getViewTreeObserver().removeOnGlobalLayoutListener(this);
////            }
////        });
//
//        int height_of_one_game_menu_button = (int) Math.round(screenHeight - toolbar_height - game_menu_image_height - 3*10)/2;
//        Log.d("Tag", "screenHeight: " + String.valueOf(screenHeight));
//        Log.d("Tag", "game_menu_image_height: " + String.valueOf(game_menu_image_height));
//        Log.d("Tag", "toolbar_height: " + String.valueOf(toolbar_height));
//        Log.d("Tag", "height_of_one_game_menu_button: " + String.valueOf(height_of_one_game_menu_button));
//
//
//        ViewGroup.LayoutParams writeScoreBtnparams = writeScoreButton.getLayoutParams();
//        writeScoreBtnparams.height = height_of_one_game_menu_button;
//        writeScoreButton.setLayoutParams(writeScoreBtnparams);
////
////        ViewGroup.LayoutParams params2 = leaderboardsButton.getLayoutParams();
////        params2.height = height_of_one_game_menu_button;
////        leaderboardsButton.setLayoutParams(params2);

    }//onCreate


//    @Override
//    protected void onStart() {
//        super.onStart();
//
////        Button writeScoreButton = findViewById(R.id.button_write_score);
////        Button leaderboardsButton = findViewById(R.id.button_leaderboards);
////        imageMainWallpaper = findViewById(R.id.game_menu_image);
//
//
//    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            // Perform delete operation here
            dbHelper.deleteGame(game_name_string);
//            gameTableLayout.removeView(row);  //TEGA za brisanje SPLOH NE RABIM  :) :) :)
            Intent intent_back = new Intent(GameMenuActivity.this, MainActivity.class);
            startActivity(intent_back);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}