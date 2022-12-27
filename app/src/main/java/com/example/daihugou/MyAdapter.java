package com.example.daihugou;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final List<String> localDataSet;
    private final List<Integer> selectData = new ArrayList<>();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.text_view);
        }

        public TextView getTextView(){
            return textView;
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public MyAdapter(List<String> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.my_text_view, viewGroup, false);

        final ViewHolder holder = new ViewHolder(view);

        //クリックイベントを登録
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ViewGroup.LayoutParams lp = holder.textView.getLayoutParams();
                //ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;

                // クリックされた要素を配列として保持
                int position = holder.getAdapterPosition();
                if(selectData.contains(position)){
                    selectData.remove((Integer) position);
                    //mlp.setMargins(mlp.leftMargin,20,mlp.rightMargin,mlp.bottomMargin);
                }else {
                    selectData.add(position);
                    //mlp.setMargins(mlp.leftMargin,0,mlp.rightMargin,mlp.bottomMargin);
                }
                //holder.textView.setLayoutParams(mlp);
                Toast.makeText(v.getContext() , localDataSet.get(position), Toast.LENGTH_LONG).show();
            }
        });
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public int getSelectItemCount(){return selectData.size();}

    public List<Integer> getSelectData(){ return selectData; }

    public void ClearSelectData(){selectData.clear();}
}