package ng.com.quickinfo.plom.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.R;
import ng.com.quickinfo.plom.Utils.FilterUtils;
import ng.com.quickinfo.plom.Utils.Utilities;
;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getTotalSum;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isDueSoon;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isOverDue;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isToday;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString1;

public class LoanListAdapter extends RecyclerView.Adapter<LoanListAdapter.LoanViewHolder> {

    private final LayoutInflater mInflater;
    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    String currency;
    int reminderDays;

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
        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(mContext);
        editor = sharedPref.edit();
        currency = sharedPref.getString("currency","N" );
        reminderDays = sharedPref.getInt("reminderDays", 7);


        return new LoanViewHolder(itemView);
        //TODO oncreate viewholder


    }

    @Override
    public void onBindViewHolder(LoanViewHolder holder, final int position) {
        if (mLoans != null) {
            int amount;

            Loan loan = mLoans.get(position);
            amount = loan.getAmount();
            holder.nameView.setText(loan.getName());
            holder.dateTakenView.setText(dateToString1(loan.getDateToRepay()));
            holder.amountView.setText(currency + amount + "");
            //remarks
            if(!loan.getRemarks().isEmpty()){
                holder.remarksView.setVisibility(View.VISIBLE);
                holder.remarksView.setText(loan.getRemarks());
            }
            //offset balance
            if(loan.getOffset()!=0){
                //get)ffsetTotal - AmountgetoffsetTotal -ge
                int offsets = getOffsetTotal(loan.getId());
                holder.amountView.setText(currency + (amount - offsets) );
                holder.balanceView.setVisibility(View.VISIBLE);

            }

            //loantype
            if(loan.getLoanType()!=0){
                holder.borrowView.setVisibility(View.VISIBLE);
                holder.lendView.setVisibility(View.VISIBLE);
            }

            //clear
            if(loan.getClearStatus()!=0){
                holder.llClear.setVisibility(View.VISIBLE);

            }
            Date date = loan.getDateToRepay();
            //duesoon
            Boolean isOn = false;
            String comment = "";
            int color = R.color.green;

            if(isToday(date)){
                isOn = true;
                comment = "Due Today";
                color = R.color.date_due;
            }
            else if (isDueSoon(date, reminderDays)){
                isOn = true;
                comment = "Due Soon";
                color = R.color.date_duesoon;


            }
            else if (isOverDue(date)){
                isOn = true;
                comment = "Over Due";
                color = R.color.date_overdue;
            }
            //notify
            if (isOn){
                holder.llNotify.setVisibility(View.VISIBLE);
                holder.commentView.setText(comment);
                holder.alertView.setBackgroundColor(color);
                if(loan.getNotify()!=0){
                    holder.notifyOff.setVisibility(View.GONE);
                    holder.notifyOn.setVisibility(View.VISIBLE);
                }


            }


        } else {
            // Covers the case of data not being ready yet.
            holder.nameView.setText("No Loan");
        }
        //testing where to insert the interface listener to enable
        //comm between RV and activity
        Utilities.log(TAG, "onbindview" + "" + getItemCount() + getTotalSum(mLoans));

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

    private boolean isOnNotice(Date date, int reminderDays) {

        return (isToday(date) || isDueSoon(date, reminderDays) || isOverDue(date));
    }

    private int getOffsetTotal(long id) {

        return 1;
    }


    public void setLoans(List<Loan> loans){
        //for all loans as returned by livedata from activity, uncomment //mLoans = loans;
        //for active loans with cleared status false i.e 0
        mLoans = loans;
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
        private TextView nameView, amountView,  dateTakenView, commentView, remarksView, balanceView;
        private ImageView borrowView, lendView, notifyOn, notifyOff, alertView;
        private LinearLayout llClear, llNotify;

        private LoanViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tvLRVName);
            amountView = itemView.findViewById(R.id.tvLRVAmount);
            dateTakenView = itemView.findViewById(R.id.tvLRVDate);
            commentView = itemView.findViewById(R.id.tvLRVtime);
            balanceView = itemView.findViewById(R.id.tvBalanceGone);
            remarksView = itemView.findViewById(R.id.tvLRVRemarks);


            borrowView = itemView.findViewById(R.id.ivLoanTypeGone);
            lendView = itemView.findViewById(R.id.ivLoanType);
            notifyOn= itemView.findViewById(R.id.ivLRVNotifyOn);
            notifyOff = itemView.findViewById(R.id.ivLRVNotifyOff);
            alertView = itemView.findViewById(R.id.ivTLRAlert);

            llClear = itemView.findViewById(R.id.llClearGone);
            llNotify = itemView.findViewById(R.id.llNotifyGone);


        }
    }

    public interface OnHandlerInteractionListener{
        //interface to handle interation with the activity
        public void onHandlerInteraction(long loan_id);
    }
}