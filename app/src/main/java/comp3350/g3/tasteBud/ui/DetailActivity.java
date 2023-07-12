package comp3350.g3.tasteBud.ui;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import comp3350.g3.tasteBud.R;
import comp3350.g3.tasteBud.logic.Messages;
import comp3350.g3.tasteBud.logic.RecipeProcessor;
import comp3350.g3.tasteBud.object.Recipe;
import comp3350.g3.tasteBud.logic.PersistenceSingleton;

public class DetailActivity extends FragmentActivity implements DeleteInteraction {
    TextView recipeTitle;
    TextView recipeDescription;
    TextView recipeTags;
    TextView recipeIngredients;
    RecipeProcessor recipeProcessor;
    Recipe recipe;

    ImageView recipeImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Connect with Layout File:"detail_activity"
        setContentView(R.layout.detail_activity);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeTags = findViewById(R.id.recipeTags);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipeImage = findViewById(R.id.recipeImageDetail);

        //Create a Recipe Processor to link to the logic layer
        recipeProcessor = new RecipeProcessor(PersistenceSingleton.getInstance().GetIsPersistence());

        //Create the instance of Recipe to get information of each recipe
        recipe = (Recipe) getIntent().getSerializableExtra("bean");
        recipeTitle.setText(recipe.getName());
        recipeDescription.setText(recipe.getDesc());

        //Handling Image Viewing
        String imagePath = recipe.getImageUri();

        if (imagePath != null && !imagePath.isEmpty()) {
            int imageId = getResources().getIdentifier(imagePath, "drawable", getPackageName());

            if (imageId != 0) {
                // This image is a default image located in the drawable folder.
                Glide.with(this).load(imageId).into(recipeImage);
            } else {
                // This image is a user-picked one located in the device file system.
                Glide.with(this).load(Uri.parse(imagePath)).into(recipeImage);
            }
        } else {
            // No image available, so a placeholder image.
            recipeImage.setImageResource(R.mipmap.recipedefault);
        }


        String tagCollection = "";
        for (int n = 0; n < recipe.getTags().size(); n++) {
            String tag = TextUtils.isEmpty(tagCollection) ? "" : "" + tagCollection + ",";
            tagCollection = tag + (recipe.getTags().get(n)); //to get appropriately indexed tag
        }
        recipeTags.setText(tagCollection);

        String ingredientsCollection = "";
        for (int n = 0; n < recipe.getIngredients().size(); n++) {
            String ingredients = TextUtils.isEmpty(ingredientsCollection) ? "" : "" + ingredientsCollection + ",";
            ingredientsCollection = ingredients + (recipe.getIngredients().get(n)); //to get appropriately indexed ingredients
        }
        recipeIngredients.setText(ingredientsCollection);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        findViewById(R.id.delete).setOnClickListener(v -> {

            Messages.buildWarningDeleteDialogue(findViewById(R.id.delete).getContext(), "Are you sure you want to delete this recipe?", this);
        });
    }

    public void delete() {
        recipeProcessor.deleteRecipe(recipe.getId());
        finish();
    }
}
