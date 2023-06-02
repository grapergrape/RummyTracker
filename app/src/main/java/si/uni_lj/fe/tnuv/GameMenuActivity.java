package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.List;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;

public class GameMenuActivity extends AppCompatActivity {
    private ImageView imageMainWallpaper;    // za glavno ozadje aplikacije
    private MyDatabaseHelper dbHelper;
    private TableLayout gameTableLayout;
    private int table_layout_id;
    public static String game_name_string;
    public int game_menu_image_height;

    public static int testIntVar = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);
        Log.d("Tag","[GameMenu] {onCreate()}");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

        Intent intent_incoming = getIntent();
        game_name_string = intent_incoming.getStringExtra("message_key_1");
        setTitle(game_name_string);

    }//onCreate



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Tag","[GameMenu] {onStart()}");

//        String game_name_string;

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
                Intent intent_leaderboards = new Intent(GameMenuActivity.this, ViewStatsActivity.class);   //Intent(kdo kliče, koga kliče)
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






//                // Perform delete operation here
//                int gameId = dbHelper.getGameId(game_name_string);
//                dbHelper.deleteGameAndScores(gameId);
////            gameTableLayout.removeView(row);  //TEGA za brisanje trenutne igre iz sql databaze SPLOH NE RABIM  :) :) :)
//                Intent intent_back = new Intent(GameMenuActivity.this, MainActivity.class);
//                startActivity(intent_back);
                return true;
            case android.R.id.home: //tu je pa potrebno spredaj napisati android, ker se item z id-jem "R.id.home" ne nahaja v nobeni moji mapi, ampak (verjetno) v neki standardni android knjižnici al nekj podobnega
                                    //tale case MORAM NUJNO DEFINIRATI zato, da overwrite-am vgrajeno funkcijo za up action/go back button iz orodne vrstice (ta orodna vrstica je že vgrajeno v to mojo temo) (ne vem kako priti do te vgrajene/default funkcije za to, kaj naredi ta go back toolbar button)
                this.finish();
                return true;
        }

//        int id = item.getItemId();
//        if (id == R.id.action_delete) {   //s tem poveš kaj naj trash icon button v toolbar-u naredi
//            // Perform delete operation here
//            dbHelper.deleteGame(game_name_string);
////            gameTableLayout.removeView(row);  //TEGA za brisanje SPLOH NE RABIM  :) :) :)
//            Intent intent_back = new Intent(GameMenuActivity.this, MainActivity.class);
//            startActivity(intent_back);
//            return true;
//        }
//        if (id == android.R.id.home) {   //s tem poveš kaj naj trash icon button v toolbar-u naredi
//            this.finish();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onResume() {
        Log.d("Tag","[GameMenu] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag","[GameMenu] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag","[GameMenu] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {  //NEVEM zakaj bi v tej situaciji/taki aktivnosti rabil to funkcijo
        Log.d("Tag","[GameMenu] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag","[GameMenu] {onRestart()}");
        super.onRestart();
    }
}