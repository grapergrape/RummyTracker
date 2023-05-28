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

    public boolean isMaxConsecutiveTrackerFullyScored(int gameId) {
        List<Player> playerList = getPlayersInGame(gameId); // Retrieve the player list for the game

        SQLiteDatabase db = this.getReadableDatabase();

        // Query to retrieve the highest consecutive tracker for the given game ID
        String query = "SELECT MAX(" + COLUMN_CONSECUTIVE_TRACKER + ") AS max_consecutive_tracker" +
                " FROM " + TABLE_GAME_SCORES +
                " INNER JOIN game_players ON game_scores.game_player_id = game_players.rowid" +
                " WHERE game_players.game_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gameId)});
        int maxConsecutiveTracker = 0;
        if (cursor.moveToFirst()) {
            maxConsecutiveTracker = cursor.getInt(cursor.getColumnIndex("max_consecutive_tracker"));
        }
        cursor.close();

        // Query to check if each player has an inserted score instance for the maximum consecutive tracker
        query = "SELECT COUNT(*) AS count" +
                " FROM " + TABLE_GAME_SCORES +
                " INNER JOIN game_players ON game_scores.game_player_id = game_players.rowid" +
                " WHERE game_players.game_id = ?" +
                " AND " + COLUMN_CONSECUTIVE_TRACKER + " = ?";

        cursor = db.rawQuery(query, new String[]{String.valueOf(gameId), String.valueOf(maxConsecutiveTracker)});

        boolean isMaxConsecutiveTrackerFullyScored = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(cursor.getColumnIndex("count"));
            isMaxConsecutiveTrackerFullyScored = count == playerList.size();
        }

        cursor.close();
        db.close();

        return isMaxConsecutiveTrackerFullyScored;
    }

    public boolean isSessionZeroScored(int gameId) {
        List<Player> playerList = getPlayersInGame(gameId); // Retrieve the player list for the game

        SQLiteDatabase db = this.getReadableDatabase();

        // Query to retrieve the highest consecutive tracker for the given game ID
        String query = "SELECT MAX(" + COLUMN_CONSECUTIVE_TRACKER + ") AS max_consecutive_tracker" +
                " FROM " + TABLE_GAME_SCORES +
                " INNER JOIN game_players ON game_scores.game_player_id = game_players.rowid" +
                " WHERE game_players.game_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gameId)});
        int maxConsecutiveTracker = 0;
        if (cursor.moveToFirst()) {
            maxConsecutiveTracker = cursor.getInt(cursor.getColumnIndex("max_consecutive_tracker"));
        }
        cursor.close();

        query = "SELECT COUNT(*) AS count" +
                " FROM " + TABLE_GAME_SCORES +
                " INNER JOIN game_players ON game_scores.game_player_id = game_players.rowid" +
                " WHERE game_players.game_id = ?" +
                " AND " + COLUMN_CONSECUTIVE_TRACKER + " = ?" +
                " AND (" +
                "     " + COLUMN_SCORE + " = 0" +
                "     OR game_scores.rowid = (" +
                "         SELECT game_scores.rowid" +
                "         FROM " + TABLE_GAME_SCORES +
                "         INNER JOIN game_players ON game_scores.game_player_id = game_players.rowid" +
                "         WHERE game_players.game_id = ?" +
                "         AND " + COLUMN_CONSECUTIVE_TRACKER + " = ?" +
                "         ORDER BY game_scores.rowid LIMIT 1" +
                "     )" +
                ")";


        cursor = db.rawQuery(query, new String[]{String.valueOf(gameId), String.valueOf(maxConsecutiveTracker),
                String.valueOf(gameId), String.valueOf(maxConsecutiveTracker)});

        boolean isSessionZeroScored = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(cursor.getColumnIndex("count"));
            isSessionZeroScored = count > 0;
        }

        cursor.close();

        // If each player has a score inserted for the consecutive tracker, set isSessionZeroScored to false
        if (isSessionZeroScored) {
            int playerCount = playerList.size();

            query = "SELECT COUNT(*) AS count" +
                    " FROM " + TABLE_GAME_SCORES +
                    " INNER JOIN game_players ON game_scores.game_player_id = game_players.rowid" +
                    " WHERE game_players.game_id = ?" +
                    " AND " + COLUMN_CONSECUTIVE_TRACKER + " = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(gameId), String.valueOf(maxConsecutiveTracker)});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                if (count == playerCount) {
                    isSessionZeroScored = false;
                }
            }

            cursor.close();
        }

        db.close();

        return isSessionZeroScored;
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
    public void addScore(int playerId, int gameId, int scoreValue, int session) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IDENTIFIER, gameId + "_" + playerId);
        values.put(COLUMN_SCORE, scoreValue);
        values.put(COLUMN_GAME_PLAYER_ID, getGamePlayerId(gameId, playerId));
        values.put(COLUMN_CONSECUTIVE_TRACKER, session);
        db.insert(TABLE_GAME_SCORES, null, values);
        db.close();
    }

    private int getGamePlayerId(int gameId, int playerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT rowid FROM game_players WHERE game_id = ? AND player_id = ?";
        String[] selectionArgs = {String.valueOf(gameId), String.valueOf(playerId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        int gamePlayerId = -1;
        if (cursor.moveToFirst()) {
            gamePlayerId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return gamePlayerId;
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
                playerStatsList.add(playerNickname + ": " + averageScore);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return playerStatsList;
    }

    //Method  that returns how mnay times a player has had a score of 0 aka won the game
    public int countZeroScoresForPlayer(int playerId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM game_scores "
                + "WHERE game_player_id IN ("
                + "  SELECT game_player_id FROM game_players "
                + "  WHERE player_id = ?"
                + ") AND score = 0 "
                + "GROUP BY consecutive_tracker";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(playerId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                count += cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return count;
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


    public void deleteGame(String name) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = "name=?";
        String[] whereArgs = {name};
        db.delete("games", whereClause, whereArgs);

        db.close();
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


    public int getPlayerScoreForGame(long playerId, long gameId, int consecutiveTracker) {
        SQLiteDatabase db = getReadableDatabase();
        int score = -1;
        Cursor cursor = db.query("game_scores",
                new String[]{"score"},
                "game_player_id = ? AND consecutive_tracker = ?",
                new String[]{String.valueOf(getGamePlayerId(playerId, gameId)), String.valueOf(consecutiveTracker)},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            score = cursor.getInt(cursor.getColumnIndex("score"));
        }
        cursor.close();
        return score;
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
    // Method to retrieve the list of top 10 highest score insertions
    public List<ScoreInstance> getTop10HighestScoreInstances() {
        List<ScoreInstance> scoreInstances = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + TABLE_PLAYERS + "." + COLUMN_PLAYER_NICKNAME + ", " + TABLE_GAME_SCORES + "." + COLUMN_SCORE
                + " FROM " + TABLE_GAME_SCORES
                + " JOIN game_players ON " + TABLE_GAME_SCORES + "." + COLUMN_GAME_PLAYER_ID + " = game_players.rowid"
                + " JOIN " + TABLE_PLAYERS + " ON game_players.player_id = " + TABLE_PLAYERS + "." + COLUMN_PLAYER_ID
                + " ORDER BY " + TABLE_GAME_SCORES + "." + COLUMN_SCORE + " DESC"
                + " LIMIT 10";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String nickname = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NICKNAME));
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));

                ScoreInstance scoreInstance = new ScoreInstance(nickname, score);
                scoreInstances.add(scoreInstance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoreInstances;
    }



    // Inner class to represent a score instance
    public static class ScoreInstance {
        private String nickname;
        private int score;

        public ScoreInstance(String nickname, int score) {
            this.nickname = nickname;
            this.score = score;
        }

        public String getNickname() {
            return nickname;
        }

        public int getScore() {
            return score;
        }
    }
}


