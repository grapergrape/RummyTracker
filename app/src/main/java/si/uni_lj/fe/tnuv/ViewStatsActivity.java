package si.uni_lj.fe.tnuv;

import android.content.Intent;
import android.os.Bundle;
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
}
