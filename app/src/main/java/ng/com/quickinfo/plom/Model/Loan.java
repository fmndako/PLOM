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

@Entity(tableName = "loan_table",
        indices = {@Index("user_id")},
        foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete = CASCADE))

public class Loan {

    @PrimaryKey(autoGenerate = true)

    public long id;


    @ColumnInfo(name = "user_id")
    private long user_id;

     private String name;
    private Integer amount;
    private Integer loanType;
    private String remarks;
    private String number;
    private Integer clearStatus;
    private Integer offset;
    private Integer notify;
    private Integer repaymentOption;
    private String email;
   @TypeConverters(DateConverter.class)
    private Date dateTaken;
    @TypeConverters(DateConverter.class)
    private Date dateToRepay;
    @TypeConverters(DateConverter.class)
    private Date dateCleared;

    public Loan( String name, String number, String email, Integer amount, Date dateTaken,
                Date dateToRepay, Integer loanType, String remarks, Integer clearStatus,
                 Integer offset, Integer notify,Integer repaymentOption, long user_id) {

        this.name = name;
        this.number = number;
        this.email = email;
        this.amount = amount;
        this.dateTaken = dateTaken;
        this.dateToRepay = dateToRepay;
        this.loanType = loanType;
        this.remarks = remarks;
        this.clearStatus = clearStatus;
        this.offset = offset;
        this.notify = notify;
        this.repaymentOption = repaymentOption;
        this.user_id = user_id;

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getUser_id() {
        return user_id;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getLoanType() {
        return loanType;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getNumber() {
        return number;
    }

    public Integer getClearStatus() {
        return clearStatus;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getNotify() {
        return notify;
    }

    public Integer getRepaymentOption() {
        return repaymentOption;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public Date getDateToRepay() {
        return dateToRepay;
    }

    public Date getDateCleared() {
        return dateCleared;
    }
    //setters
    public void setDateCleared(Date date){
        this.dateCleared = date;
    }
    public void setClearedStatus(Date date){
        this.dateCleared = date;
        this.clearStatus = 1;
    }

    public void setId(long id) {
        this.id = id;
    }


}
