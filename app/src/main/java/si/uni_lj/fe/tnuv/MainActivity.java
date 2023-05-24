/* RAZLAGE

- GUMB ZA NAZAJ V ORODNI VRSTICI izvede 4 funkcije in sicer najprej aktivnost v katero nameravaš iti izbriše, nato jo kreaira (onCreate), nato pa še štarta (onStart), zatem pa še izbriše (onDestroy) aktivnsot v kateri si bil. Če greš pa nazaj preko gumba za nazaj od telefona, se pa onCreate() ne izvede, ampak se izvede le onStart()


*/




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


    private Button addGameActivityButton;
    private MyDatabaseHelper dbHelper;
    private TableLayout gameTableLayout;
    private Button viewStatsButton;

    private ImageView imageMainWallpaper;    // za glavno ozadje aplikacije

    public static int mainActivityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Tag","[Main] {onCreate()}");


        imageMainWallpaper = findViewById(R.id.menu_image);
        imageMainWallpaper.setImageResource(R.drawable.rummy_tracker_wallpaper);


        dbHelper = new MyDatabaseHelper(this);

        addGameActivityButton = findViewById(R.id.btn_add_game_activity);
        addGameActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddGameActivity.class);
            startActivity(intent);
        });

        gameTableLayout = findViewById(R.id.table_layout);
        List<Game> gameList = dbHelper.getAllGames();

        for (Game game : gameList) {
            TableRow row = new TableRow(this);



            Button gameNameButton = new Button(this);
            gameNameButton.setText(game.getName());
            gameNameButton.setTextSize(18);  //set the text size in pixels
            gameNameButton.setBackgroundResource(R.drawable.game_buttons_style);
            gameNameButton.setTextColor(Color.WHITE);
            gameNameButton.setAllCaps(false);
            gameNameButton.setPadding(40, 0, 40, 0);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels; //od Huawei-ja je 1080
            double game_btn_screen_ratio = 0.65;


            TableRow.LayoutParams tableRowParams  = new TableRow.LayoutParams((int) Math.round(game_btn_screen_ratio * screenWidth),
                    TableRow.LayoutParams.WRAP_CONTENT);
            tableRowParams.height=130;



            int game_btn_left_margin = (int) Math.round((1-game_btn_screen_ratio)/2*screenWidth);
            tableRowParams.setMargins(game_btn_left_margin, 5, 0, 5);


            gameNameButton.setLayoutParams(tableRowParams);
            gameNameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, GameMenuActivity.class);   //Intent(kdo kliče, koga kliče)
                    intent.putExtra("message_key_1", game.getName());
                    intent.putExtra("message_key_2", R.id.table_layout);

                    startActivity(intent);

                }
            });
            row.addView(gameNameButton);





            gameTableLayout.addView(row);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Tag","[Main] {onStart()}");



    }







    @Override
    protected void onResume() {
        Log.d("Tag","[Main] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag","[Main] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag","[Main] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {  //NEVEM zakaj bi v tej situaciji/taki aktivnosti rabil to funkcijo
//        dbHelper.close();
        Log.d("Tag","[Main] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag","[Main] {onRestart()}");
        super.onRestart();
    }


}

