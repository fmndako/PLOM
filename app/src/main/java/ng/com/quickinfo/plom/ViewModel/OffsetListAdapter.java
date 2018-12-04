package ng.com.quickinfo.plom.ViewModel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.R;
import ng.com.quickinfo.plom.Utils.Utilities;

public class OffsetListAdapter extends RecyclerView.Adapter<OffsetListAdapter.OffsetViewHolder> {

    private final LayoutInflater mInflater;
    private List<Offset> mOffsets; // Cached copy of loans
    //adding a context
    Context mContext;
    private String TAG = getClass().getSimpleName();

    //interface to handle interaction with activity
    OnHandlerInteractionListener mListener;

    public OffsetListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context); }

    @Override
    public OffsetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.offset_rv, parent, false);
        return new OffsetViewHolder(itemView);
        //TODO oncreate viewholder


    }

    @Override
    public void onBindViewHolder(OffsetViewHolder holder, final int position) {
        if (mOffsets != null) {
            Offset current = mOffsets.get(position);
            holder.remarksView.setText(current.getRemarks());
            holder.amountView.setText(current.getDateOffset().toString());
            holder.dateTakenView.setText(Utilities.dateToString1(current.getDateOffset()));
//            holder.commentView.setText("comment");


        } else {
            // Covers the case of data not being ready yet.
            holder.remarksView.setText("No Offset");
        }
        //testing where to insert the interface listener to enable
        //comm between RV and activity
        //Utilities.log(TAG, "onbindview" + "" + getItemCount() + getTotalLends(mOffsets));

        //set onclick listeners
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                Utilities.log(TAG, "onclick name");
//                startDetailActivity(mOffsets.get(position).getId());
//
//            }
//                                               });

        //instantiate OnHandlerInteraction(created by me): useless for now.
        //as an example of a listener
        if (mContext instanceof OnHandlerInteractionListener) {
            mListener = (OnHandlerInteractionListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnHandlerInteractionListener");
        }

    }


    public void setOffsets(List<Offset> offsets){
        //for all loans as returned by livedata from activity, uncomment //mOffsets = loans;
        //for active loans with cleared status false i.e 0
        mOffsets = offsets;
        notifyDataSetChanged();
    }


    // getItemCount() is called many times, and when it is first called,
    // mOffsets has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        return 0;
    }

    public int getTotal(){return 0;}

    //startactivity
    private void startDetailActivity(long loan_id){
        if (mListener != null) {
            //TODO include total lends, borrows and total.
        mListener.onHandlerInteraction(loan_id);
        }

    }

    class OffsetViewHolder extends RecyclerView.ViewHolder {
        private TextView remarksView, amountView, dateTakenView;
        private ImageView cashView;

        private OffsetViewHolder(View itemView) {
            super(itemView);
            remarksView = itemView.findViewById(R.id.tvORVRemarks);
            amountView = itemView.findViewById(R.id.tvORVAmount);
            dateTakenView = itemView.findViewById(R.id.tvORVDateOffset);
//            dateRepayView = itemView.findViewById(R.id.tvLRVDateRepay);
//            commentView = itemView.findViewById(R.id.tvLRVtime);
//
//            clearView = itemView.findViewById(R.id.ivLRVClear);
//            cashView = itemView.findViewById(R.id.ivLRVCash);
//            notifyView= itemView.findViewById(R.id.ivLRVNotify);
//               alertView = itemView.findViewById(R.id.ivTLRAlert);
        }
    }

    public interface OnHandlerInteractionListener{
        //interface to handle interation with the activity
        public void onHandlerInteraction(long loan_id);
    }
}