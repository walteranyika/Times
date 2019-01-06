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
import sacco.times.attendance.models.Item;
import sacco.times.attendance.utils.DateUtils;

/**
 * Created by walter on 9/25/18.
 */

public class CustomLateAdapter extends RecyclerView.Adapter<CustomLateAdapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<Item> arrayList;
    ArrayList<Item> displayList;

    public CustomLateAdapter(Context context, ArrayList<Item> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.displayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout3, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item u = displayList.get(position);
        holder.txtNames.setText(u.getLogname());
        holder.txtBranch.setText(u.getBranch());
        // holder.txtTimeOut.setText(u.getLogout().split(" ")[1]);
        holder.txtTimeIn.setText(DateUtils.formatTime(DateUtils.parseDate(u.getLogtime())));
        holder.txtDate.setText(DateUtils.formatDate(DateUtils.parseDate(u.getLogtime())));


        String dep = u.getDepartment().length() > 12 ? "BS DEV" : u.getDepartment();
        u.setDepartment(dep);
        holder.txtDepartment.setText(u.getDepartment());
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
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = arrayList;
                    results.count = arrayList.size();
                } else {
                    ArrayList<Item> filteredResults = new ArrayList<>();
                    String search = constraint.toString().toLowerCase();
                    for (Item u : arrayList) {
                        if (u.getBranch().toLowerCase().contains(search))//|| u.getCourse().toLowerCase().contains(search) || u.getCampus().toLowerCase().contains(search)){
                            filteredResults.add(u);
                    }
                    results.values = filteredResults;
                    results.count = filteredResults.size();
                }

                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                displayList = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtNames;
        TextView txtDate;
        TextView txtBranch;
        TextView txtTimeIn;
        TextView txtDepartment;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtNames = itemView.findViewById(R.id.txt_staff_name);
            this.txtBranch = itemView.findViewById(R.id.txt_branch_name);
            this.txtDate = itemView.findViewById(R.id.txt_date);
            this.txtTimeIn = itemView.findViewById(R.id.txt_time_in);
            //this.txtTimeOut =itemView.findViewById(R.id.txt_time_out);
            this.txtDepartment = itemView.findViewById(R.id.txt_department);
            // this.txtHoursWorked =itemView.findViewById(R.id.txt_hours_worked);
        }
    }
}
