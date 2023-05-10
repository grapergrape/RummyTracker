package si.uni_lj.fe.tnuv.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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




}


