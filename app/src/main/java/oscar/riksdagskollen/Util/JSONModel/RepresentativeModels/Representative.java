package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by oscar on 2018-03-27.
 * Hold useful information about a Representative (Ledamot)
 *
 * Representative
 *       -> RepresentativeMissionList ( There mission in parliament etc)
 *          -> RepresentativeMission
 *       -> RepresentativeInfoList (Personal information)
 *          -> RepresentativeInfo
 */
public class Representative implements Parcelable {

    public static final Creator<Representative> CREATOR = new Creator<Representative>() {
        @Override
        public Representative createFromParcel(Parcel source) {
            return new Representative(source);
        }

        @Override
        public Representative[] newArray(int size) {
            return new Representative[size];
        }
    };
    // An array of roles considered VIP. These should always be the main role of representative
    private static final String[] VIP_ROLES = {"Statsminister"};
    private String intressent_id;
    private String sourceid;
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
    private RepresentativeMissionList personuppdrag;
    private RepresentativeInfoList personuppgift;
    public Representative(String tilltalsnamn, String efternamn, String roll_kod, String image){
        this.tilltalsnamn = tilltalsnamn;
        this.efternamn = efternamn;
        this.roll_kod = roll_kod;
        this.bild_url_192 = image;
        this.sourceid = getSourceIdFromImageUrl(image);
    }

    protected Representative(Parcel in) {
        this.intressent_id = in.readString();
        this.sourceid = in.readString();
        this.hangar_id = in.readString();
        this.fodd_ar = in.readString();
        this.kon = in.readString();
        this.efternamn = in.readString();
        this.tilltalsnamn = in.readString();
        this.parti = in.readString();
        this.valkrets = in.readString();
        this.status = in.readString();
        this.bild_url_80 = in.readString();
        this.bild_url_192 = in.readString();
        this.bild_url_max = in.readString();
        this.roll_kod = in.readString();
        this.personuppdrag = in.readParcelable(RepresentativeMissionList.class.getClassLoader());
        this.personuppgift = in.readParcelable(RepresentativeInfoList.class.getClassLoader());
    }

    // Will parse a representative img url in either of the following forms:
    // http://data.riksdagen.se/filarkiv/bilder/ledamot/cb0336b4-54a8-4384-abe3-8d6cbebc63fa_80.jpg
    // https://data.riksdagen.se/filarkiv/bilder/ledamot/ac737989-5fa0-44bc-ad69-c1a0ddba71bb_320.jpg
    public static String getSourceIdFromImageUrl(String imgUrl) {
        String result = "";
        String[] splittedUrl = imgUrl.split("/");
        if (splittedUrl.length > 1) {
            String tmp = splittedUrl[splittedUrl.length - 1];
            return tmp.split("_")[0];
        }

        return result;
    }

    public String getSourceid() {
        return sourceid;
    }

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

    /**
     * @return the "Civilian" work title of the representative
     */
    public String getTitle() {
        ArrayList<RepresentativeInfo> infoList = getPersonuppgift().getUppgift();
        for (int i = 0; i < infoList.size(); i++) {
            if (infoList.get(i).getKod().equals("sv")) {
                return infoList.get(i).getUppgift()[0];
            }
        }
        return "";
    }

    /**
     * @return Returns the personal website url if it exists
     */
    public String getWebsite() {
        if (getPersonuppgift() != null) {
            ArrayList<RepresentativeInfo> infoList = getPersonuppgift().getUppgift();
            for (int i = 0; i < infoList.size(); i++) {
                if (infoList.get(i).getKod().equals("Webbsida")) {
                    return infoList.get(i).getUppgift()[0];
                }
            }
        }
        return "";
    }

    /**
     * Get biography
     *
     * @return ArrayList<[Title,INFO]>
     */
    public ArrayList<String[]> getBiography() {
        ArrayList<String[]> biography = new ArrayList<>();
        if (getPersonuppgift() != null) {
            ArrayList<RepresentativeInfo> infoList = getPersonuppgift().getUppgift();
            for (int i = 0; i < infoList.size(); i++) {
                if (infoList.get(i).getTyp().equals("biografi")) {
                    biography.add(new String[]{infoList.get(i).getKod(), infoList.get(i).getUppgift()[0]});
                }
            }
        }
        return biography;
    }

    public RepresentativeInfoList getPersonuppgift() {
        return personuppgift;
    }

    public String getAge() {
        if (fodd_ar == null) return "-";

        Calendar cal = Calendar.getInstance();
        int age = cal.get(Calendar.YEAR) - Integer.parseInt(fodd_ar);
        return Integer.toString(age);
    }

    private String getCurrentPartyRole() {
        if (personuppdrag.getUppdrag().size() == 0) return null;
        for (RepresentativeMission mission : personuppdrag.getUppdrag()) {
            if (mission.typ.equals("partiuppdrag") && mission.tom == null) {
                // Status can be "active" or "inactive" etc
                if (mission.status != null) return mission.status + " " + mission.roll_kod;
                return mission.roll_kod;
            }
        }
        // No current party role
        return null;
    }

    // Determine if status is more important to show than partyrole
    private boolean roleIsVIP() {
        for (int i = 0; i < VIP_ROLES.length; i++) {
            if (status != null && status.equals(VIP_ROLES[i])) return true;
        }
        return false;
    }

    public String getDescriptiveRole() {
        if (roleIsVIP()) return status;
        if (getCurrentPartyRole() != null) return getCurrentPartyRole();
        else if (getStatus() != null) return status;
        else return getCurrentOrMostRecentRole();
    }

    private String getCurrentOrMostRecentRole() {
        if (personuppdrag.getUppdrag().size() == 0) return "";
        for (RepresentativeMission mission : personuppdrag.getUppdrag()) {
            if (mission.tom == null) {
                // Status can be "active" or "inactive" etc
                if (mission.status != null) return mission.status + " " + mission.roll_kod;
                return mission.roll_kod;
            }
        }

        // If all else fails, just return the first role in the list
        RepresentativeMission mostRecent = personuppdrag.getUppdrag().get(0);
        if (mostRecent.status != null) return mostRecent.status + " " + mostRecent.roll_kod;
        return mostRecent.roll_kod;
    }

    public ArrayList<RepresentativeMission> getPersonuppdrag() {
        return personuppdrag.getUppdrag();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.intressent_id);
        dest.writeString(this.sourceid);
        dest.writeString(this.hangar_id);
        dest.writeString(this.fodd_ar);
        dest.writeString(this.kon);
        dest.writeString(this.efternamn);
        dest.writeString(this.tilltalsnamn);
        dest.writeString(this.parti);
        dest.writeString(this.valkrets);
        dest.writeString(this.status);
        dest.writeString(this.bild_url_80);
        dest.writeString(this.bild_url_192);
        dest.writeString(this.bild_url_max);
        dest.writeString(this.roll_kod);
        dest.writeParcelable(this.personuppdrag, flags);
        dest.writeParcelable(this.personuppgift, flags);
    }
}
