package org.uitagenda.utils;

import android.content.Context;

import com.google.gson.Gson;

import org.uitagenda.model.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Inneke on 21/08/2015.
 */
public class CityHelper
{
    private static List<City> cities;

    public static List<City> getCities(Context context)
    {
        if(cities == null)
        {
            BufferedReader bufferedReader = null;
            StringBuilder json = new StringBuilder();
            try
            {
                bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("cities.json")));

                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    json.append(line);
                }
            }
            catch(IOException e)
            {
                e.getMessage();
            }
            finally
            {
                try
                {
                    if (bufferedReader != null)
                        bufferedReader.close();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
            }

            City[] cityArray = new Gson().fromJson(json.toString(), City[].class);
            cities = cityArray != null ? Arrays.asList(cityArray) : null;
        }

        return cities;
    }
}
