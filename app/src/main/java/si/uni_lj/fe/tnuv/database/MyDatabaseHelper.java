package si.uni_lj.fe.tnuv.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_PLAYERS = "players";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_PLAYER_NICKNAME = "nickname";


    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to store game names and IDs
        String createTableSql = "CREATE TABLE games ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "status INTEGER DEFAULT 1)";
        db.execSQL(createTableSql);

        // Create a table to store player information
        createTableSql = "CREATE TABLE " + TABLE_PLAYERS + " ("
                + COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PLAYER_NICKNAME + " TEXT)";
        db.execSQL(createTableSql);

        // Create a junction table to link games and players
        createTableSql = "CREATE TABLE game_players ("
                + "game_id INTEGER,"
                + "player_id INTEGER,"
                + "PRIMARY KEY (game_id, player_id),"
                + "FOREIGN KEY (game_id) REFERENCES games(id),"
                + "FOREIGN KEY (player_id) REFERENCES " + TABLE_PLAYERS + "(player_id))";
        db.execSQL(createTableSql);

        // Create a table to store game scores
        createTableSql = "CREATE TABLE game_scores ("
                + "score_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "identifier TEXT,"
                + "score INTEGER,"
                + "game_player_id INTEGER,"
                + "consecutive_tracker INTEGER,"
                + "FOREIGN KEY (game_player_id) REFERENCES game_players(rowid))";
        db.execSQL(createTableSql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If you need to upgrade the database schema, modify this method
    }
    //Method that checks if a game with a given name already exists
    public boolean isGameNameUnique(String gameName) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { "id" };
        String selection = "name=?";
        String[] selectionArgs = { gameName };

        Cursor cursor = db.query("games", columns, selection, selectionArgs, null, null, null);

        boolean isUnique = (cursor.getCount() == 0);

        cursor.close();
        db.close();

        return isUnique;
    }



    public int getGameId(String gameName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = { "id" };
        String selection = "name=?";
        String[] selectionArgs = { gameName };
        Cursor cursor = db.query("games", projection, selection, selectionArgs, null, null, null);
        int gameId = -1;
        if (cursor.moveToFirst()) {
            gameId = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return gameId;
    }

    public void insertGame(String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("games", null, values);

        db.close();
    }

    public List<Game> getAllGames() {
        List<Game> gameList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {"id", "name", "status"};
        Cursor cursor = db.query("games", projection, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int gameId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String gameName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int gameStatus = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
            Game game = new Game(gameId, gameName, gameStatus);
            gameList.add(game);
        }

        cursor.close();
        db.close();

        return gameList;
    }


    public void deleteGame(String name) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "name=?";
        String[] whereArgs = {name};
        db.delete("games", whereClause, whereArgs);

        db.close();
    }

    public void changeStatus(String name) {
        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {"status"};
        String selection = "name=?";
        String[] selectionArgs = {name};
        Cursor cursor = db.query("games", projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
            ContentValues values = new ContentValues();
            values.put("status", (status == 0) ? 1 : 0);

            String whereClause = "name=?";
            String[] whereArgs = {name};
            db.update("games", values, whereClause, whereArgs);
        }

        cursor.close();
        db.close();
    }

    private int getPlayerId(String playerName, SQLiteDatabase db) {
        String[] projection = {"player_id"};
        String selection = "name=?";
        String[] selectionArgs = {playerName};
        Cursor cursor = db.query(TABLE_PLAYERS, projection, selection, selectionArgs, null, null, null);

        int playerId = -1;
        if (cursor.moveToFirst()) {
            playerId = cursor.getInt(cursor.getColumnIndexOrThrow("player_id"));
        }

        cursor.close();

        return playerId;
    }

    private int getGameId(String gameName, SQLiteDatabase db) {
        String[] projection = {"id"};
        String selection = "name=?";
        String[] selectionArgs = {gameName};
        Cursor cursor = db.query("games", projection, selection, selectionArgs, null, null, null);

        int gameId = -1;
        if (cursor.moveToFirst()) {
            gameId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }

        cursor.close();

        return gameId;
    }
    public void insertPlayer(Player player) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_PLAYER_NICKNAME, player.getNickname());

        db.insert(MyDatabaseHelper.TABLE_PLAYERS, null, values);
        db.close();
    }


    public void updatePlayer(Player player) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_PLAYER_NICKNAME, player.getNickname());


        String selection = MyDatabaseHelper.COLUMN_PLAYER_ID + "=?";
        String[] selectionArgs = {String.valueOf(player.getId())};

        db.update(MyDatabaseHelper.TABLE_PLAYERS, values, selection, selectionArgs);
        db.close();
    }

    public void deletePlayer(Player player) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = MyDatabaseHelper.COLUMN_PLAYER_ID + "=?";
        String[] selectionArgs = {String.valueOf(player.getId())};

        db.delete(MyDatabaseHelper.TABLE_PLAYERS, selection, selectionArgs);
        db.close();
    }
    public void insertPlayerToGame(int gameID, int playerID) {
        SQLiteDatabase db = getWritableDatabase();

        // Insert the game-player relationship
        ContentValues values = new ContentValues();
        values.put("game_id", gameID);
        values.put("player_id", playerID);
        db.insert("game_players", null, values);

        db.close();
    }

    public List<Player> getPlayersInGame(int gameId) {
        List<Player> playersList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + TABLE_PLAYERS + ".*" +
                " FROM " + TABLE_PLAYERS +
                " INNER JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID + " = game_players." + "player_id" +
                " WHERE game_players.game_id = ?";

        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(gameId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_ID));
                String nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_NICKNAME));


                Player player = new Player(id, nickname);
                playersList.add(player);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playersList;
    }
    //RETURNS A LIST OF ALL PLAYERS: their ID and nickname as a list within a list so not really usable
    public List<Player> getAllPlayers() {
        List<Player> playerList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM players", null);

        if (cursor.moveToFirst()) {
            do {
                Player player = new Player(cursor.getInt(0), cursor.getString(1));
                playerList.add(player);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playerList;
    }
    //Returns True or false depending wether a player exists in the database
    public boolean playerExists(String nickname) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                MyDatabaseHelper.TABLE_PLAYERS,
                new String[]{MyDatabaseHelper.COLUMN_PLAYER_NICKNAME},
                MyDatabaseHelper.COLUMN_PLAYER_NICKNAME + " = ?",
                new String[]{nickname},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    public List<String> getAllPlayerNicknames() {
        List<String> playerNicknames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nickname FROM players", null);

        if (cursor.moveToFirst()) {
            do {
                playerNicknames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playerNicknames;
    }

    public int getPlayerIdByNickname(String nickname) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {MyDatabaseHelper.COLUMN_PLAYER_ID};
        String selection = MyDatabaseHelper.COLUMN_PLAYER_NICKNAME + " = ?";
        String[] selectionArgs = {nickname};

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_PLAYERS, projection, selection, selectionArgs, null, null, null);

        int playerId = -1;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            playerId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return playerId;
    }

    public void insertScore(int gamePlayerId, int score) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the current consecutive tracker value for this game and player
        int consecutiveTracker = getCurrentConsecutiveTracker(db, gamePlayerId);

        // Insert the new score with the current consecutive tracker value
        ContentValues values = new ContentValues();
        values.put("game_player_id", gamePlayerId);
        values.put("score", score);
        values.put("consecutive_tracker", consecutiveTracker);
        db.insert("game_scores", null, values);

        // Increment the consecutive tracker value for this game and player
        incrementConsecutiveTracker(db, gamePlayerId);
    }

    private int getCurrentConsecutiveTracker(SQLiteDatabase db, int gamePlayerId) {
        int consecutiveTracker = 1;
        String[] columns = {"consecutive_tracker"};
        String selection = "game_player_id = ?";
        String[] selectionArgs = {String.valueOf(gamePlayerId)};
        String orderBy = "consecutive_tracker DESC";
        Cursor cursor = db.query("game_scores", columns, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            consecutiveTracker = cursor.getInt(cursor.getColumnIndex("consecutive_tracker")) + 1;
        }
        cursor.close();
        return consecutiveTracker;
    }

    private void incrementConsecutiveTracker(SQLiteDatabase db, int gamePlayerId) {
        int consecutiveTracker = getCurrentConsecutiveTracker(db, gamePlayerId);
        ContentValues values = new ContentValues();
        values.put("consecutive_tracker", consecutiveTracker);
        String whereClause = "game_player_id = ?";
        String[] whereArgs = {String.valueOf(gamePlayerId)};
        db.update("game_scores", values, whereClause, whereArgs);
    }


    public int getPlayerScoreForGame(int gameId, int playerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT gs.score FROM game_scores gs " +
                "INNER JOIN game_players gp ON gs.game_player_id = gp.rowid " +
                "WHERE gp.game_id = ? AND gp.player_id = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(gameId), String.valueOf(playerId)});
        int score = -1; // default value if score is not found
        if (cursor.moveToFirst()) {
            score = cursor.getInt(cursor.getColumnIndex("score"));
        }
        cursor.close();
        return score;
    }



    public void insertGameScore(int gameId, int playerId, int score) {
        SQLiteDatabase db = getWritableDatabase();

        // First, find the game_player_id for this game and player
        String findGamePlayerSql = "SELECT rowid FROM game_players WHERE game_id = ? AND player_id = ?";
        String[] findGamePlayerArgs = {String.valueOf(gameId), String.valueOf(playerId)};
        Cursor cursor = db.rawQuery(findGamePlayerSql, findGamePlayerArgs);
        int gamePlayerId = -1;
        if (cursor.moveToNext()) {
            gamePlayerId = cursor.getInt(0);
        }
        cursor.close();

        // If the game_player_id was not found, do not insert the score
        if (gamePlayerId == -1) {
            return;
        }

        // Insert the game score record
        ContentValues values = new ContentValues();
        values.put("identifier", UUID.randomUUID().toString());
        values.put("score", score);
        values.put("game_player_id", gamePlayerId);
        values.put("consecutive_tracker", getConsecutiveTracker(gameId, playerId) + 1);
        db.insert("game_scores", null, values);

        // Check if the score is zero and, if so, print out a message
        if (score == 0) {
            String findPlayerSql = "SELECT nickname FROM players WHERE player_id = ?";
            String[] findPlayerArgs = {String.valueOf(playerId)};
            cursor = db.rawQuery(findPlayerSql, findPlayerArgs);
            String nickname = "";
            if (cursor.moveToNext()) {
                nickname = cursor.getString(0);
            }
            cursor.close();
            Log.d("RUMMY", nickname + " won the round of rummy!");
        }
    }

    private int getConsecutiveTracker(int gameId, int playerId) {
        SQLiteDatabase db = getReadableDatabase();

        int consecutiveTracker = 0;

        // First, find the game_player_id for this game and player
        String findGamePlayerSql = "SELECT rowid FROM game_players WHERE game_id = ? AND player_id = ?";
        String[] findGamePlayerArgs = {String.valueOf(gameId), String.valueOf(playerId)};
        Cursor cursor = db.rawQuery(findGamePlayerSql, findGamePlayerArgs);
        int gamePlayerId = -1;
        if (cursor.moveToNext()) {
            gamePlayerId = cursor.getInt(0);
        }
        cursor.close();

        // If the game_player_id was not found, return 0
        if (gamePlayerId == -1) {
            return consecutiveTracker;
        }

        // Find the latest consecutive tracker value for this player in this game
        String findTrackerSql = "SELECT consecutive_tracker FROM game_scores WHERE game_player_id = ? ORDER BY score_id DESC LIMIT 1";
        String[] findTrackerArgs = {String.valueOf(gamePlayerId)};
        cursor = db.rawQuery(findTrackerSql, findTrackerArgs);
        if (cursor.moveToNext()) {
            consecutiveTracker = cursor.getInt(0);
        }
        cursor.close();


        return consecutiveTracker;
    }

    public Player getPlayer(int playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_PLAYER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(playerId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        Player player = null;
        if (cursor.moveToFirst()) {
            String nickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
            player = new Player(playerId, nickname);
        }

        cursor.close();
        db.close();

        return player;
    }

}


