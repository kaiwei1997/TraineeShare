package viewHolder;

import android.com.traineeshare.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CompanyJobHolder extends RecyclerView.ViewHolder{
    private final TextView tv_title;
    private final TextView tv_desc;
    private final TextView tv_postDate;

    public CompanyJobHolder(View itemView){
        super(itemView);
        tv_title = (TextView)itemView.findViewById(R.id.tv_createdJobTitle);
        tv_desc = (TextView)itemView.findViewById(R.id.tv_createdJobDescription);
        tv_postDate = (TextView)itemView.findViewById(R.id.tv_createdJobDate);
    }

    public void setTv_title(String t){
        tv_title.setText(t);
    }
    public void setTv_desc(String d){
        tv_desc.setText(d);
    }
    public void setTv_postDate(String p){
        tv_postDate.setText(p);
    }

}
