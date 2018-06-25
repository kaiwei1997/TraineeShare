package viewHolder;

import android.com.traineeshare.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ApplicationHolder extends RecyclerView.ViewHolder {
    private final TextView tv_title;
    private final TextView tv_studentName;

    public ApplicationHolder(View itemView){
        super(itemView);
        tv_title = (TextView)itemView.findViewById(R.id.tv_createdJobTitle);
        tv_studentName = (TextView)itemView.findViewById(R.id.tv_studentName);
    }

    public void setTv_title(String t){
        tv_title.setText(t);
    }
    public void setTv_desc(String n){
        tv_studentName.setText(n);
    }
}
