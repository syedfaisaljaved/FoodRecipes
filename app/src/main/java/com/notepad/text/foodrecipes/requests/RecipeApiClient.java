package com.notepad.text.foodrecipes.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.notepad.text.foodrecipes.AppExecuters;
import com.notepad.text.foodrecipes.models.Recipe;
import com.notepad.text.foodrecipes.requests.responses.RecipeResponse;
import com.notepad.text.foodrecipes.requests.responses.RecipeSearchResponse;
import com.notepad.text.foodrecipes.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.notepad.text.foodrecipes.utils.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";

    private static RecipeApiClient instance;
    private static RetrieveRecipesRunnables mRetrieveRecipesRunnable;
    private MutableLiveData<List<Recipe>> mRecipes;
    private static RetrieveRecipeRunnables mRetrieveRecipeRunnable;
    private MutableLiveData<Recipe> mRecipe;

    public static RecipeApiClient getInstance(){
        if (instance == null){
            instance = new RecipeApiClient();
        }
        return instance;
    }

    public RecipeApiClient() {

        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }

    public void searchRecipesApi(String query, int pageNumber){
        if (mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable = null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnables(query, pageNumber);
        final Future handler = AppExecuters.getInstance().networkIO().submit(mRetrieveRecipesRunnable);

        AppExecuters.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //let user know its timed out
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchRecipebyId(String recipeId){
        if (mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable = null;
        }
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnables(recipeId);
        final Future handler = AppExecuters.getInstance().networkIO().submit(mRetrieveRecipeRunnable);

        AppExecuters.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //let user know its timed out
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveRecipesRunnables implements Runnable {

        private String query;
        private int pageNumber;
        private boolean cancelRequest;


        public RetrieveRecipesRunnables(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query,pageNumber).execute();
                if (cancelRequest){
                    return;
                }

                if (response.code() == 200){
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
                    if (pageNumber == 1){
                        mRecipes.postValue(list);
                    }else {
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(list);
                        mRecipes.postValue(currentRecipes);
                    }
                }else {
                    String error = response.errorBody().string();
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }

        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber){
            return ServiceGenerator.getRecipeApi().searchRecipe(
                    Constants.API_KEY,
                    query,
                    String.valueOf(pageNumber)
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: cancelling the search request");
            cancelRequest = true;
        }
    }

    private class RetrieveRecipeRunnables implements Runnable {

        private String recipeId;
        private boolean cancelRequest;


        public RetrieveRecipeRunnables(String recipeId) {
            this.recipeId = recipeId;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe(recipeId).execute();
                if (cancelRequest){
                    return;
                }

                if (response.code() == 200){
                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe();
                    mRecipe.postValue(recipe);

                }else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error );
                    mRecipe.postValue(null);
                }
            } catch(IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }

        }

        private Call<RecipeResponse> getRecipe(String recipeId){
            return ServiceGenerator.getRecipeApi().getRecipe(
                    Constants.API_KEY,
                    recipeId
            );
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: cancelling the search request");
            cancelRequest = true;
        }
    }

    public void cancelRequest(){
        if (mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable.cancelRequest();
        }
        if (mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable.cancelRequest();
        }
    }


}
