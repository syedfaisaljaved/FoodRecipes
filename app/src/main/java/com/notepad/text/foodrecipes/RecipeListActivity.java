package com.notepad.text.foodrecipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.notepad.text.foodrecipes.adapters.OnRecipeListener;
import com.notepad.text.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.notepad.text.foodrecipes.models.Recipe;

import com.notepad.text.foodrecipes.utils.VerticalSpacingItemDerecorator;
import com.notepad.text.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecipeRecyclerAdapter mRecipeRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);

        initRecyclerView();
        subscribeObservers();
        initSearchView();

        if (!mRecipeListViewModel.isViewingRecipes()){
            //display search categories
            displaySearchCategories();
        }

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }

    public void subscribeObservers(){

        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes !=null){
                    if (mRecipeListViewModel.isViewingRecipes()){
                        mRecipeListViewModel.setIsPerformingQuery(false);
                        mRecipeRecyclerAdapter.setRecipes(recipes);
                    }
                }
            }
        });
    }


    private void initRecyclerView(){
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
        VerticalSpacingItemDerecorator itemDecorator = new VerticalSpacingItemDerecorator(5);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!mRecyclerView.canScrollVertically(1)){
                    //search the page
                    mRecipeListViewModel.searchNextpage();
                }
            }
        });
    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mRecipeRecyclerAdapter.displayLoading();
                mRecipeListViewModel.searchRecipesApi(s,1);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    @Override
    public void onRecipeClick(int position) {

        Intent intent = new Intent(this, RecipeActivty.class);
        intent.putExtra("recipe", mRecipeRecyclerAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {

        mRecipeRecyclerAdapter.displayLoading();
        mRecipeListViewModel.searchRecipesApi(category,1);
        mSearchView.clearFocus();
    }

    private void displaySearchCategories(){
        mRecipeListViewModel.setIsViewingRecipes(false);
        mRecipeRecyclerAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.onBackPressed()) {
            super.onBackPressed();
        }
        else {
            displaySearchCategories();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_categories){
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
