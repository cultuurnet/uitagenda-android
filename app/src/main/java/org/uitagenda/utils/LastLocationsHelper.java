package org.uitagenda.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.uitagenda.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 21/08/2015.
 */
public class LastLocationsHelper
{
    private static final String PREFERENCES_NAME = "UitAgendaLocationPrefs";

    private static final String PREFERENCE_LAST_LOCATIONS = "last_locations";

    public static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static List<City> getLastLocations(Context context)
    {
        String citiesJson = LastLocationsHelper.getPreferences(context).getString(PREFERENCE_LAST_LOCATIONS, null);

        if(TextUtils.isEmpty(citiesJson))
            return null;

        City[] cityArray = new Gson().fromJson(citiesJson, City[].class);

        List<City> lastCities = new ArrayList<>();

        if(cityArray != null)
            for(City city : cityArray)
                lastCities.add(city);

        return lastCities;
    }

    public static void addLocationAndSave(Context context, City city)
    {
        if(city == null)
            return;

        List<City> cities = LastLocationsHelper.getLastLocations(context);

        if(cities == null)
            cities = new ArrayList<>();
        else if(cities.contains(city))
            cities.remove(city);
        else if(cities.size() >= 5)
            cities.remove(cities.size() - 1);

        cities.add(0, city);

        String citiesJson = new Gson().toJson(cities);

        SharedPreferences.Editor prefs = LastLocationsHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_LAST_LOCATIONS, citiesJson);
        prefs.commit();
    }
}
