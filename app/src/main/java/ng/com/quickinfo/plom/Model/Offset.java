package ng.com.quickinfo.plom.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import ng.com.quickinfo.plom.Utils.DateConverter;

import static android.arch.persistence.room.ForeignKey.CASCADE;


@Entity(tableName = "offset_table",

        indices = {@Index("loan_id")},
        foreignKeys = @ForeignKey(entity = Loan.class,
                parentColumns = "id",
                childColumns = "loan_id",
                onDelete = CASCADE))

public class Offset {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    private Integer amount;
    private String remarks;

    @ColumnInfo(name = "loan_id")
    private long loan_id;

    @TypeConverters(DateConverter.class)
    private Date dateOffset;

    public Offset( Integer amount, Date dateOffset,
                 String remarks, long loan_id) {

        this.remarks= remarks;
        this.amount = amount;
        this.dateOffset = dateOffset;
        this.loan_id = loan_id;

    }

    public long getId() {
        return id;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getRemarks() {
        return remarks;
    }


    public long getLoan_id() {
        return loan_id;
    }

    public Date getDateOffset() {
        return dateOffset;
    }

    public void setLoan_id(long id){
        this.loan_id = id;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setDateOffset(Date dateOffset) {
        this.dateOffset = dateOffset;
    }
}