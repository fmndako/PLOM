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

        //instantiate OnHandlerInteraction
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
        mLoans = loans;
        notifyDataSetChanged();
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