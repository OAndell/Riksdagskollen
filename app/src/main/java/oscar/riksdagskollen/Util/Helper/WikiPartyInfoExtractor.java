package oscar.riksdagskollen.Util.Helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WikiPartyInfoExtractor {
    private String wikiContent;
    private String partySummary = null;
    private String partyIdeology = null;
    private String lastUpdated = null;
    private Document summaryDoc;
    private Document footerDoc;

    public WikiPartyInfoExtractor(String wikiContent) {
        this.wikiContent = wikiContent;
        this.summaryDoc = Jsoup.parseBodyFragment(getSummaryFragment());
        this.footerDoc = Jsoup.parseBodyFragment(getFooterFragment());

    }

    private String getSummaryFragment() {
        int startIndex = wikiContent.indexOf("id=\"firstHeading\"");
        int endIndex = wikiContent.indexOf("id=\"toc\"");
        return wikiContent.substring(startIndex, endIndex);
    }

    private String getFooterFragment() {
        int startIndex = wikiContent.indexOf("id=\"footer\"");
        int endIndex = wikiContent.indexOf("id=\"footer-places\"");
        return wikiContent.substring(startIndex, endIndex);
    }

    public String getLastUpdated() {
        if (this.lastUpdated != null) return this.lastUpdated;

        Element lastUpdatedNode = footerDoc.selectFirst("#footer-info-lastmod");
        if (lastUpdatedNode != null) this.lastUpdated = lastUpdatedNode.text().replace("Sidan", "");
        else this.lastUpdated = "";

        return this.lastUpdated;
    }

    public String getPartySummary() {
        if (partySummary != null) return partySummary;

        StringBuilder partySummary = new StringBuilder();
        final int paragraphLimit = 2;
        int paragraphCount = 0;
        Element introBody = summaryDoc.select("#mw-content-text > div").first();
        for (Element element : introBody.children()) {
            if (element.is("p")) {
                String elText = element.text();
                elText = elText.replaceAll("\\[[0-9A-ö ]+\\]", "");
                partySummary.append(elText).append("\n\n");
                paragraphCount++;
            }
            if (paragraphCount == paragraphLimit) break;
        }
        this.partySummary = partySummary.toString();
        return this.partySummary;
    }

    public String getPartyIdeology() {
        if (partyIdeology != null) return partyIdeology;

        Element ideologyTitle = summaryDoc.select("th:contains(Politisk&nbsp;ideologi)").first();
        if (ideologyTitle == null)
            ideologyTitle = summaryDoc.select("th:contains(Politisk ideologi)").first();
        if (ideologyTitle == null)
            ideologyTitle = summaryDoc.select("th:contains(ideologi)").first();
        if (ideologyTitle == null) return "";

        Element ideologyContent = ideologyTitle.nextElementSibling();
        StringBuilder ideologyBuilder = new StringBuilder();
        for (Element child : ideologyContent.children()) {
            if ((!child.tagName().equals("a") &&
                    !child.tagName().equals("br") &&
                    !child.tagName().equals("sup")) &&
                    child.elementSiblingIndex() > 1) {
                break;
            }

            if (child.tagName().equals("a")) {
                ideologyBuilder.append(child.text());
                ideologyBuilder.append(", ");
            }
        }

        this.partyIdeology = ideologyBuilder.toString().replaceAll(", $", "");
        return partyIdeology;

    }

}
