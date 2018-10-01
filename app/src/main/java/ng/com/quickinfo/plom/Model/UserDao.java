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
public interface UserDao {

    @Query("select * from user_table")
    LiveData<List<User>> getAllUsers();

    @Query("select * from user_table where email = :email")
    User getUserbyEmail(String email);

    @Insert(onConflict = REPLACE)
    void addUser(User user);
    //void insert(Loan loan);

    @Delete
    void deleteUser(User user);
}