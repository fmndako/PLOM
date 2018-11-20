package ng.com.quickinfo.plom.ViewModel;

import android.content.Context;
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

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.R;
import ng.com.quickinfo.plom.Utils.Utilities;

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
    public void onBindViewHolder(LoanViewHolder holder, int position) {
        if (mLoans != null) {
            Loan current = mLoans.get(position);
            holder.loanItemView.setText(current.getName());
            holder.IdItemView.setText(current.getId()+ "");

            holder.userIdView.setText(current.getUser_id() + "");
        } else {
            // Covers the case of data not being ready yet.
            holder.loanItemView.setText("No Loan");
        }
        //testing where to insert the interface listener to enable
        //comm between RV and activity
        Utilities.log(TAG, "onbindview" + "" + getItemCount() + getTotalLends());

        //set onclick listeners
        holder.loanItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Utilities.log(TAG, "onclick name");

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
//        if (mListener != null) {
//            //TODO include total lends, borrows and total.
//            mListener.onHandlerInteraction(getTotalLends());
//        }
    }


    public void setLoans(List<Loan> loans){
        //for all loans as returned by livedata from activity, uncomment //mLoans = loans;
        //for active loans with cleared status false i.e 0
        mLoans = activeLoans(loans);
        notifyDataSetChanged();
    }

    //clear status filters
    public List<Loan> activeLoans (List<Loan> loans){
        //returns all active loans
        List<Loan> activeLoans = new ArrayList<>();
        for (Loan loan: loans){
            if (loan.getClearStatus()==0){
                activeLoans.add(loan);
            }
        }
        return activeLoans;
    }

    //loan Type filter filters
    public List<List<Loan>> loanType (List<Loan> loans){
        //returns a list of list of lends and borrow
        List<List<Loan>> typeLoan = new ArrayList<>();
        List<Loan> lendLoans = new ArrayList<>();
        List<Loan> borrowLoans = new ArrayList<>();

        for (Loan loan: loans){
            if (typeIsLend(loan)){
                lendLoans.add(loan);
            }else {borrowLoans.add(loan);}
        }
        typeLoan.add(lendLoans);
        typeLoan.add(borrowLoans);
        return typeLoan;
    }

    //repayment date filters (due date)
    //loan Type filter filters
    public List<List<Loan>> dateFilterList (List<Loan> loans){
        //returns a list(3) of list of loans by date filters
        List<List<Loan>> dateList = new ArrayList<>();
        List<Loan> dueSoonLoans = new ArrayList<>();
        List<Loan> dueLoans = new ArrayList<>();
        List<Loan> overDueLoans = new ArrayList<>();

        Date today = Calendar.getInstance().getTime();
        for (Loan loan: loans){
            if (loanIsDueSoon(loan, today, 7)){
                dueSoonLoans.add(loan);
            }else if (loanIsDue(loan, today)){
                dueLoans.add(loan);}
                else if (loanIsOverDue(loan, today)){
                overDueLoans.add(loan);
            }
        }
        dateList.add(dueSoonLoans);
        dateList.add(dueLoans);
        dateList.add(overDueLoans);
        return dateList;
    }

    //filter helper functions (loan type)
    public boolean typeIsLend(Loan loan){
        return (loan.getLoanType() == 0);
    }

    //time filter helper functions
    public boolean loanIsDue(Loan loan, Date today){
        //returns true if datetorepay is same as today
        return loan.getDateToRepay().equals(today);
    }
    public boolean loanIsDueSoon(Loan loan,Date today,  int days){
        //returns true if diff between datetorepay and today is less than specified days
         //and not any of the other options
        return (!loanIsDue(loan, today) & !loanIsOverDue(loan, today)) &
                (loan.getDateToRepay().compareTo(today)) < days;
    }
    public boolean loanIsOverDue(Loan loan, Date today){
        //returns true if date of repayment is after today
        return (loan.getDateToRepay().after(today));
    }



    // getItemCount() is called many times, and when it is first called,
    // mLoans has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mLoans != null)
            return mLoans.size();
        else return 0;
    }

    public int getTotalLends(){
        int sum = 0;
        if (mLoans != null){

            for (int x = 0; x<mLoans.size(); x++ ){
            sum += mLoans.get(x).getAmount();
            }
            return sum;
        }
        else
            {return sum;}
        //Utilities.log("LoanListAdpater", sum + "");
    }

    class LoanViewHolder extends RecyclerView.ViewHolder {
        private final TextView loanItemView;
        private TextView IdItemView;
        private TextView userIdView;

        private LoanViewHolder(View itemView) {
            super(itemView);
            loanItemView = itemView.findViewById(R.id.textView);
            IdItemView = itemView.findViewById(R.id.age);
            userIdView = itemView.findViewById(R.id.occupation);

        }

    }
    public interface OnHandlerInteractionListener{
        //interface to handle interation with the activity
        public void onHandlerInteraction(long total);
    }
}