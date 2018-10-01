package ng.com.quickinfo.plom.ViewModel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.R;

public class LoanListAdapter extends RecyclerView.Adapter<LoanListAdapter.LoanViewHolder> {

    private final LayoutInflater mInflater;
    private List<Loan> mLoans; // Cached copy of loans

    public LoanListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public LoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.loan_rv, parent, false);
        return new LoanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LoanViewHolder holder, int position) {
        if (mLoans != null) {
            Loan current = mLoans.get(position);
            holder.loanItemView.setText(current.getName());
        } else {
            // Covers the case of data not being ready yet.
            holder.loanItemView.setText("No Loan");
        }
    }

    void setLoans(List<Loan> loans){
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

    class LoanViewHolder extends RecyclerView.ViewHolder {
        private final TextView loanItemView;

        private LoanViewHolder(View itemView) {
            super(itemView);
            loanItemView = itemView.findViewById(R.id.textView);
        }
    }
}