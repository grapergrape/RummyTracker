package si.uni_lj.fe.tnuv.database;

import java.io.Serializable;

public class Player implements Serializable {
    private int id;
    private String nickname;

    public Player(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}

