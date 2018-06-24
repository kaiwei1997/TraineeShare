package viewHolder;

import android.com.traineeshare.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class VacancyHolder extends RecyclerView.ViewHolder{

    private final ImageView iv_companyLogo;
    private final TextView tv_title;
    private final TextView tv_company;
    private final TextView tv_period;

    public VacancyHolder(View itemView){
        super(itemView);
        iv_companyLogo = (ImageView)itemView.findViewById(R.id.iv_jobCompanyLogo);
        tv_title = (TextView)itemView.findViewById(R.id.tv_jobTitle);
        tv_company = (TextView)itemView.findViewById(R.id.tv_jobCompany);
        tv_period = (TextView)itemView.findViewById(R.id.tv_jobPeriod);
    }

    public void setIv_companyLogo(String url){
        Picasso.get().load(url).into(iv_companyLogo);
    }
    public void setTv_title(String t){
        tv_title.setText(t);
    }

    public void setTv_company(String c){
        tv_company.setText(c);
    }

    public void setTv_period(String d){
        tv_period.setText(d);
    }
}
