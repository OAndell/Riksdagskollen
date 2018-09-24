package oscar.riksdagskollen.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

/**
 * Created by gustavaaro on 2018-09-24.
 */

public class AlertManager {

    private HashSet<String> replyAlerts;

    // Stores each alert active based on map with Id:boolean

    private SharedPreferences alertsPreferences;


    public AlertManager(Context app) {
        alertsPreferences = app.getSharedPreferences("alerts", 0);
        replyAlerts = new HashSet<>();
        Set<String> docAlertIds = alertsPreferences.getAll().keySet();
        replyAlerts.addAll(docAlertIds);
        System.out.println("ALERT INIT: " + replyAlerts.toString());
    }

    public HashSet<String> getReplyAlerts() {
        return replyAlerts;
    }

    public boolean isAlertEnabledForDoc(PartyDocument document) {
        System.out.println("Array: " + Arrays.toString(replyAlerts.toArray()));
        return replyAlerts.contains(document.getId());
    }

    public void setAlertEnabledForDoc(PartyDocument document, boolean enabled) {
        if (enabled) {
            alertsPreferences.edit().putBoolean(document.getId(), true).apply();
            replyAlerts.add(document.getId());

            // Start tracking job if not already started
            RiksdagskollenApp.getInstance().scheduleCheckRepliesJobIfNotRunning();

        } else {
            alertsPreferences.edit().remove(document.getId()).apply();
            replyAlerts.remove(document.getId());
        }
        System.out.println("Array: " + Arrays.toString(replyAlerts.toArray()));
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
        System.out.println("returning: " + enabled);
        return enabled;
    }

    public boolean hasAlerts() {
        return !replyAlerts.isEmpty();
    }


}
