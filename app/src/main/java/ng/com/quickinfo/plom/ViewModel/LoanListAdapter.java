package ng.com.quickinfo.plom.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.SumPathEffect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.R;
import ng.com.quickinfo.plom.Utils.FilterUtils;
import ng.com.quickinfo.plom.Utils.Utilities;

import static ng.com.quickinfo.plom.Utils.FilterUtils.activeLoans;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getTotalLends;

public class LoanListAdapter extends RecyclerView.Adapter<LoanListAdapter.LoanViewHolder> {

    private final LayoutInflater mInflater;
    private List<Loan> mLoans; // Cached copy of loans
    //adding a context
    Context mContext;
    private String TAG = getClass().getSimpleName();

    //interface to handle interaction with activity
    OnHandlerInteractionListener mListener;

    public LoanListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context); }

    @Override
    public LoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.loan_rv, parent, false);
        return new LoanViewHolder(itemView);
        //TODO oncreate viewholder


    }

    @Override
    public void onBindViewHolder(LoanViewHolder holder, final int position) {
        if (mLoans != null) {
            Loan current = mLoans.get(position);
            holder.loanItemView.setText(current.getName());
            holder.IdItemView.setText(current.getId()+ "");

            holder.userIdView.setText(current.getUser_id() + "");
            holder.remarksItemView.setText(Utilities.dateToString(current.getDateToRepay()));
        } else {
            // Covers the case of data not being ready yet.
            holder.loanItemView.setText("No Loan");
        }
        //testing where to insert the interface listener to enable
        //comm between RV and activity
        Utilities.log(TAG, "onbindview" + "" + getItemCount() + getTotalLends(mLoans));

        //set onclick listeners
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Utilities.log(TAG, "onclick name");
                startDetailActivity(mLoans.get(position).getId());

            }
                                               });

        //instantiate OnHandlerInteraction(created by me): useless for now.
        //as an example of a listener
        if (mContext instanceof OnHandlerInteractionListener) {
            mListener = (OnHandlerInteractionListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnHandlerInteractionListener");
        }

    }


    public void setLoans(List<Loan> loans){
        //for all loans as returned by livedata from activity, uncomment //mLoans = loans;
        //for active loans with cleared status false i.e 0
        mLoans = activeLoans(loans);
        notifyDataSetChanged();
    }


    // getItemCount() is called many times, and when it is first called,
    // mLoans has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        return FilterUtils.getItemCount(mLoans);
    }

    //startactivity
    private void startDetailActivity(long loan_id){
        if (mListener != null) {
            //TODO include total lends, borrows and total.
        mListener.onHandlerInteraction(loan_id);
        }

    }

    class LoanViewHolder extends RecyclerView.ViewHolder {
        private final TextView loanItemView;
        private TextView IdItemView;
        private TextView userIdView;
        private TextView remarksItemView;

        private LoanViewHolder(View itemView) {
            super(itemView);
            loanItemView = itemView.findViewById(R.id.departure);
            IdItemView = itemView.findViewById(R.id.departure1);
            userIdView = itemView.findViewById(R.id.city1);
            remarksItemView = itemView.findViewById(R.id.city);
        }
    }

    public interface OnHandlerInteractionListener{
        //interface to handle interation with the activity
        public void onHandlerInteraction(long loan_id);
    }
}