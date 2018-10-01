package ng.com.quickinfo.plom.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo;

public class LoanViewModel extends AndroidViewModel {

    private LoanRepo mRepository;

    private LiveData<List<Loan>> mAllLoans;

    public LoanViewModel (Application application) {
        super(application);
        mRepository = new LoanRepo(application);
        mAllLoans = mRepository.getAllLoans();

    }

    LiveData<List<Loan>> getAllLoans() { return mAllLoans; }

    public void insert(Loan loan) { mRepository.insert(loan); }
}
