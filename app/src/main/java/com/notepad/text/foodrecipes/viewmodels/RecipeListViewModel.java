package com.notepad.text.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.notepad.text.foodrecipes.models.Recipe;
import com.notepad.text.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipes;
    private boolean mIsPerformingQuery;

    public RecipeListViewModel() {
        mRecipeRepository = new RecipeRepository();
        mIsPerformingQuery = false;
    }

    public LiveData<List<Recipe>> getRecipes(){

        return mRecipeRepository.getRecipes();
    }

    public void searchRecipesApi(String query, int pageNumber){
        mIsViewingRecipes = true;
        mIsPerformingQuery = true;
        mRecipeRepository.searchRecipesApi(query,pageNumber);
    }

    public void searchNextpage(){
        if (!mIsPerformingQuery && mIsViewingRecipes){
            mRecipeRepository.searchNextPage();
        }
    }

    public boolean isViewingRecipes(){
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes){
         mIsViewingRecipes = isViewingRecipes;
    }

    public void setIsPerformingQuery(boolean isPerformingQuery){
        mIsPerformingQuery = isPerformingQuery;
    }

    public boolean isPerformingQuery(){
        return mIsPerformingQuery;
    }

    public boolean onBackPressed(){
        if (mIsPerformingQuery){
            //cancel the request
            mRecipeRepository.cancelrequest();
            mIsPerformingQuery = false;
        }
        else if (mIsViewingRecipes){
            mIsViewingRecipes = false;
            return false;
        }
        return true;
    }
}
