package ng.com.quickinfo.plom.Model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import ng.com.quickinfo.plom.Utils.DateConverter;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface LoanDao {

    @Query("select * from loan_table where id = :id")
    LiveData<Loan> getItembyId(long id);

    @Query("select * from loan_table where user_id = :id")
    List<Loan> getLoans(long id);

    @Query("select * from loan_table where user_id = :id")
    LiveData<List<Loan>>  getItembyUserId(long id);

    @Insert(onConflict = REPLACE)
    void insert(Loan loan);

    @Delete
    void deleteLoan(Loan loan);
}