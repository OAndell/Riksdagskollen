package oscar.riksdagskollen.Util.JSONModel;

/**
 * Created by oscar on 2018-03-27.
 * Hold useful information about a Representative (Ledamot)
 */
public class Representative  {

    public Representative(String tilltalsnamn, String efternamn, String roll_kod, String image){
        this.tilltalsnamn = tilltalsnamn;
        this.efternamn = efternamn;
        this.roll_kod = roll_kod;
        this.bild_url_192 = image;
    }

    private String intressent_id;
    private String hangar_id;
    private String fodd_ar;
    private String kon;
    private String efternamn;
    private String tilltalsnamn;
    private String parti;
    private String valkrets;
    private String status;
    private String bild_url_80;
    private String bild_url_192;
    private String bild_url_max;
    private String roll_kod;

    public String getIntressent_id() {
        return intressent_id;
    }

    public String getHangar_id() {
        return hangar_id;
    }

    public String getFodd_ar() {
        return fodd_ar;
    }

    public String getKon() {
        return kon;
    }

    public String getEfternamn() {
        return efternamn;
    }

    public String getTilltalsnamn() {
        return tilltalsnamn;
    }

    public String getParti() {
        return parti;
    }

    public String getValkrets() {
        return valkrets;
    }

    public String getStatus() {
        return status;
    }

    public String getBild_url_80() {
        return bild_url_80;
    }

    public String getBild_url_192() {
        return bild_url_192;
    }

    public String getBild_url_max() {
        return bild_url_max;
    }

    public String getRoll_kod() {
        return roll_kod;
    }

}
