package com.example.daihugou;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.ViewHolder> {

    private final List<Integer> iImages;
    private final List<Integer> selectData = new ArrayList<>();
    private final List<View> views = new ArrayList<>();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_view);
        }

        public ImageView getImageView(){
            return imageView;
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet Integer[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public MyImageAdapter(List<Integer> dataSet) {
        iImages = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.my_image_view, viewGroup, false);

        final ViewHolder holder = new ViewHolder(view);

        //クリックイベントを登録
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ViewGroup.LayoutParams lp =  v.getLayoutParams();
                //ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;

                // クリックされた要素を配列として保持
                int position = holder.getAdapterPosition();
                if(selectData.contains(position)){
                    selectData.remove((Integer) position);
                    views.remove(v);
                    v.setTranslationY(v.getY()+30);
                    //mlp.setMargins(mlp.leftMargin,0,mlp.rightMargin,mlp.bottomMargin);
                }else {
                    selectData.add(position);
                    views.add(v);
                    v.setTranslationY(v.getY()-30);
                    //mlp.setMargins(mlp.leftMargin,50,mlp.rightMargin,mlp.bottomMargin);
                }
                //v.setLayoutParams(mlp);
            }
        });
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getImageView().setImageResource(iImages.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return iImages.size();
    }

    public List<Integer> getSelectData(){
        for (View v : views) {
            v.setTranslationY(v.getY()+30);
        }
        views.clear();
        return selectData;
    }

    public void ClearSelectData(){selectData.clear();}
}