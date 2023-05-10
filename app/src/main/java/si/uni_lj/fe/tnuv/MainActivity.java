package si.uni_lj.fe.tnuv;


import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText gameNameEditText;
    private Button insertButton;
    private Button allGamesButton;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDatabaseHelper(this);

        gameNameEditText = findViewById(R.id.game_name_edittext);
        insertButton = findViewById(R.id.insert_button);
        allGamesButton = findViewById(R.id.all_games_button);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameName = gameNameEditText.getText().toString().trim();

                if (gameName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a game name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!dbHelper.isGameNameUnique(gameName)) {
                    Toast.makeText(MainActivity.this, "A game with that name already exists. Please choose a different name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelper.insertGame(gameName);

                Toast.makeText(MainActivity.this, "Game inserted into database", Toast.LENGTH_SHORT).show();
            }
        });

        allGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameListActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
