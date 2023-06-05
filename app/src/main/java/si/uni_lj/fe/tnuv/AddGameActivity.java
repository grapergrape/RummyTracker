package si.uni_lj.fe.tnuv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

        dbHelper = new MyDatabaseHelper(this);

        gameNameEditText = findViewById(R.id.game_name_edittext);
        insertButton = findViewById(R.id.insert_button);

        gameNameEditText.requestFocus();

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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //tale case MORAM NUJNO DEFINIRATI zato, da overwrite-am vgrajeno funkcijo za up action/go back button iz orodne vrstice (ta orodna vrstica je že vgrajeno v to mojo temo)
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
