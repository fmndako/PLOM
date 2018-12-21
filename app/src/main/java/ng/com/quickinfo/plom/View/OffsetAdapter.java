package ng.com.quickinfo.plom.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import ng.com.quickinfo.plom.ActivitySettings;
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.R;
import ng.com.quickinfo.plom.Utils.Utilities;

import static ng.com.quickinfo.plom.Utils.Utilities.dateToString1;


public class OffsetAdapter extends BaseAdapter {

    private SharedPreferences myPref;
    private SharedPreferences.Editor editor;
    String currency;
    Context context;

    List<Offset> offset;
    Typeface fonts1,fonts2;





    public OffsetAdapter(Context context, List<Offset> offset) {


        this.context = context;
        this.offset = offset;
        myPref = Utilities.MyPref.getSharedPref(context);

        editor = Utilities.MyPref.getEditor();
        currency = myPref.getString(ActivitySettings.Pref_Currency, "N");

    }











    @Override
    public int getCount() {
        return offset.size();
    }

    @Override
    public Object getItem(int position) {
        return offset.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getTotal(){
            int sum = 0;
            if (offset != null){
                for (int x = 0; x<offset.size(); x++ ){
                    sum += offset.get(x).getAmount();
                }
                return sum;
            }
            else
            {return sum;}


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        fonts1 =  Typeface.createFromAsset(context.getAssets(),
                "fonts/Lato-Light.ttf");

        fonts2 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Lato-Regular.ttf");

        ViewHolder viewHolder = null;

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.offset_list,null);

            viewHolder = new ViewHolder();

            viewHolder.image = (ImageView)convertView.findViewById(R.id.image);
            viewHolder.title = (TextView)convertView.findViewById(R.id.tvOffsetAmount);
            viewHolder.discription = (TextView)convertView.findViewById(R.id.tvOffsetRemarks);
            viewHolder.date = (TextView)convertView.findViewById(R.id.tvOffsetDate);





            viewHolder.title.setTypeface(fonts2);
            viewHolder.discription.setTypeface(fonts1);

            viewHolder.date.setTypeface(fonts2);

            convertView.setTag(viewHolder);

        }else {

            viewHolder = (ViewHolder)convertView.getTag();
        }







        Offset offset = (Offset)getItem(position);

        //viewHolder.image.setImageResource(offset.getImage());
        viewHolder.title.setText(currency + offset.getAmount()+"");
        viewHolder.discription.setText(offset.getRemarks());
        viewHolder.date.setText(dateToString1(
                offset.getDateOffset()));







        return convertView;
    }

    private class ViewHolder{
        ImageView image;
        TextView title;
        TextView discription;
        TextView date;





    }

}




