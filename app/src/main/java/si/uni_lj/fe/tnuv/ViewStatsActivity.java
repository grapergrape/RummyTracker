package si.uni_lj.fe.tnuv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ViewStatsActivity extends AppCompatActivity {

    private Button viewAverageScoresButton;
    private Button mostWinsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);
        Log.d("Tag","[ViewStats] {onCreate()}");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Za up action (go back) button v orodni vrstici (toolbar/app bar/action bar)

        viewAverageScoresButton = findViewById(R.id.view_average_scores_button);
        mostWinsButton = findViewById(R.id.most_wins_button);

        viewAverageScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewStatsActivity.this, AverageScoresActivity.class);
                startActivity(intent);
            }
        });

        mostWinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewStatsActivity.this, MostWinsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        Log.d("Tag","[ViewStats] {onStart()}");
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
        Log.d("Tag","[ViewStats] {onResume()}");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Tag","[ViewStats] {onPause()}");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Tag","[ViewStats] {onStop()}");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        dbHelper.close();
        Log.d("Tag","[ViewStats] {onDestroy()}");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("Tag","[ViewStats] {onRestart()}");
        super.onRestart();
    }
}
