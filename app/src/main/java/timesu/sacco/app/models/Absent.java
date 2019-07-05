package timesu.sacco.app.models;

/**
 * Created by walter on 1/3/19.
 */

public class Absent {
    private  String names, branch, department, date;
    public Absent() {
    }

    public Absent(String names, String branch, String department, String date) {
        this.names = names;
        this.branch = branch;
        this.department = department;
        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
