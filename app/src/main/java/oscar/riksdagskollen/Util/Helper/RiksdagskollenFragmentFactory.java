package oscar.riksdagskollen.Util.Helper;

import android.support.v4.app.Fragment;

import java.lang.ref.SoftReference;

import oscar.riksdagskollen.CurrentNews.CurrentNewsListFragment;
import oscar.riksdagskollen.Fragment.AboutFragment;
import oscar.riksdagskollen.Fragment.DebateListFragment;
import oscar.riksdagskollen.Fragment.DecisionsListFragment;
import oscar.riksdagskollen.Fragment.PartyFragment;
import oscar.riksdagskollen.Fragment.PartyListFragment;
import oscar.riksdagskollen.Fragment.ProtocolListFragment;
import oscar.riksdagskollen.Fragment.RepresentativeListFragment;
import oscar.riksdagskollen.Fragment.SavedDocumentsFragment;
import oscar.riksdagskollen.Fragment.SearchListFragment;
import oscar.riksdagskollen.Fragment.TwitterListFragment;
import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.Party;

public class RiksdagskollenFragmentFactory {

    private SoftReference<CurrentNewsListFragment> currentNewsListFragment;
    private SoftReference<ProtocolListFragment> protFragment;
    private SoftReference<DecisionsListFragment> decisionsFragment;
    private SoftReference<RepresentativeListFragment> repFragment;
    private SoftReference<VoteListFragment> voteListFragment;
    private SoftReference<DebateListFragment> debateFragment;
    private SoftReference<AboutFragment> aboutFragment;
    private SoftReference<SavedDocumentsFragment> savedDocumentsFragment;
    private SoftReference<SearchListFragment> searchFragment;
    private SoftReference<TwitterListFragment> twitterListFragment;
    private SoftReference<PartyFragment> sPartyFragment;
    private SoftReference<PartyFragment> mPartyFragment;
    private SoftReference<PartyFragment> sdPartyFragment;
    private SoftReference<PartyFragment> mpPartyFragment;
    private SoftReference<PartyFragment> cPartyFragment;
    private SoftReference<PartyFragment> vPartyFragment;
    private SoftReference<PartyFragment> lPartyFragment;
    private SoftReference<PartyFragment> kdPartyFragment;


    public Fragment getFragment(String fragment) {

        String s = CurrentParties.getS().getID();

        switch (fragment) {
            case CurrentNewsListFragment.SECTION_NAME_NEWS:
                if (currentNewsListFragment == null || currentNewsListFragment.get() == null)
                    currentNewsListFragment = new SoftReference<>(CurrentNewsListFragment.newInstance());
                return currentNewsListFragment.get();

            case DecisionsListFragment.SECTION_NAME_DECISIONS:
                if (decisionsFragment == null || decisionsFragment.get() == null)
                    decisionsFragment = new SoftReference<>(DecisionsListFragment.newInstance());
                return decisionsFragment.get();

            case DebateListFragment.SECTION_NAME_DEBATE:
                if (debateFragment == null || debateFragment.get() == null)
                    debateFragment = new SoftReference<>(DebateListFragment.newInstance());
                return debateFragment.get();

            case VoteListFragment.SECTION_NAME_VOTE:
                if (voteListFragment == null || voteListFragment.get() == null)
                    voteListFragment = new SoftReference<>(VoteListFragment.newInstance(null));
                return voteListFragment.get();

            case RepresentativeListFragment.SECTION_NAME_REPS:
                if (repFragment == null || repFragment.get() == null)
                    repFragment = new SoftReference<>(RepresentativeListFragment.newInstance());
                return repFragment.get();

            case ProtocolListFragment.SECTION_NAME_protocol:
                if (protFragment == null || protFragment.get() == null)
                    protFragment = new SoftReference<>(ProtocolListFragment.newInstance());
                return protFragment.get();
            case TwitterListFragment.SECTION_NAME_TWITTER:
                if (twitterListFragment == null || twitterListFragment.get() == null)
                    twitterListFragment = new SoftReference<>(TwitterListFragment.newInstance());
                return twitterListFragment.get();

            case "s":
                sPartyFragment = setUpPartySoftFragmentReference(sPartyFragment, CurrentParties.getS());
                return sPartyFragment.get();

            case "m":
                mPartyFragment = setUpPartySoftFragmentReference(mPartyFragment, CurrentParties.getM());
                return mPartyFragment.get();

            case "sd":
                sdPartyFragment = setUpPartySoftFragmentReference(sdPartyFragment, CurrentParties.getSD());
                return sdPartyFragment.get();

            case "c":
                cPartyFragment = setUpPartySoftFragmentReference(cPartyFragment, CurrentParties.getC());
                return cPartyFragment.get();

            case "v":
                vPartyFragment = setUpPartySoftFragmentReference(vPartyFragment, CurrentParties.getV());
                return vPartyFragment.get();

            case "kd":
                kdPartyFragment = setUpPartySoftFragmentReference(kdPartyFragment, CurrentParties.getKD());
                return kdPartyFragment.get();

            case "l":
                lPartyFragment = setUpPartySoftFragmentReference(lPartyFragment, CurrentParties.getL());
                return lPartyFragment.get();

            case "mp":
                mpPartyFragment = setUpPartySoftFragmentReference(mpPartyFragment, CurrentParties.getMP());
                return mpPartyFragment.get();

            case SearchListFragment.SECTION_NAME_SEARCH:
                if (searchFragment == null || searchFragment.get() == null)
                    searchFragment = new SoftReference<>(SearchListFragment.newInstance());
                return searchFragment.get();

            case SavedDocumentsFragment.SECTION_NAME_SAVED:
                if (savedDocumentsFragment == null || savedDocumentsFragment.get() == null)
                    savedDocumentsFragment = new SoftReference<>(SavedDocumentsFragment.newInstance());
                return savedDocumentsFragment.get();

            case AboutFragment.SECTION_NAME_ABOUT:
                if (aboutFragment == null || aboutFragment.get() == null)
                    aboutFragment = new SoftReference<>(AboutFragment.newInstance());
                return aboutFragment.get();

            default:
                return null;
        }

    }


    private SoftReference<PartyFragment> setUpPartySoftFragmentReference(SoftReference<PartyFragment> fragmentReference, Party party) {
        if (fragmentReference == null || fragmentReference.get() == null) {
            fragmentReference = new SoftReference<>(PartyFragment.newInstance(party));
            PartyListFragment partyListFragment = PartyListFragment.newInstance(party);
            fragmentReference.get().setListFragment(partyListFragment);
        }
        return fragmentReference;
    }

}
