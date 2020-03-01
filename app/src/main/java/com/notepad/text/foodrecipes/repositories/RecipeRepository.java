package com.notepad.text.foodrecipes.repositories;

import androidx.lifecycle.LiveData;

import com.notepad.text.foodrecipes.models.Recipe;
import com.notepad.text.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;

    private RecipeApiClient mRecipeApiClient;

    public static RecipeRepository getInstance(){
        if (instance == null){
           instance = new RecipeRepository();
        }
        return instance;
    }

    public RecipeRepository() {

        mRecipeApiClient = new RecipeApiClient();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipeApiClient.getRecipes();
    }

    public void searchRecipesApi(String query, int pageNumber){
        if (pageNumber == 0){
            pageNumber = 1;
        }
        mRecipeApiClient.searchRecipesApi(query,pageNumber);
    }
}
