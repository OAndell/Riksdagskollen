package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import oscar.riksdagskollen.Util.Enum.CurrentParties;

public class Debate implements Parcelable {


    private DebateSpeech[] anforande;

    public DebateSpeech[] getAnforande() {
        return anforande;
    }

    public ArrayList<Party> getPartiesInDebate() {
        ArrayList<Party> parties = new ArrayList<>();
        for (int i = 0; i < anforande.length; i++) {
            Party party = CurrentParties.getParty(anforande[i].getParti());
            if (!parties.contains(party)) {
                parties.add(party);
            }
        }
        return parties;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.anforande, flags);
    }

    public Debate() {
    }

    protected Debate(Parcel in) {
        this.anforande = in.createTypedArray(DebateSpeech.CREATOR);
    }

    public static final Parcelable.Creator<Debate> CREATOR = new Parcelable.Creator<Debate>() {
        @Override
        public Debate createFromParcel(Parcel source) {
            return new Debate(source);
        }

        @Override
        public Debate[] newArray(int size) {
            return new Debate[size];
        }
    };
}
