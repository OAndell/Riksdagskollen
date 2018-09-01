package oscar.riksdagskollen.Util;

import java.util.ArrayList;

import oscar.riksdagskollen.R;

/**
 * Created by gustavaaro on 2018-07-21.
 */

public enum DecicionCategory {

    FiU("FiU"),
    CU("CU"),
    AU("AU"),
    FöU("FöU"),
    JuU("JuU"),
    KU("KU"),
    KrU("KrU"),
    MJU("MJU"),
    NU("NU"),
    SkU("SkU"),
    SfU("SfU"),
    SoU("SoU"),
    TU("TU"),
    UbU("UbU"),
    UU("UU"),
    UFöU("UFöU");

    DecicionCategory(String id) {
        this.id = id;
    }

    private String committeeCategoryName;
    private String id;
    private int categoryColor;

    static {
        FiU.committeeCategoryName = "Finans";
        FiU.categoryColor = R.color.cat_light_blue;

        CU.committeeCategoryName = "Konsumentfrågor";
        CU.categoryColor = R.color.cat_teal;

        AU.committeeCategoryName = "Arbetsmarknad";
        AU.categoryColor = R.color.cat_orange;

        FöU.committeeCategoryName = "Försvar och miltär";
        FöU.categoryColor = R.color.cat_lime;

        JuU.committeeCategoryName = "Kriminalitet och rättväsende";
        JuU.categoryColor = R.color.cat_blue;

        KU.committeeCategoryName = "Riksdagen";
        KU.categoryColor = R.color.primaryColor;

        KrU.committeeCategoryName = "Kultur";
        KrU.categoryColor = R.color.cat_pink;

        MJU.committeeCategoryName = "Miljö och jordbruk";
        MJU.categoryColor = R.color.cat_green;

        NU.committeeCategoryName = "Näringsliv";
        NU.categoryColor = R.color.cat_orange;

        SkU.committeeCategoryName = "Skatter";
        SkU.categoryColor = R.color.cat_yellow;

        SfU.committeeCategoryName = "Socialförsäkringar";
        SfU.categoryColor = R.color.cat_brown;

        SoU.committeeCategoryName = "Vård och omsorg";
        SoU.categoryColor = R.color.cat_red;

        TU.committeeCategoryName = "Transport och kommunikation";
        TU.categoryColor = R.color.cat_dark_gray;

        UbU.committeeCategoryName = "Utbildning";
        UbU.categoryColor = R.color.cat_purple;

        UU.committeeCategoryName = "Utrikesfrågor";
        UU.categoryColor = R.color.cat_light_gray;

        UFöU.committeeCategoryName = "Utrikesförsvar";
        UFöU.categoryColor = R.color.cat_lime;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    public String getCategoryName() {
        return committeeCategoryName;
    }

    public String getId() {
        return id;
    }

    public static CharSequence[] getCategoryNames() {
        CharSequence[] names = new CharSequence[values().length];

        for (int i = 0; i < names.length; i++) {
            names[i] = values()[i].committeeCategoryName;
        }

        return names;
    }

    public static ArrayList<DecicionCategory> getAllCategories() {
        ArrayList<DecicionCategory> categories = new ArrayList<>();
        for (int i = 0; i < values().length; i++) {
            categories.add(values()[i]);
        }
        return categories;
    }

    public static DecicionCategory getCategoryFromBet(String bet) {
        String id = bet.split("[0-9]+")[0];

        for (DecicionCategory c : values()) {
            if (id.equals(c.id))return c;
        }

        for (DecicionCategory c : values()) {
            if (id.contains(c.id))return c;
        }

        return null;
    }

    @Override
    public String toString() {
        return committeeCategoryName;
    }
}
