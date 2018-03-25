package oscar.riksdagskollen.Utilities.JSONModels;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class Party {

    private String id;

    public Party(String name, String id) {
        this.id = id;
    }

    public String getID(){
        return id;
    }
}