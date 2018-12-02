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
public interface OffsetDao {

    /*@Query("select * from offset_table")
    LiveData<List<Offset>> getAllOffsetItems();

*/
    @Query("select * from offset_table where id = :id")
    Offset getItembyId(long id);


    @Query("select * from offset_table where loan_id = :id")
    LiveData<List<Offset>> getItembyLoanId(long id);

    @Insert(onConflict = REPLACE)
    void addOffset(Offset Offset);
    //void insert(Offset Offset);

    @Delete
    void deleteOffset(Offset Offset);



}