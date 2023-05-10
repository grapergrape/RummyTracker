package si.uni_lj.fe.tnuv.database;


public class Score {
    private int scoreID;
    private int identifier;
    private int scoreValue;
    private int gamePlayerID;

    public Score(int scoreID, int identifier, int scoreValue, int gamePlayerID) {
        this.scoreID = scoreID;
        this.identifier = identifier;
        this.scoreValue = scoreValue;
        this.gamePlayerID = gamePlayerID;
    }

    public int getScoreID() {
        return scoreID;
    }

    public void setScoreID(int scoreID) {
        this.scoreID = scoreID;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getGamePlayerID() {
        return gamePlayerID;
    }

    public void setGamePlayerID(int gamePlayerID) {
        this.gamePlayerID = gamePlayerID;
    }
}

