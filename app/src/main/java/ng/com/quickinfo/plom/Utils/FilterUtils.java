package ng.com.quickinfo.plom.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;

import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;

public class FilterUtils {

    public static int getItemCount(List<Loan> mLoans) {
        if (mLoans != null)
            return mLoans.size();
        else return 0;
    }

    public static int getTotalLends(List<Loan> mLoans){
        int sum = 0;
        if (mLoans != null){
            for (int x = 0; x<mLoans.size(); x++ ){
                sum += mLoans.get(x).getAmount();
            }
            return sum;
        }
        else
        {return sum;}
    }

    //clear status filters
    public static List<Loan> activeLoans (List<Loan> loans){
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
    public static List<List<Loan>> loanType (List<Loan> loans){
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
    public static List<List<Loan>> dateFilterList (List<Loan> loans){
        //returns a list(3) of list of loans by date filters
        List<List<Loan>> dateList = new ArrayList<>();
        List<Loan> dueSoonLoans = new ArrayList<>();
        List<Loan> dueLoans = new ArrayList<>();
        List<Loan> overDueLoans = new ArrayList<>();

        Date today = Calendar.getInstance().getTime();
        for (Loan loan: loans){

            if (loanIsDue(loan, today)){
                dueLoans.add(loan);}

            else if (loanIsOverDue(loan, today)){
                overDueLoans.add(loan);
            }else if (loanIsDueSoon(loan, today, 7)){
                dueSoonLoans.add(loan);
            }
        }
        dateList.add(dueSoonLoans);
        dateList.add(dueLoans);
        dateList.add(overDueLoans);
        return dateList;
    }

    //filter helper functions (loan type)
    public static boolean typeIsLend(Loan loan){
        return (loan.getLoanType() == 0);
    }

    //time filter helper functions
    public static boolean loanIsDue(Loan loan, Date today){
        //returns true if datetorepay is same as today
        Utilities.log("date:due", dateToString(loan.getDateToRepay())+ ""
                +dateToString(today) + ": "  +":"+ loan.getDateToRepay().compareTo(today) + "");
        return loan.getDateToRepay().compareTo(today)==1;
    }
    public static boolean loanIsDueSoon(Loan loan,Date today,  int reminderDays){
        //returns true if date is before today and after reminder number of days
        Date daysAgo = new Date(today.getTime() - reminderDays*24*60*60);
        Utilities.log("duesoondate:", dateToString(today) + ": " +
                dateToString(loan.getDateToRepay())
                +":"+reminderDays+
                (loan.getDateToRepay().before(today) & !(loan.getDateToRepay().getTime() <  daysAgo.getTime())) + "");
        Date dateToRepay =    loan.getDateToRepay();
        Calendar calToday = Calendar.getInstance();
        calToday.setTime(today);

            //today is not less than days ago
        return (loan.getDateToRepay().before(today) & !(loan.getDateToRepay().getTime() <  daysAgo.getTime()));


    }
    public static boolean loanIsOverDue(Loan loan, Date today){
        //returns true if date of repayment is after today
        Utilities.log("Overduedate:", dateToString(today) + ": " +

                dateToString(loan.getDateToRepay()) +":"
                     +(loan.getDateToRepay().after(today)) + "");
        return (loan.getDateToRepay().after(today));
    }



}
