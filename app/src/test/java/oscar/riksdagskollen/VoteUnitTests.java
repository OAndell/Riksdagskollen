package oscar.riksdagskollen;

/**
 * Created by oscar on 2018-09-20.
 */

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import oscar.riksdagskollen.Activity.VoteActivity;
import oscar.riksdagskollen.Util.Helper.VoteResults;
import oscar.riksdagskollen.Util.JSONModel.Vote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VoteUnitTests {

    private VoteResults testVoteResult;
    private Vote voteTest1;
    private Vote voteTest2;
    private Vote voteTest3;


    @Before
    public void setUp(){
         /*
        S	97	0	0	16
        M	71	0	0	12
        SD	0	34	0	8
        MP	22	0	0	3
        C	18	0	0	4
        V	20	0	0	1
        L	17	0	0	2
        KD	15	0	0	1
        -	0	3	0	5
        Totalt	260	37	0	52*/
        String resultHTMLTable = "<table class=\"vottabell\" summary='Voteringsresultat'><tr class='sakfragan'><td colspan='5'><h4>Omröstning i sakfrågan</h4><p>Utskottets förslag mot reservation 2 (SD)</p></td></tr><tr class='vottabellrubik'><th>Parti</th><th>Ja</th><th>Nej</th><th>Avstående</th><th>Frånvarande</th></tr><tr><td class='parti'>S</td><td class='rost_ja'>97</td><td class='rost_nej'>0</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>16</td></tr><tr><td class='parti'>M</td><td class='rost_ja'>71</td><td class='rost_nej'>0</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>12</td></tr><tr><td class='parti'>SD</td><td class='rost_ja'>0</td><td class='rost_nej'>34</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>8</td></tr><tr><td class='parti'>MP</td><td class='rost_ja'>22</td><td class='rost_nej'>0</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>3</td></tr><tr><td class='parti'>C</td><td class='rost_ja'>18</td><td class='rost_nej'>0</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>4</td></tr><tr><td class='parti'>V</td><td class='rost_ja'>20</td><td class='rost_nej'>0</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>1</td></tr><tr><td class='parti'>L</td><td class='rost_ja'>17</td><td class='rost_nej'>0</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>2</td></tr><tr><td class='parti'>KD</td><td class='rost_ja'>15</td><td class='rost_nej'>0</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>1</td></tr><tr><td class='parti'>-</td><td class='rost_ja'>0</td><td class='rost_nej'>3</td><td class='rost_avstar'>0</td><td class='rost_franvarande'>5</td></tr><tr><td class='totalt'>Totalt</td><td class='summa_ja'>260</td><td class='summa_nej'>37</td><td class='summa_avstar'>0</td><td class='summa_franvarande'>52</td></tr><tr><td colspan='5'><h4 class='beslut'></h4></td></tr></table>";
        testVoteResult = new VoteResults(resultHTMLTable);

        voteTest1 = new Vote();
        voteTest2 = new Vote();
        voteTest3 = new Vote();
        try {
            Field id = Vote.class.getDeclaredField("id");
            Field organ = Vote.class.getDeclaredField("organ");
            Field nummer = Vote.class.getDeclaredField("nummer");

            id.setAccessible(true);
            organ.setAccessible(true);
            nummer.setAccessible(true);

            id.set(voteTest1, "GQ19KU2p2");
            organ.set(voteTest1, "KU");
            nummer.set(voteTest1, "2");

            id.set(voteTest2, "GQ19SKU1p4");
            organ.set(voteTest2, "SKU");
            nummer.set(voteTest2, "1");

            id.set(voteTest3, "GQ19SOU11p1");
            organ.set(voteTest3, "SOU");
            nummer.set(voteTest3, "11");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void voteResult_Correct_Total_ReturnsTrue(){
        int correctTotalVotes[] = {260, 37, 0 ,52};
        assertTrue(Arrays.equals(testVoteResult.getTotal(),correctTotalVotes));
    }

    @Test
    public void voteResult_CorrectPartyVotes_ReturnsTrue() {
        int correctSVotes[] = {97, 0,0,16};
        int correctMVotes[] = {71, 0,0,12};
        int correctSDVotes[] = {0, 34,0,8};
        int correctMPVotes[] = {22, 0,0,3};
        int correctCVotes[] = {18, 0,0,4};
        int correctVVotes[] = {20, 0,0,1};
        int correctLVotes[] = {17, 0,0,2};
        int correctKDVotes[] = {15, 0,0,1};
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("S"),correctSVotes));
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("M"),correctMVotes));
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("SD"),correctSDVotes));
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("MP"),correctMPVotes));
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("C"),correctCVotes));
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("V"),correctVVotes));
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("L"),correctLVotes));
        assertTrue(Arrays.equals(testVoteResult.getPartyVotes("KD"),correctKDVotes));
    }

    @Test
    public void voteResult_CorrectParties_ReturnsTrue(){
        String[] correctParties = {"S","M","SD","MP","C","V","L","KD"};
        ArrayList<String> parties =  testVoteResult.getPartiesInVote();
        for (int i = 0; i < correctParties.length; i++) {
            assertTrue(parties.contains(correctParties[i]));
        }
    }

    @Test
    public void voteResult_hashMapCreated_ReturnsTrue(){
        assertTrue(testVoteResult.getVoteResults()!=null);
    }

    @Test
    public void voteActivity_parsesURLcorrect() {
        assertEquals("Voteresult 1 url parse: ", "http://riksdagen.se/sv/dokument-lagar/arende/betankande/_GQ01KU2", VoteActivity.getBetUrl(voteTest1));
        assertEquals("Voteresult 2 url parse: ", "http://riksdagen.se/sv/dokument-lagar/arende/betankande/_GQ01SKU1", VoteActivity.getBetUrl(voteTest2));
        assertEquals("Voteresult 3 url parse: ", "http://riksdagen.se/sv/dokument-lagar/arende/betankande/_GQ01SOU11", VoteActivity.getBetUrl(voteTest3));
    }

}
