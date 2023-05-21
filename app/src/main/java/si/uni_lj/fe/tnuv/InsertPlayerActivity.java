package si.uni_lj.fe.tnuv;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
import si.uni_lj.fe.tnuv.database.Player;

public class InsertPlayerActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private EditText nicknameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_player);
        Log.d("Tag","[InsertPlayer] {onCreate()}");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

        dbHelper = new MyDatabaseHelper(this);
        nicknameEditText = findViewById(R.id.nickname_edit_text);

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nicknameEditText.getText().toString().trim();

                if (nickname.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a nickname", Toast.LENGTH_SHORT).show();
                } else if (dbHelper.playerExists(nickname)) {
                    Toast.makeText(getApplicationContext(), "Player already exists", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.insertPlayer(new Player(0, nickname));
                    Toast.makeText(getApplicationContext(), "Player added", Toast.LENGTH_SHORT).show();

                    // Return to AddPlayerActivity
                    finish();

                }
            }
        });
    }


    @Override
    protected void onStart() {
        Log.d("Tag","[InsertPlayer] {onStart()}");
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
        Log.d("Tag","[InsertPlayer] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag","[InsertPlayer] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag","[InsertPlayer] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        dbHelper.close();
        Log.d("Tag","[InsertPlayer] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag","[InsertPlayer] {onRestart()}");
        super.onRestart();
    }
}

