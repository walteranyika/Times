package timesu.sacco.attendance.models;

import java.util.Date;

/**
 * Created by walter on 1/3/19.
 */

public class Item {

    private String pin,  branch, device_branch, device, logname, logtime, department,logout;
    Date loginDate;


    public Item() {
    }

    public Item(String pin, String branch, String device_branch, String device, String logname, String logtime, String department) {
        this.pin = pin;
        this.branch = branch;
        this.device_branch = device_branch;
        this.device = device;
        this.logname = logname;
        this.logtime = logtime;
        this.department = department;
    }

    public Item(String pin, String branch, String device_branch, String device, String logname, String logtime, String department, String logout) {
        this.pin = pin;
        this.branch = branch;
        this.device_branch = device_branch;
        this.device = device;
        this.logname = logname;
        this.logtime = logtime;
        this.department = department;
        this.logout = logout;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDevice_branch() {
        return device_branch;
    }

    public void setDevice_branch(String device_branch) {
        this.device_branch = device_branch;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public String getLogtime() {
        return logtime;
    }

    public void setLogtime(String logtime) {
        this.logtime = logtime;
    }
}
