package org.uitagenda.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Inneke on 19/08/2015.
 */
public class FavoritesHelper
{
    private static final String PREFERENCES_NAME = "UitAgendaFavoritesPrefs";

    private static List<FavoritesListener> listeners;

    public static void addListener(FavoritesListener listener)
    {
        if(listeners == null)
            listeners = new ArrayList<>();
        listeners.add(listener);
    }

    public static void removeListener(FavoritesListener listener)
    {
        if(listeners != null)
            listeners.remove(listener);
    }

    public static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void addFavorite(Context context, String cdbid)
    {
        SharedPreferences.Editor prefs = FavoritesHelper.getPreferences(context).edit();
        prefs.putString(cdbid, "favorite");
        prefs.apply();

        if(listeners != null)
            for(FavoritesListener listener : listeners)
                listener.onFavoriteAdded(cdbid);
    }

    public static void removeFavorite(Context context, String cdbid)
    {
        SharedPreferences.Editor prefs = FavoritesHelper.getPreferences(context).edit();
        prefs.remove(cdbid);
        prefs.apply();

        if(listeners != null)
            for(FavoritesListener listener : listeners)
                listener.onFavoriteRemoved(cdbid);
    }

    public static boolean isFavorite(Context context, String cdbid)
    {
        return FavoritesHelper.getPreferences(context).contains(cdbid);
    }

    public static String getAllFavorites(Context context)
    {
        Set<String> favorites = FavoritesHelper.getPreferences(context).getAll().keySet();

        StringBuilder sb = new StringBuilder();
        for(String favorite : favorites)
        {
            if(sb.length() > 0)
                sb.append(" OR ");
            sb.append("\"").append(favorite).append("\"");
        }

        return sb.toString();
    }

    public static interface FavoritesListener
    {
        public void onFavoriteAdded(String cdbid);
        public void onFavoriteRemoved(String cdbid);
    }
}
