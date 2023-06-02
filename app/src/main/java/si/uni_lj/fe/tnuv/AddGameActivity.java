package si.uni_lj.fe.tnuv;


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;

//public class AddGameActivity extends AppCompatActivity implements View.OnClickListener {
public class AddGameActivity extends AppCompatActivity {

    private EditText gameNameEditText;
    private Button insertButton;
    //    private Button allGamesButton;
    private MyDatabaseHelper dbHelper;

//    private ImageView imageMainWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        Log.d("Tag","[AddGame] {onCreate()}");

//        Toolbar toolbarAddGame = (Toolbar) findViewById(R.id.toolbar_add_game);
//        setSupportActionBar(toolbarAddGame);  //ta ukaz instant crasha celo aplikacijo, ker je v Manifestu izbrana tema (Theme.RummyTracker), KI IMA ŽE app/tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

        dbHelper = new MyDatabaseHelper(this);

        gameNameEditText = findViewById(R.id.game_name_edittext);
        insertButton = findViewById(R.id.insert_button);
//        allGamesButton = findViewById(R.id.all_games_button);

//        Button tst_btn = findViewById(R.id.test_button);
////        tst_btn.setOnClickListener(this);
//        tst_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                // Show the keyboard
////                gameNameEditText.requestFocus();
////                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////                imm.showSoftInput(gameNameEditText, InputMethodManager.SHOW_IMPLICIT);
//            }
//        });

        // Show the keyboard
        gameNameEditText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  //to dela le če to dodam v dodaten gumb
//        imm.showSoftInput(gameNameEditText, InputMethodManager.SHOW_IMPLICIT);  //to dela le če to dodam v dodaten gumb
//        UIUtil.showKeyboard(this, gameNameEditText);  //to tud ne dela

        insertButton.setOnClickListener(v -> {
            String gameName = gameNameEditText.getText().toString().trim();

            if (gameName.isEmpty()) {
                Toast.makeText(AddGameActivity.this, "Please enter a game name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.isGameNameUnique(gameName)) {
                Toast.makeText(AddGameActivity.this, "A game with that name already exists. Please choose a different name.", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.insertGame(gameName);

            Toast.makeText(AddGameActivity.this, "Game inserted into database", Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(AddGameActivity.this, MainActivity.class);
            startActivity(intent);

        });

//        allGamesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, GameListActivity.class);
//                startActivity(intent);
//            }
//        });


    }

//    @Override
//    protected void onResume() {  // Perform any actions you want to execute when the activity is starting or becoming visible
//        super.onResume();
//        // Your code here
//        try {
//            Thread.sleep(2000); // Sleep for 2000 milliseconds (2 seconds)
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        // Show the keyboard
//        gameNameEditText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(gameNameEditText, InputMethodManager.SHOW_IMPLICIT);
//
//    }


    @Override
    protected void onStart() {
        Log.d("Tag","[AddGame] {onStart()}");
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //tale case MORAM NUJNO DEFINIRATI zato, da overwrite-am vgrajeno funkcijo za up action/go back button iz orodne vrstice (ta orodna vrstica je že vgrajeno v to mojo temo) (ne vem kako priti do te vgrajene/default funkcije za to, kaj naredi ta go back toolbar button
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d("Tag","[AddGame] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag","[AddGame] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag","[AddGame] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("Tag","[AddGame] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag","[AddGame] {onRestart()}");
        super.onRestart();
    }


}
