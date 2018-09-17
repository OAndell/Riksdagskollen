package oscar.riksdagskollen.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import oscar.riksdagskollen.R;

/**
 * Created by gustavaaro on 2018-09-15.
 */

public class ThemeManager {

    private Context appContext;
    private SharedPreferences preferences;
    private static final String CURRENT_THEME = "current_theme";

    public ThemeManager(Context appContext) {
        this.appContext = appContext;
        preferences = appContext.getSharedPreferences("theme", Context.MODE_PRIVATE);
        currentTheme = Theme.getTheme(preferences.getString(CURRENT_THEME, "default"));
    }

    public enum Theme {
        DEFAULT("default", "Standard", "motion_style.css", R.style.DefaultTheme, R.style.DefaultTheme_NoActionBar),
        BLACK("black", "AMOLED-svart", "motion_style_black.css", R.style.BlackTheme, R.style.BlackTheme_NoActionBar);
        int id;
        int noActionBarTheme;
        String name;
        String displayname;
        String css;

        Theme(String name, String displayName, String css, int id, int noActionBar) {
            this.id = id;
            this.noActionBarTheme = noActionBar;
            this.name = name;
            this.displayname = displayName;
            this.css = css;
        }

        public static Theme getTheme(String name) {
            switch (name) {
                case "default":
                    return Theme.DEFAULT;
                case "black":
                    return Theme.BLACK;
                default:
                    return DEFAULT;
            }
        }

        public static CharSequence[] getDisplayNames() {
            CharSequence[] dispNames = new CharSequence[values().length];
            for (int i = 0; i < dispNames.length; i++) {
                dispNames[i] = values()[i].displayname;
            }
            return dispNames;
        }

        public String getName() {
            return name;
        }

        public String getDisplayname() {
            return displayname;
        }

        public String getCss() {
            return css;
        }
    }

    Theme currentTheme = Theme.DEFAULT;


    public int getCurrentTheme(boolean useThemeWithoutActionBar) {
        if (useThemeWithoutActionBar) return currentTheme.noActionBarTheme;
        else return currentTheme.id;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }


    public int getCurrentThemeIndex() {
        for (int i = 0; i < Theme.values().length; i++) {
            if (Theme.values()[i] == currentTheme) return i;
        }

        return 0;
    }

    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        preferences.edit().putString(CURRENT_THEME, theme.name).apply();
    }

}
