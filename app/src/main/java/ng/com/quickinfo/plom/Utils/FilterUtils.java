package ng.com.quickinfo.plom.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;

import static android.service.autofill.Validators.or;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;

public class FilterUtils {


    //search
    public static List<Loan> searchLoans(List<Loan> loans, String query){
        List<Loan> filterLoans = new ArrayList<>();
        for (Loan loan: loans){
            if (loan.getName().contains(query) || (loan.getAmount().toString().contains(query)))
            {
                filterLoans.add(loan);
            }
        }
        return filterLoans;
    }

    public static int getItemCount(List<Loan> mLoans) {
        if (mLoans != null)
            return mLoans.size();
        else return 0;
    }

    public static int getTotalSum(List<Loan> mLoans){
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
    public static List<Loan> clearedLoans (List<Loan> loans){
        //returns all active loans
        List<Loan> Loans = new ArrayList<>();
        for (Loan loan: loans){
            if (loan.getClearStatus()!=0){
                Loans.add(loan);
            }
        }
        return Loans;
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
    public static List<List<Loan>> dateFilterList (List<Loan> loans, int days){
        //returns a list(3) of list of loans by date filters
        List<List<Loan>> dateList = new ArrayList<>();
        List<Loan> dueSoonLoans = new ArrayList<>();
        List<Loan> dueLoans = new ArrayList<>();
        List<Loan> overDueLoans = new ArrayList<>();

        Date today = Calendar.getInstance().getTime();
        for (Loan loan: loans){

            if (isOverDue(loan.getDateToRepay())){
                overDueLoans.add(loan);}

            else if (isDueSoon(loan.getDateToRepay(), days)){
                dueSoonLoans.add(loan);
            }else if (isToday(loan.getDateToRepay())){
                dueLoans.add(loan);
            }
        }
        dateList.add(dueSoonLoans);
        dateList.add(dueLoans);
        dateList.add(overDueLoans);
        return dateList;
    }

    public static List<Loan> dateIsDue (List<Loan> loans){
        List<Loan> dueLoans = new ArrayList<>();

        for (Loan loan: loans){

        if (isToday(loan.getDateToRepay())){
                dueLoans.add(loan);
            }
        }
        return dueLoans;
    }

    public static List<Loan> dateIsDueSoon (List<Loan> loans, int days){
        List<Loan> dueSoonLoans = new ArrayList<>();

        for (Loan loan: loans){

            if (isDueSoon(loan.getDateToRepay(), days)) {
                dueSoonLoans.add(loan);
            }
        }
        return dueSoonLoans;
    }

    public static List<Loan> dateIsOverDue (List<Loan> loans){
        List<Loan> Loans = new ArrayList<>();

        for (Loan loan: loans){

            if (isOverDue(loan.getDateToRepay())) {
                Loans.add(loan);
            }
        }
        return Loans;
    }

    public static List<Loan> Notifications (List<Loan> loans) {
        List<Loan> Loans = new ArrayList<>();

        for (Loan loan : loans) {

            if (loan.getNotify()!=0) {
                Loans.add(loan);
            }
        }
        return Loans;
    }
    //filter helper functions (loan type)
    public static boolean typeIsLend(Loan loan){
        return (loan.getLoanType() == 0);
    }

    //time filter helper functions

    public static boolean isDueSoon(Date date,  int reminderDays){
        //returns true if date is after today and and before date + reminder number of days
        Date today = Calendar.getInstance().getTime();
        Date daysAgo = new Date(today.getTime() + (reminderDays*24*60*60*1000));


        Calendar calToday = Calendar.getInstance();
        Calendar calRepay = Calendar.getInstance();
        calToday.setTime(today);
        calRepay.setTime(date);
        //log("HomeActivity:", dateToString(daysAgo));
        return (today.before(date) && date.before(daysAgo) );
           }
    public static boolean isOverDue(Date date){
        //returns true is loan is over due i.e date of repayment has passed
        //- today is ahead of date of repayment
        log("Date", dateToString(date) +" : " + Calendar.getInstance().getTime());
        return (Calendar.getInstance().getTime().after(date)) && !isToday(date);
    }



    public static boolean isToday(Date date){

        return (dateToString(date).equals(dateToString(Calendar.getInstance().getTime())));
    }

}
