package si.uni_lj.fe.tnuv.database;


public class Game {
    private long id;
    private String name;
    private int status;

    public Game(int id, String name, int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return (int) id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
