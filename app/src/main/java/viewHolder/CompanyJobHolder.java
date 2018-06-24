package viewHolder;

import android.com.traineeshare.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CompanyJobHolder extends RecyclerView.ViewHolder{
    private final TextView tv_title;
    private final TextView tv_desc;

    public CompanyJobHolder(View itemView){
        super(itemView);
        tv_title = (TextView)itemView.findViewById(R.id.tv_createdJobTitle);
        tv_desc = (TextView)itemView.findViewById(R.id.tv_createdJobDescription);
    }

    public void setTv_title(String t){
        tv_title.setText(t);
    }
    public void setTv_desc(String d){
        tv_desc.setText(d);
    }

}
