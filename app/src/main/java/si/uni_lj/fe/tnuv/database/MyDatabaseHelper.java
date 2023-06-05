package si.uni_lj.fe.tnuv.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_PLAYERS = "players";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_PLAYER_NICKNAME = "nickname";
    private static final String TABLE_GAME_SCORES = "game_scores";
    private static final String COLUMN_SCORE_ID = "score_id";
    private static final String COLUMN_IDENTIFIER = "identifier";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_GAME_PLAYER_ID = "game_player_id";
    private static final String COLUMN_CONSECUTIVE_TRACKER = "consecutive_tracker";


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

    public int getConsecutiveTracker(int gameId, int playerId) {
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

    public void removeScore(int scoreId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_SCORE_ID + " = ?";
        String[] whereArgs = {String.valueOf(scoreId)};
        db.delete(TABLE_GAME_SCORES, whereClause, whereArgs);
    }
    public String isSessionZeroScored(int gameId, int playerId, int consecutiveTracker) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to check if any other player in the game has a score of 0 for the given consecutive tracker
        String query = "SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME +
                " FROM " + TABLE_PLAYERS +
                " INNER JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID + " = game_players.player_id" +
                " INNER JOIN game_scores ON game_players.rowid = game_scores.game_player_id" +
                " WHERE game_scores.consecutive_tracker = " + consecutiveTracker +
                " AND game_scores.score = 0" +
                " AND game_players.game_id = " + gameId +
                " AND game_players.player_id != " + playerId;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            // Retrieve the nickname of the other player
            String otherPlayerNickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
            cursor.close();
            return otherPlayerNickname;
        } else {
            cursor.close();
            return null;
        }
    }

    public String getPlayerNickname(int playerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_PLAYER_NICKNAME};
        String selection = COLUMN_PLAYER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(playerId)};

        Cursor cursor = db.query(TABLE_PLAYERS, columns, selection, selectionArgs, null, null, null);

        String nickname = null;
        if (cursor.moveToFirst()) {
            nickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
        }

        cursor.close();
        return nickname;
    }
    public void removePlayersFromGame(List<String> playerNicknames, int gameId) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the player IDs based on the provided nicknames
        String playerIdsQuery = "SELECT " + COLUMN_PLAYER_ID + " FROM " + TABLE_PLAYERS +
                " WHERE " + COLUMN_PLAYER_NICKNAME + " IN ('" + TextUtils.join("','", playerNicknames) + "')";
        Cursor cursor = db.rawQuery(playerIdsQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int playerId = cursor.getInt(cursor.getColumnIndex(COLUMN_PLAYER_ID));

                // Delete the game player entry
                db.delete("game_players", "game_id = ? AND player_id = ?", new String[]{String.valueOf(gameId), String.valueOf(playerId)});

                // Delete the game scores for the specific game and player
                db.delete("game_scores", "game_player_id IN " +
                        "(SELECT rowid FROM game_players WHERE game_id = ? AND player_id = ?)", new String[]{String.valueOf(gameId), String.valueOf(playerId)});
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
    }


    // Method that returns a list of the 10 highest scores in the game across all games
    public ArrayList<String> getTop10HighestScoreInstances() {
        ArrayList<String> topScoresList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME
                + ", game_scores.score FROM " + TABLE_PLAYERS
                + " JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID
                + " = game_players.player_id JOIN game_scores ON game_players.rowid"
                + " = game_scores.game_player_id ORDER BY game_scores.score DESC LIMIT 10", null);

        if (cursor.moveToFirst()) {
            do {
                String playerNickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
                topScoresList.add(playerNickname + ": " + score);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return topScoresList;
    }

    //This method uses an SQL query that counts how many times each player has won (i.e. had a score of 0) in the game_scores table, and then sorts the results in descending order by the win count. The method returns a list of strings, where each string represents a player's nickname and win count.
    public ArrayList<String> getPlayersSortedByWins() {
        ArrayList<String> playerStatsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME
                + ", COUNT(CASE WHEN game_scores.score = 0 THEN 1 ELSE null END) as win_count FROM " + TABLE_PLAYERS
                + " JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID
                + " = game_players.player_id JOIN game_scores ON game_players.rowid"
                + " = game_scores.game_player_id GROUP BY " + TABLE_PLAYERS + "."
                + COLUMN_PLAYER_ID + " ORDER BY win_count DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String playerNickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
                int winCount = cursor.getInt(cursor.getColumnIndex("win_count"));
                playerStatsList.add(playerNickname + ": " + winCount);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playerStatsList;
    }

    //Method that returns a list of players by their average score whre the lower the score the better
    public ArrayList<String> getPlayersSortedByAverageScore() {
        ArrayList<String> playerStatsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME
                + ", AVG(game_scores.score) as average_score FROM " + TABLE_PLAYERS
                + " JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID
                + " = game_players.player_id JOIN game_scores ON game_players.rowid"
                + " = game_scores.game_player_id GROUP BY " + TABLE_PLAYERS + "."
                + COLUMN_PLAYER_ID + " ORDER BY average_score ASC", null);

        if (cursor.moveToFirst()) {
            do {
                String playerNickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
                double averageScore = cursor.getDouble(cursor.getColumnIndex("average_score"));
//                playerStatsList.add(playerNickname + ": " + averageScore);
                playerStatsList.add(playerNickname + ": " + String.format("%.2f", averageScore));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playerStatsList;
    }
    // Method that returns a list of the 10 highest scores in the game for a specific gameId
    public ArrayList<String> getTop10HighestScoreInstancesForGame(int gameId) {
        ArrayList<String> topScoresList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME
                + ", game_scores.score FROM " + TABLE_PLAYERS
                + " JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID
                + " = game_players.player_id JOIN game_scores ON game_players.rowid"
                + " = game_scores.game_player_id WHERE game_players.game_id = ?"
                + " ORDER BY game_scores.score DESC LIMIT 10", new String[]{String.valueOf(gameId)});

        if (cursor.moveToFirst()) {
            do {
                String playerNickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
                topScoresList.add(playerNickname + ": " + score);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return topScoresList;
    }

    // Method that returns a list of players sorted by their win count for a specific gameId
    public ArrayList<String> getPlayersSortedByWinsForGame(int gameId) {
        ArrayList<String> playerStatsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME
                + ", COUNT(CASE WHEN game_scores.score = 0 THEN 1 ELSE null END) as win_count FROM " + TABLE_PLAYERS
                + " JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID
                + " = game_players.player_id JOIN game_scores ON game_players.rowid"
                + " = game_scores.game_player_id WHERE game_players.game_id = ?"
                + " GROUP BY " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID + " ORDER BY win_count DESC", new String[]{String.valueOf(gameId)});

        if (cursor.moveToFirst()) {
            do {
                String playerNickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
                int winCount = cursor.getInt(cursor.getColumnIndex("win_count"));
                playerStatsList.add(playerNickname + ": " + winCount);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playerStatsList;
    }

    // Method that returns a list of players sorted by their average score for a specific gameId
    public ArrayList<String> getPlayersSortedByAverageScoreForGame(int gameId) {
        ArrayList<String> playerStatsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME
                + ", AVG(game_scores.score) as average_score FROM " + TABLE_PLAYERS
                + " JOIN game_players ON " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID
                + " = game_players.player_id JOIN game_scores ON game_players.rowid"
                + " = game_scores.game_player_id WHERE game_players.game_id = ?"
                + " GROUP BY " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID + " ORDER BY average_score ASC", new String[]{String.valueOf(gameId)});

        if (cursor.moveToFirst()) {
            do {
                String playerNickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
                double averageScore = cursor.getDouble(cursor.getColumnIndex("average_score"));
//                playerStatsList.add(playerNickname + ": " + averageScore);
                playerStatsList.add(playerNickname + ": " + String.format("%.2f", averageScore));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playerStatsList;
    }

    public void editGameScore(int gameId, int playerId, int consecutiveTracker, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, newScore);

        String whereClause = "game_id = ? AND player_id = ? AND " + COLUMN_CONSECUTIVE_TRACKER + " = ?";
        String[] whereArgs = {String.valueOf(gameId), String.valueOf(playerId), String.valueOf(consecutiveTracker)};

        db.update(TABLE_GAME_SCORES, values, whereClause, whereArgs);
        db.close();
    }

    public void removeGameScore(int gameId, int playerId, int consecutiveTracker) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "game_id = ? AND player_id = ? AND " + COLUMN_CONSECUTIVE_TRACKER + " = ?";
        String[] whereArgs = {String.valueOf(gameId), String.valueOf(playerId), String.valueOf(consecutiveTracker)};

        db.delete(TABLE_GAME_SCORES, whereClause, whereArgs);
        db.close();
    }
    public List<HashMap<String, Object>> getPlayerScoresForGame(int playerId, int gameId) {
        List<HashMap<String, Object>> scoresList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT game_scores.score_id, game_scores.identifier, game_scores.score, game_scores.consecutive_tracker " +
                "FROM game_scores " +
                "INNER JOIN game_players ON game_scores.game_player_id = game_players.rowid " +
                "WHERE game_players.game_id = ? AND game_players.player_id = ? " +
                "ORDER BY game_scores.consecutive_tracker ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gameId), String.valueOf(playerId)});
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> score = new HashMap<>();
                score.put("score_id", cursor.getInt(0));
                score.put("identifier", cursor.getString(1));
                score.put("score", cursor.getInt(2));
                score.put("consecutive_tracker", cursor.getInt(3));
                scoresList.add(score);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }
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
            if (cursor.getColumnIndex("id") != -1){
                gameId = cursor.getInt(cursor.getColumnIndex("id"));
            } else {
                Log.d("Tag","ERROR: cursor.getColumnIndex(\"id\"):"+cursor.getColumnIndex("id"));
            }
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
    public void deletePlayersAndScores(List<String> playerNicknames) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete the players from the players table
        String deletePlayerSql = "DELETE FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_PLAYER_NICKNAME + " IN (" +
                TextUtils.join(",", Collections.nCopies(playerNicknames.size(), "?")) + ")";
        db.execSQL(deletePlayerSql, playerNicknames.toArray(new String[0]));

        // Delete the game scores tied to the players
        String deleteScoresSql = "DELETE FROM game_scores WHERE game_player_id IN " +
                "(SELECT rowid FROM game_players WHERE player_id IN " +
                "(SELECT " + COLUMN_PLAYER_ID + " FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_PLAYER_NICKNAME + " IN (" +
                TextUtils.join(",", Collections.nCopies(playerNicknames.size(), "?")) + ")))";
        db.execSQL(deleteScoresSql, playerNicknames.toArray(new String[0]));

        // Delete the players from the game_players table
        String deleteGamePlayersSql = "DELETE FROM game_players WHERE player_id IN " +
                "(SELECT " + COLUMN_PLAYER_ID + " FROM " + TABLE_PLAYERS + " WHERE " + COLUMN_PLAYER_NICKNAME + " IN (" +
                TextUtils.join(",", Collections.nCopies(playerNicknames.size(), "?")) + "))";
        db.execSQL(deleteGamePlayersSql, playerNicknames.toArray(new String[0]));
    }

    public void deleteGameAndScores(int gameId) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete the game from the games table
        String deleteGameSql = "DELETE FROM games WHERE id = ?";
        db.execSQL(deleteGameSql, new String[]{String.valueOf(gameId)});

        // Delete the game scores tied to the game
        String deleteScoresSql = "DELETE FROM game_scores WHERE game_player_id IN " +
                "(SELECT rowid FROM game_players WHERE game_id = ?)";
        db.execSQL(deleteScoresSql, new String[]{String.valueOf(gameId)});

        // Delete the game players tied to the game
        String deletePlayersSql = "DELETE FROM game_players WHERE game_id = ?";
        db.execSQL(deletePlayersSql, new String[]{String.valueOf(gameId)});
    }
    public void insertPlayer(Player player) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_PLAYER_NICKNAME, player.getNickname());

        db.insert(MyDatabaseHelper.TABLE_PLAYERS, null, values);
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
    private long getGamePlayerId(long playerId, long gameId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("game_players",
                new String[]{"rowid"},
                "player_id = ? AND game_id = ?",
                new String[]{String.valueOf(playerId), String.valueOf(gameId)},
                null,
                null,
                null);
        long gamePlayerId = -1;
        if (cursor.moveToFirst()) {
            gamePlayerId = cursor.getLong(cursor.getColumnIndex("rowid"));
        }
        cursor.close();
        return gamePlayerId;
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

    public void editScore(int scoreId, int newScore) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, newScore);

        String whereClause = COLUMN_SCORE_ID + " = ?";
        String[] whereArgs = {String.valueOf(scoreId)};

        db.update(TABLE_GAME_SCORES, values, whereClause, whereArgs);
    }

}


