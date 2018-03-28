package oscar.riksdagskollen.Utilities.JSONModels;

import org.json.JSONObject;

/**
 * Created by oscar on 2018-03-28.
 */

public class CurrentNews {
    private String id;
    private String titel;
    private String publicerad;
    private String summary;
    private String img_url;
    private String img_text;
    private String img_fotograf;
    private String img_tumnagel_url;
    private JSONObject sokdata;
    private JSONObject linklista;

    public String getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getSummary() {
        return summary;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getImg_text() {
        return img_text;
    }

    public String getImg_fotograf() {
        return img_fotograf;
    }

    public String getImg_tumnagel_url() {
        return img_tumnagel_url;
    }

    public JSONObject getSokdata() {
        return sokdata;
    }

    public JSONObject getLinklista() {
        return linklista;
    }



}
