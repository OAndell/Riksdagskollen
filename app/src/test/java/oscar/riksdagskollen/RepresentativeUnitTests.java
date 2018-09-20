package oscar.riksdagskollen;

import org.junit.Test;

import oscar.riksdagskollen.Util.JSONModel.Representative;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by gustavaaro on 2018-09-19.
 */
public class RepresentativeUnitTests {

    @Test
    public void sourceIdParsingFromImgUrl() {
        assertThat(Representative.getSourceIdFromImageUrl(
                "https://data.riksdagen.se/filarkiv/bilder/ledamot/ac737989-5fa0-44bc-ad69-c1a0ddba71bb_320.jpg")
                .equals("ac737989-5fa0-44bc-ad69-c1a0ddba71bb"), is(true));
        assertThat(Representative.getSourceIdFromImageUrl(
                "http://data.riksdagen.se/filarkiv/bilder/ledamot/727e2213-ac97-4ceb-bfab-f228debd8074_80.jpg")
                .equals("727e2213-ac97-4ceb-bfab-f228debd8074"), is(true));
    }

}


