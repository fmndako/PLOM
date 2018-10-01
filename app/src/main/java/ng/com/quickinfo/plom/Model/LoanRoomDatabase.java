package ng.com.quickinfo.plom.Model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Loan.class, User.class, Offset.class}, version = 1, exportSchema = false)
public abstract class LoanRoomDatabase extends RoomDatabase {

    private static LoanRoomDatabase INSTANCE;

    static LoanRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LoanRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LoanRoomDatabase.class, "loan_database")
                            // Wipes and rebuilds instead of migrating 
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract LoanDao loanDao();
    public abstract UserDao userDao();
    public abstract OffsetDao offsetDao();

}
