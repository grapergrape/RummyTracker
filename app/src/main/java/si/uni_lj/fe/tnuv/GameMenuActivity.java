package si.uni_lj.fe.tnuv;

import androidx.appcompat.app.AppCompatActivity;

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


//        testIntVar = 5;
//        Log.d("Tag","{onCreate()} testIntVar: " + testIntVar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

        Intent intent_incoming = getIntent();
//        if(MainActivity.mainActivityChecker == 1) {  //To check-iranje z if-om sm mogu dodat zato, da sem rešil en bug, do katerega je prišlo, ker se ta funkcija onCreate() IZVEDE VSAKIČ, ko se odpre ta aktivnost in ne le prvič in nevem zakaj je to tako -> ZA TO je kriv gumb za nazaj v orodni vrstici, saj izvede 4 funkcije in sicer najprej aktivnost v katero nameravaš iti izbriše, nato jo kreaira (onCreate), nato pa še štarta (onStart), zatem pa še izbriše (onDestroy) aktivnsot v kateri si bil. Če greš pa nazaj preko gumba za nazaj od telefona, se pa onCreate() ne izvede, ampak se izvede le onStart()
//            game_name_string = intent_incoming.getStringExtra("message_key_1");
//            setTitle(game_name_string);
//            MainActivity.mainActivityChecker=0;
//        }
        game_name_string = intent_incoming.getStringExtra("message_key_1");
        setTitle(game_name_string);

//        Log.d("Tag","{onCreate()} game_name_string: " + game_name_string);

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
//        game_name_string = intent_incoming.getStringExtra("message_key_1");
//        Log.d("Tag","{onStart()} game_name_string: " + game_name_string);


//        Log.d("Tag","{onStart()} testIntVar: " + String.valueOf(testIntVar));



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
//                Log.d("Tag","{onStart()} WrtScrBtn: game_name_string: " + game_name_string);
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
                // Perform delete operation here
                dbHelper.deleteGame(game_name_string);
//            gameTableLayout.removeView(row);  //TEGA za brisanje trenutne igre iz sql databaze SPLOH NE RABIM  :) :) :)
                Intent intent_back = new Intent(GameMenuActivity.this, MainActivity.class);
                startActivity(intent_back);
                return true;
            case android.R.id.home: //tu je pa potrebno spredaj napisati android, ker se item z id-jem "R.id.home" ne nahaja v nobeni moji mapi, ampak (verjetno) v neki standardni android knjižnici al nekj podobnega
                                    //tale case MORAM NUJNO DEFINIRATI zato, da overwrite-am vgrajeno funkcijo za up action/go back button iz orodne vrstice (ta orodna vrstica je že vgrajeno v to mojo temo) (ne vem kako priti do te vgrajene/default funkcije za to, kaj naredi ta go back toolbar button
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