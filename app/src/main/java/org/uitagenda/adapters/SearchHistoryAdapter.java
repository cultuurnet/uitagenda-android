package org.uitagenda.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.uitagenda.R;
import org.uitagenda.database.SearchTable;

/**
 * Created by Inneke on 21/08/2015.
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private int layoutItem;
    private OnItemClickListener listener;
    private Cursor cursor;

    public SearchHistoryAdapter()
    {
        this.layoutItem = R.layout.listitem_search_history;
    }

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public void setCursor(Cursor cursor)
    {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    public Cursor getCursor()
    {
        return this.cursor;
    }

    public int getSearchId(int position)
    {
        if(this.cursor != null && this.cursor.moveToPosition(position))
            return this.cursor.getInt(this.cursor.getColumnIndex(SearchTable.COLUMN_ID));
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType)
    {
        View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(this.layoutItem, viewGroup, false);
        return new SearchViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        this.onBindSearchViewHolder((SearchViewHolder) viewHolder, i);
    }

    private void onBindSearchViewHolder(SearchViewHolder viewHolder, int i)
    {
        this.cursor.moveToPosition(i);

        String completeQuery = this.cursor.getString(this.cursor.getColumnIndex(SearchTable.COLUMN_COMPLETE_QUERY));

        viewHolder.textViewCompleteQuery.setText(completeQuery);
    }

    @Override
    public int getItemCount()
    {
        return this.cursor != null ? this.cursor.getCount() : 0;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textViewCompleteQuery;

        public SearchViewHolder(View itemView)
        {
            super(itemView);

            this.textViewCompleteQuery = (TextView) itemView.findViewById(R.id.textView_completeQuery);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(SearchHistoryAdapter.this.listener != null)
                SearchHistoryAdapter.this.listener.onSearchClick(v, this.getAdapterPosition());
        }
    }

    public static interface OnItemClickListener
    {
        public void onSearchClick(View caller, int position);
    }
}
