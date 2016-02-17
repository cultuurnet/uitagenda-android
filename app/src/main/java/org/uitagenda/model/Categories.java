package org.uitagenda.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke on 12/08/2015.
 */
public class Categories
{
    @SerializedName("category")
    private List<Label> categories;

    private static final String CATEGORY_EVENT_TYPE = "eventtype";

    public List<String> getCategories()
    {
        List<String> categories = new ArrayList<>();
        if(this.categories != null)
            for(Label category : this.categories)
                if(category != null && category.getType() != null && category.getType().contains(CATEGORY_EVENT_TYPE))
                    categories.add(category.getValue());
        return categories;
    }
}
