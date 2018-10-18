package oscar.riksdagskollen.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

/**
 * Created by gustavaaro on 2018-09-24.
 */

public class AlertManager {

    private HashSet<String> replyAlerts;
    private HashMap<String, String> partyAlerts;
    private HashMap<String, String> sectionAlerts;

    private SharedPreferences docAlertsPreferences;
    private SharedPreferences sectionAlertPreferences;
    private SharedPreferences partyAlertPreferences;


    public AlertManager(Context app) {
        docAlertsPreferences = app.getSharedPreferences("docAlerts", 0);
        sectionAlertPreferences = app.getSharedPreferences("sectionAlerts", 0);
        partyAlertPreferences = app.getSharedPreferences("partyAlerts", 0);

        replyAlerts = new HashSet<>();
        partyAlerts = new HashMap<>();
        sectionAlerts = new HashMap<>();

        Set<String> docAlertIds = docAlertsPreferences.getAll().keySet();
        replyAlerts.addAll(docAlertIds);

        for (String key : sectionAlertPreferences.getAll().keySet()) {
            sectionAlerts.put(key, sectionAlertPreferences.getString(key, ""));
        }

        for (String key : partyAlertPreferences.getAll().keySet()) {
            partyAlerts.put(key, partyAlertPreferences.getString(key, ""));
        }

    }

    public HashSet<String> getReplyAlerts() {
        return replyAlerts;
    }

    public HashMap<String, String> getSectionAlerts() {
        return sectionAlerts;
    }

    public HashMap<String, String> getPartyAlerts() {
        return partyAlerts;
    }

    public boolean isAlertEnabledForDoc(PartyDocument document) {
        return replyAlerts.contains(document.getId());
    }

    public boolean isAlertEnabledForParty(String partyId) {
        return partyAlerts.containsKey(partyId);
    }

    public boolean isAlertEnabledForSection(String section) {
        return sectionAlerts.containsKey(section);
    }

    public void setAlertEnabledForPartyDocuments(String partyId, PartyDocument latest, boolean enabled) {
        if (enabled) {
            partyAlertPreferences.edit().putString(partyId, latest.getId()).apply();
            partyAlerts.put(partyId, latest.getId());
            // Start tracking job if not already started
            RiksdagskollenApp.getInstance().scheduleCheckAlertsJobIfNotRunning();
        } else {
            partyAlertPreferences.edit().remove(partyId).apply();
            partyAlerts.remove(partyId);
        }
    }

    public boolean toggleEnabledForParty(String partyid, PartyDocument latest) {
        boolean enabled;
        if (partyAlerts.containsKey(partyid)) {
            setAlertEnabledForPartyDocuments(partyid, latest, false);
            enabled = false;
        } else {
            setAlertEnabledForPartyDocuments(partyid, latest, true);
            enabled = true;
        }
        return enabled;
    }

    public void setAlertEnabledForSection(String section, String idLatest, boolean enabled) {
        if (enabled) {
            sectionAlertPreferences.edit().putString(section, idLatest).apply();
            sectionAlerts.put(section, idLatest);
            // Start tracking job if not already started
            RiksdagskollenApp.getInstance().scheduleCheckAlertsJobIfNotRunning();
        } else {
            sectionAlertPreferences.edit().remove(section).apply();
            sectionAlerts.remove(section);
        }
    }

    public boolean toggleEnabledForPage(String section, String idLatest) {
        boolean enabled;
        if (sectionAlerts.containsKey(section)) {
            setAlertEnabledForSection(section, idLatest, false);
            enabled = false;
        } else {
            setAlertEnabledForSection(section, idLatest, true);
            enabled = true;
        }
        return enabled;
    }


    public void setAlertEnabledForDoc(PartyDocument document, boolean enabled) {
        if (enabled) {
            docAlertsPreferences.edit().putBoolean(document.getId(), true).apply();
            replyAlerts.add(document.getId());
            // Start tracking job if not already started
            RiksdagskollenApp.getInstance().scheduleCheckAlertsJobIfNotRunning();
        } else {
            docAlertsPreferences.edit().remove(document.getId()).apply();
            replyAlerts.remove(document.getId());
        }
    }

    public boolean toggleEnabledForDoc(PartyDocument document) {
        boolean enabled;
        if (replyAlerts.contains(document.getId())) {
            setAlertEnabledForDoc(document, false);
            enabled = false;
        } else {
            setAlertEnabledForDoc(document, true);
            enabled = true;
        }
        return enabled;
    }

    public boolean hasAlerts() {
        return !replyAlerts.isEmpty() && !partyAlerts.isEmpty() && !sectionAlerts.isEmpty();
    }

    public static AlertManager getInstance() {
        return RiksdagskollenApp.getInstance().getAlertManager();
    }

    public int getAlertCount() {
        return (replyAlerts.size() + partyAlerts.size() + sectionAlerts.size());
    }

}
