package oscar.riksdagskollen.Util;

import android.content.Context;

import javax.xml.transform.Source;

import oscar.riksdagskollen.R;

/**
 * Created by gustavaaro on 2018-07-21.
 */

public enum Committee {

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

    Committee(String id){
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

    public String getCommitteeCategoryName() {
        return committeeCategoryName;
    }

    public static Committee getCategoryFromBet(String bet){
        String id = bet.split("[0-9]+")[0];

        for (Committee c : values()) {
            if (id.equals(c.id))return c;
        }

        for (Committee c: values()){
            if (id.contains(c.id))return c;
        }

        return null;
    }

}
