package timesu.sacco.attendance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import timesu.sacco.attendance.R;
import timesu.sacco.attendance.models.Item;
import timesu.sacco.attendance.utils.DateUtils;

/**
 * Created by walter on 9/25/18.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<Item> arrayList;
    ArrayList<Item> displayList;

    public CustomAdapter(Context context, ArrayList<Item> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.displayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout2, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item u = displayList.get(position);
        holder.txtNames.setText(u.getLogname());
        holder.txtBranch.setText(u.getBranch());
        holder.txtTimeIn.setText(DateUtils.formatTime(DateUtils.parseDate(u.getLogtime())));
        holder.txtDate.setText(DateUtils.formatDate(DateUtils.parseDate(u.getLogtime())));
        holder.txtTimeOut.setText(DateUtils.formatTime(DateUtils.parseDate(u.getLogout())));
        String dep = u.getDepartment().length() > 12 ? "BS DEV" : u.getDepartment();
        u.setDepartment(dep);
        holder.txtDepartment.setText(dep);

        Date startDate = DateUtils.parseDate(u.getLogtime());
        Date endDate = DateUtils.parseDate(u.getLogout());
        long differenceInMillis = Math.abs(endDate.getTime() - startDate.getTime());
        String formattedText = DateUtils.formatElapsedTime(differenceInMillis / 1000); //divide by 1000 to get seconds from milliseconds
        holder.txtHoursWorked.setText(formattedText);
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
                ArrayList<Item> filteredResults = new ArrayList<>();
                String search = constraint.toString().toLowerCase();
                String items[] = search.split(":");

                if (constraint.length() == 0 || constraint.toString().trim().equals(":")) {
                    results.values = arrayList;
                    results.count = arrayList.size();
                }
                else if (items[0].trim().isEmpty() && items[1].trim().isEmpty()) {

                    results.values = arrayList;
                    results.count = arrayList.size();

                } else if (!items[0].trim().isEmpty() && items[1].trim().isEmpty())//DEP:
                {
                    String department = items[0].trim();
                    for (Item u : arrayList) {
                        if (u.getDepartment().toLowerCase().contains(department)) //|| u.getCourse().toLowerCase().contains(search) || u.getCampus().toLowerCase().contains(search)){
                            filteredResults.add(u);
                    }
                    results.values = filteredResults;
                    results.count = filteredResults.size();
                } else if (items[0].trim().isEmpty() && !items[1].trim().isEmpty())//:BRANCH
                {
                    String branch = items[1].trim();
                    for (Item u : arrayList) {
                        if (u.getBranch().toLowerCase().contains(branch)) //|| u.getCourse().toLowerCase().contains(search) || u.getCampus().toLowerCase().contains(search)){
                            filteredResults.add(u);
                    }
                    results.values = filteredResults;
                    results.count = filteredResults.size();
                } else if (!items[0].trim().isEmpty() && !items[1].trim().isEmpty())//DEP:BRANCH
                {
                    String branch = items[1].trim();
                    String department = items[0].trim();
                    for (Item u : arrayList) {
                        if (u.getBranch().toLowerCase().contains(branch) && u.getDepartment().toLowerCase().contains(department)) //|| u.getCourse().toLowerCase().contains(search) || u.getCampus().toLowerCase().contains(search)){
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
        TextView txtTimeOut;
        TextView txtTimeIn;
        TextView txtDepartment;
        TextView txtHoursWorked;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtNames = itemView.findViewById(R.id.txt_staff_name);
            this.txtBranch = itemView.findViewById(R.id.txt_branch_name);
            this.txtDate = itemView.findViewById(R.id.txt_date);
            this.txtTimeIn = itemView.findViewById(R.id.txt_time_in);
            this.txtTimeOut = itemView.findViewById(R.id.txt_time_out);
            this.txtDepartment = itemView.findViewById(R.id.txt_department);
            this.txtHoursWorked = itemView.findViewById(R.id.txt_hours_worked);
        }
    }
}
