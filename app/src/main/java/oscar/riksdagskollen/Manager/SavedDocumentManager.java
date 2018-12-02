package oscar.riksdagskollen.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import oscar.riksdagskollen.RiksdagskollenApp;

public class SavedDocumentManager {

    private HashMap<String, Long> savedDocIds;
    private SharedPreferences preferences;


    public SavedDocumentManager(Context app) {
        preferences = app.getSharedPreferences("saved", 0);

        savedDocIds = new HashMap<>();
        Set<String> docIds = preferences.getAll().keySet();
        for (String key : docIds) {
            savedDocIds.put(key, preferences.getLong(key, 0));
        }
    }

    public void save(String docId) {
        // Time in seconds
        long timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        preferences.edit().putLong(docId, timestamp).apply();
        savedDocIds.put(docId, timestamp);
    }


    public void unSave(String docId) {
        preferences.edit().remove(docId).apply();
        savedDocIds.remove(docId);
    }

    public boolean isSaved(String docId) {
        return preferences.contains(docId);
    }

    public HashMap<String, Long> getSavedDocs() {
        return savedDocIds;
    }

    public static SavedDocumentManager getInstance() {
        return RiksdagskollenApp.getInstance().getSavedDocumentManager();
    }
}
