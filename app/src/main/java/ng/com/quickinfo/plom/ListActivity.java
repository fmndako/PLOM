package ng.com.quickinfo.plom;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo;
import ng.com.quickinfo.plom.Utils.FilterUtils;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.View.LoanListAdapter;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.DetailActivity.startSettings;
import static ng.com.quickinfo.plom.Utils.Utilities.intentToLoan;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

public class ListActivity extends LifecycleLoggingActivity implements
        LoanListAdapter.OnHandlerInteractionListener {
    //implemented a listener from the adapter to handle layout clicks
    //delare variables
    //intent for signout and revoke access
    ListReceiver ListReceiver;
    IntentFilter intentfilter;

    @BindView(R.id.tvSizeLends)
    MyTextView tvSize;
    @BindView(R.id.tvTotalLends)
    MyTextView tvTotal;
    @BindView(R.id.spFilter)
    Spinner spFilter;




    //adapter
    //loads the RV
    private RecyclerView recyclerView;
    private LoanListAdapter adapter;

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    String currency;
    int reminderDays;
    //loan
    private List<Loan> mLoans;

    @BindView(R.id.register_progress)
    ProgressBar mRegisterProgress;

    //context
    private Context mContext;
    //TODO remive
    Activity activity;
    //initiate viewmodel
    LoanViewModel mLoanViewModel;
    private long mUserId;
    //resources
    Resources resources;


    int loanType;
    //required for start new activity
    public static final int NEW_LOAN_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //show progress
        //load View Model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //resources
        resources = getResources();
        //context
        mContext = getApplicationContext();
        Utilities.showProgress(true, mRegisterProgress, mContext);

        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(mContext);
        editor = sharedPref.edit();
        currency = sharedPref.getString(ActivitySettings.Pref_Currency,"N" );
        reminderDays = sharedPref.getInt(ActivitySettings.Pref_ReminderDays, 7);
        //intent
        mUserId = sharedPref.getLong(ActivitySettings.Pref_User, 1);
        loanType = getIntent().getIntExtra("loanType", 0);

        activity = this;


        //register receiver
        registerMyReceivers();

        //set recyclerview
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new LoanListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRV(loanType);


        //setspinner filter
        spFilter.setSelection(loanType);
        setSpinnerListener();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewLoan();
            }
        });

    }


    public void setSpinnerListener() {

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //makeToast(mContext, "item selected spinner" + i + l);
                loadRV(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//
//

    }
    public boolean canTransition(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public void onHandlerInteraction(long loan_id, View view) {
        //my own listener created in the loanadapter class

        startDetailActivity(loan_id, view);
    }

    private void startDetailActivity(long loan_id, View view) {
        //starts detail activity
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.EXTRA_PARAM_ID, loan_id);
        //TODO starts a shared transition from list screen to detail screen
        if(canTransition()){
        initiateTransitionToDetailScreen(detailIntent, view);}
        else{
            startActivity(detailIntent);
        }
    }

    private void addNewLoan() {
        //starts add loan activity for result
        Intent intent = new Intent(this, AddLoanActivity.class);
        intent.putExtra("loantype", loanType);
        startActivityForResult(intent, NEW_LOAN_ACTIVITY_REQUEST_CODE);
    }

    private void loadRV(final int loanType) {

        //loads the RV

        //observer
        mLoanViewModel.getLoansByUserId(mUserId).observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the loans in the adapter.
                log(TAG, loans.size() + "size");
                List<Loan> activeLoans = FilterUtils.activeLoans(loans);
                switch (loanType) {
                    case 0:
                        mLoans = loans;
                        break;
                    case 1:
                        mLoans = activeLoans;
                        break;
                    case 2:
                        mLoans = FilterUtils.loanType(activeLoans).get(0);
                        break;
                    case 3:
                        mLoans = FilterUtils.loanType(activeLoans).get(1);

                        break;
                    case 4:
                        mLoans = FilterUtils.dateIsDueSoon(activeLoans, reminderDays);

                        break;
                    case 5:
                        mLoans = FilterUtils.dateIsDue(activeLoans);
                        break;
                    case 6:
                        mLoans = FilterUtils.dateIsOverDue(activeLoans);

                        break;
                    case 7:
                        mLoans =FilterUtils.Notifications(activeLoans, reminderDays);
                        break;
                    case 8:
                        mLoans = FilterUtils.clearedLoans(loans);
                        break;

                }
                adapter.setLoans(mLoans);

                int count = adapter.getItemCount();

                //resourcesadb
                tvSize.setText(count + " " + resources.getQuantityText(
                        R.plurals.numberOfLoans,
                        count));
                tvTotal.setText(currency + adapter.getItemSum());
                //TODO update other UI
                Date date = Calendar.getInstance().getTime();
                log(TAG, Utilities.dateToString(date));
            }
        });
        Utilities.showProgress(false, mRegisterProgress, mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);

        //search view
        MenuItem searchViewItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                log(TAG, query);
                // if( ! searchView.isIconified()) {
//                    searchView.setIconified(true);
//                }
//                item.collapseActionView();
//                return false;

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                log(TAG, newText);
                adapter.setLoans(FilterUtils.searchLoans(mLoans, newText.toLowerCase()));
                tvSize.setText(adapter.getItemCount()+ "Loans");
                tvTotal.setText(currency + adapter.getItemSum());

                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //start settings activity with userId
            startActivity(startSettings(mContext, mUserId));
            return true;
        } else if (id == R.id.action_list_home){
            log(TAG, "onoptionsitemselected: home click");
            //makeToast(mContext, "home clicked");
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("id", mUserId);
            startActivity(intent);

        }


        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == NEW_LOAN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Loan loan = intentToLoan(intent);//use database utils async task
            loan.setUser_id(mUserId);
            new LoanRepo.LoanAsyncTask(mLoanViewModel,
                    DetailActivity.loanInsertAction).execute(loan);

            ///makeToast(this, "loan saved");

        }

    }

    private void registerMyReceivers() {
        ListReceiver = new ListReceiver();
        intentfilter = new IntentFilter(DetailActivity.loanInsertAction);
        //intentfilter.addAction(ListActivity.ACTION_DELETE_ACCOUNT);
        //registers receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(ListReceiver, intentfilter);
    }

    private void unRegisterMyReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(ListReceiver);
    }

    //""" listener that receives broadcast from repo database action completed

    public class ListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, intent.getAction());
            switch (intent.getAction()) {
                case DetailActivity.loanInsertAction:
                    makeToast(mContext, "Loan added");
                    break;
            }


        }
    }


    //shared transition between List screen and Detail screen
    // BEGIN_INCLUDE(start_activity)
    /**
     * Now create an {@link android.app.ActivityOptions} instance using the

     */
    public void initiateTransitionToDetailScreen(Intent intent, View view){
    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,

            // Now we provide a list of Pair items which contain the view we can transitioning
            // from, and the name of the view it is transitioning to, in the launched activity
//            new Pair<View, String>(view.findViewById(R.id.imageview_item),
//                    DetailActivity.VIEW_NAME_HEADER_IMAGE),
            new Pair<View, String>(view.findViewById(R.id.ivLoanType),
                    DetailActivity.VIEW_NAME_HEADER_IMAGE),
            new Pair<View, String>(view.findViewById(R.id.tvLRVAmount),
                    DetailActivity.VIEW_NAME_AMOUNT));

    // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    // END_INCLUDE(start_activity)
    }
}
