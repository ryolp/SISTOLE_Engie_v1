package enruta.sistole_engie.services;

import android.database.Cursor;

public class DbBaseMgr {
    public static int getInt(Cursor c, String columnName, int defaultValue) {
        int idx;

        if (c == null)
            return defaultValue;

        idx = c.getColumnIndex(columnName);

        if (idx < 0)
            return defaultValue;

        return c.getInt(idx);
    }

    public static long getLong(Cursor c, String columnName, long defaultValue) {
        int idx;

        if (c == null)
            return defaultValue;

        idx = c.getColumnIndex(columnName);

        if (idx < 0)
            return defaultValue;

        return c.getLong(idx);
    }

    public static String getString(Cursor c, String columnName, String defaultValue) {
        int idx;

        if (c == null)
            return defaultValue;

        idx = c.getColumnIndex(columnName);

        if (idx < 0)
            return defaultValue;

        return c.getString(idx);
    }
}
