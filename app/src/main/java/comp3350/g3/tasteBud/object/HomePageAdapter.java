package comp3350.g3.tasteBud.object;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import comp3350.g3.tasteBud.R;
import comp3350.g3.tasteBud.ui.IListInteraction;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class HomePageAdapter extends BaseQuickAdapter<Recipe, HomePageAdapter.ViewHolder> {
    //BaseQuickAdapter is a more powerful(simpler) tool for converting Recipes into a list

    private boolean selectionMode;
    private IListInteraction listInteraction;
    private RecyclerView recycler;
    private ArrayList<Recipe> selectedItems;

    private Context context;

    public HomePageAdapter(Context context, IListInteraction listInteraction, RecyclerView recycler) {
        super(R.layout.view_recipe);
        this.context = context;
        selectionMode = false;
        this.listInteraction = listInteraction;
        this.recycler = recycler;
        this.selectedItems = new ArrayList<>();
        bindToRecyclerView(recycler);
    }

    @Override
    public ViewHolder createBaseViewHolder(android.view.View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void convert(ViewHolder helper, Recipe recipe) {
        String tags = recipe.getTags().toString();
        ;
        tags = tags.replace("[", "").replace("]", "");

        helper.setText(R.id.tvTitle, recipe.getName());
        helper.setText(R.id.num2, tags);

        if (getData().get(helper.getAbsoluteAdapterPosition()).isSelected()) {
            helper.checkbox.setVisibility(View.VISIBLE);
            helper.background.setBackgroundColor(Color.LTGRAY);
        } else {
            helper.checkbox.setVisibility(View.GONE);
            helper.background.setBackgroundColor(Color.WHITE);
        }

        //Handling image viewing
        String imagePath = recipe.getImageUri();

        if (imagePath != null && !imagePath.isEmpty()) {
            int imageId = context.getResources().getIdentifier(imagePath, "drawable", context.getPackageName());

            if (imageId != 0) {
                // This image is a default image located in the drawable folder.
                Glide.with(context).load(imageId).into(helper.img);
            } else {
                // This image is a user-picked one located in the device file system.
                Glide.with(context).load(Uri.parse(imagePath)).into(helper.img);
            }
        } else {
            // No image available, so a placeholder image.
            helper.img.setImageResource(R.mipmap.recipedefault);
        }

    }


    public void removeAllSelections() {
        final int dataSize = this.getData().size();
        for (int i = 0; i < dataSize; i++) {
            getData().get(i).changeSelection(Recipe.NOT_SELECTED);
            notifyItemChanged(i);
        }
    }

    public List<Recipe> getSelectedItems() {

        return selectedItems;
    }

    public void onSelectionMode() {
        selectionMode = true;
    }

    public void offSelectionMode() {
        selectionMode = false;
        selectedItems.clear();
        removeAllSelections();
    }


    public class ViewHolder extends BaseViewHolder {
        // initialize variables
        LinearLayout background;
        ImageView checkbox;
        TextView title, tags;

        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // assign variables
            background = itemView.findViewById(R.id.linearLayout);
            checkbox = itemView.findViewById(R.id.imageViewCheckMark);
            title = itemView.findViewById(R.id.tvTitle);
            tags = itemView.findViewById(R.id.num2);
            img = itemView.findViewById(R.id.img); // Add this line
            setClicks();

        }

        private void setClicks() {
            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (selectionMode) {
                    Recipe recipe = getData().get(position);
                    if (!recipe.isSelected()) {
                        addSelections(position, recipe);

                    } else {
                        removeSelections(position, recipe);
                    }
                } else {
                    listInteraction.onClickListItem(position);
                }
                Log.d(null, selectedItems.size() + " : " + position);
                onBindViewHolder(this, position);
            });

            itemView.setOnLongClickListener(v -> {
                if (!selectionMode) {
                    int position = getAbsoluteAdapterPosition();
                    Recipe recipe = getData().get(position);
                    listInteraction.onHoldListItem(position);
                    onSelectionMode();
                    addSelections(position, recipe);
                    onBindViewHolder(this, position);
                }
                return true;
            });
        }

        private void addSelections(int position, Recipe recipe) {
            selectedItems.add(getData().get(position));
            recipe.changeSelection(Recipe.SELECTED);

        }

        private void removeSelections(int position, Recipe recipe) {
            if (selectionMode) {
                recipe.changeSelection(Recipe.NOT_SELECTED);
                selectedItems.remove(recipe);
            }

        }
    }

}