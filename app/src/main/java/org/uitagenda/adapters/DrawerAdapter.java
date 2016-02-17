package org.uitagenda.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.uitagenda.R;

/**
 * Created by Inneke on 11/08/2015.
 */
public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private OnItemClickListener listener;
    private int layoutItem;
    private String[] titles;
    private int[] icons;

    public static final int POSITION_CURRENT = 0;
    public static final int POSITION_SEARCH = 1;
    public static final int POSITION_FAVORITES = 2;
    public static final int POSITION_SEARCH_HISTORY = 3;
    public static final int POSITION_ABOUT = 4;

    public DrawerAdapter(Context context, OnItemClickListener listener)
    {
        this.listener = listener;
        this.layoutItem = R.layout.listitem_drawer;
        this.titles = context.getResources().getStringArray(R.array.drawer_items);
        this.icons = new int[]{
                R.drawable.drawer_current,
                R.drawable.drawer_search,
                R.drawable.drawer_favorites,
                R.drawable.drawer_search_history,
                R.drawable.drawer_about
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(this.layoutItem, viewGroup, false);
        return new DrawerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        this.onBindDrawerViewHolder((DrawerViewHolder) viewHolder, i);
    }

    private void onBindDrawerViewHolder(DrawerViewHolder viewHolder, int i)
    {
        viewHolder.textViewTitle.setText(this.titles[i]);
        viewHolder.imageViewIcon.setImageResource(this.icons[i]);
    }

    @Override
    public int getItemCount()
    {
        return this.titles.length;
    }

    public class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textViewTitle;
        ImageView imageViewIcon;

        public DrawerViewHolder(View itemView)
        {
            super(itemView);

            this.textViewTitle = (TextView)itemView.findViewById(R.id.textView_title);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(DrawerAdapter.this.listener != null)
                DrawerAdapter.this.listener.onDrawerItemClick(this.getAdapterPosition(), DrawerAdapter.this.titles[this.getAdapterPosition()]);
        }
    }

    public static interface OnItemClickListener
    {
        public void onDrawerItemClick(int position, String title);
    }
}
