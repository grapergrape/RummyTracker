//package si.uni_lj.fe.tnuv;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.List;
//import si.uni_lj.fe.tnuv.R;
//import si.uni_lj.fe.tnuv.database.Game;
//import si.uni_lj.fe.tnuv.database.MyDatabaseHelper;
//import si.uni_lj.fe.tnuv.database.Player;
//
//public class GameListActivity extends AppCompatActivity {
//
//    private TableLayout gameTableLayout;
//    private MyDatabaseHelper dbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.game_list_item);
//
//        dbHelper = new MyDatabaseHelper(this);
//        gameTableLayout = findViewById(R.id.table_layout);
//
//        List<Game> gameList = dbHelper.getAllGames();
//
//        for (Game game : gameList) {
//            TableRow row = new TableRow(this);
//
//            TextView gameNameView = new TextView(this);
//            String statusText = game.getStatus() == 0 ? "Active" : "Finished";
//            gameNameView.setText(game.getName() + " (" + statusText + ")");
//            gameNameView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
//
//            // Add an OnClickListener to the game name view to display players in the game
//            gameNameView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int gameId = dbHelper.getGameId(game.getName());
//                    displayPlayersInGame(gameId);
//                }
//            });
//
//            row.addView(gameNameView);
//
//            Button deleteButton = new Button(this);
//            deleteButton.setText("Delete");
//            deleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
//
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dbHelper.deleteGame(game.getName());
//                    gameTableLayout.removeView(row);
//                }
//            });
//
//            Button statusButton = new Button(this);
//            statusButton.setText("Change Status");
//            statusButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
//
//            statusButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dbHelper.changeStatus(game.getName());
//                    int newStatus = game.getStatus() == 0 ? 1 : 0;
//                    game.setStatus(newStatus);
//                    String newStatusText = newStatus == 0 ? "Active" : "Finished";
//                    gameNameView.setText(game.getName() + " (" + newStatusText + ")");
//                }
//            });
//
//            row.addView(deleteButton);
//            row.addView(statusButton);
//
//            gameTableLayout.addView(row);
//        }
//    }
//
//    // Display all players in the selected game
//    private void displayPlayersInGame(int gameId) {
//        Intent intent = new Intent(GameListActivity.this, PlayerListActivity.class);
//        List<Player> players = dbHelper.getPlayersInGame(gameId);
//        if (players.size() == 0) {
//            intent.putExtra("noPlayers", true);
//        } else {
//            intent.putExtra("playersList", (java.io.Serializable) players);
//        }
//        intent.putExtra("game_id", gameId); // Set the game ID as an extra
//        startActivity(intent);
//    }
//}
//
//
//
