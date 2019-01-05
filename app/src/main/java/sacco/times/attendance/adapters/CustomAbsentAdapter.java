package sacco.times.attendance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import sacco.times.attendance.R;
import sacco.times.attendance.models.Absent;

/**
 * Created by walter on 9/25/18.
 */

public class CustomAbsentAdapter extends RecyclerView.Adapter<CustomAbsentAdapter.MyViewHolder>  implements Filterable {

    Context context;
    ArrayList<Absent> arrayList;
    ArrayList<Absent> displayList;

    public CustomAbsentAdapter(Context context, ArrayList<Absent> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.displayList=arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout_absent2, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Absent u=displayList.get(position);
        holder.txtNames.setText(u.getNames());
        holder.txtBranch.setText(u.getBranch());
        String dep = u.getDepartment().length() > 12 ? "BS DEV" : u.getDepartment();
        u.setDepartment(dep);
        holder.txtDate.setText(u.getDate());
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();
                if (constraint==null || constraint.length()==0){
                   results.values=arrayList;
                   results.count=arrayList.size();
                }else{
                    ArrayList<Absent> filteredResults =new ArrayList<>();
                    String search = constraint.toString().toLowerCase();
                    for (Absent u:arrayList) {
                        if(u.getBranch().toLowerCase().contains(search) )
                           filteredResults.add(u);
                        }
                    results.values = filteredResults;
                    results.count= filteredResults.size();
                    }

                   return results;
                }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                  displayList = (ArrayList<Absent>) results.values;
                  notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtNames;
        TextView txtDate;
        TextView txtBranch;
        TextView txtDepartment;
        public MyViewHolder(View AbsentView) {
            super(AbsentView);
            this.txtNames=AbsentView.findViewById(R.id.txt_staff_name);
            this.txtBranch =AbsentView.findViewById(R.id.txt_branch_name);
            this.txtDate =AbsentView.findViewById(R.id.txt_date);
            this.txtDepartment =AbsentView.findViewById(R.id.txt_department);
        }
    }
}
