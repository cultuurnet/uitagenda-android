package org.uitagenda.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import org.uitagenda.R;
import org.uitagenda.model.FormattedTimestamp;

import java.util.List;

/**
 * Created by Inneke on 20/08/2015.
 */
public class TimestampAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private int layoutItem;
    private List<FormattedTimestamp> timestamps;

    private int selectedPosition;

    public TimestampAdapter(List<FormattedTimestamp> timestamps, int selectedPosition)
    {
        this.layoutItem = R.layout.listitem_timestamp;
        this.timestamps = timestamps;
        this.selectedPosition = selectedPosition;
    }

    public FormattedTimestamp getTimestampForAdapterPosition(int i)
    {
        if (this.timestamps != null && this.timestamps.size() > i)
            return this.timestamps.get(i);

        return null;
    }

    public int getSelectedTimestampPosition()
    {
        return this.selectedPosition;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(this.layoutItem, viewGroup, false);
        return new TimestampViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        this.onBindTimestampViewHolder((TimestampViewHolder) viewHolder, i);
    }

    private void onBindTimestampViewHolder(TimestampViewHolder viewHolder, int i)
    {
        FormattedTimestamp timestamp = this.getTimestampForAdapterPosition(i);
        viewHolder.textViewTitle.setText(timestamp.getTimeText());
        viewHolder.radioButtonControl.setChecked(i == this.selectedPosition);
    }

    @Override
    public int getItemCount()
    {
        return this.timestamps != null ? this.timestamps.size() : 0;
    }

    public class TimestampViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textViewTitle;
        RadioButton radioButtonControl;

        public TimestampViewHolder(View itemView)
        {
            super(itemView);

            this.textViewTitle = (TextView) itemView.findViewById(R.id.textView_title);
            this.radioButtonControl = (RadioButton) itemView.findViewById(R.id.radioButton_control);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int previouslySelectedPosition =  TimestampAdapter.this.selectedPosition;
            TimestampAdapter.this.selectedPosition = this.getAdapterPosition();
            if (previouslySelectedPosition !=  TimestampAdapter.this.selectedPosition)
            {
                TimestampAdapter.this.notifyItemChanged(previouslySelectedPosition);
                TimestampAdapter.this.notifyItemChanged(TimestampAdapter.this.selectedPosition);
            }
        }
    }
}
