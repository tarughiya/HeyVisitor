package com.example.heyvisitor.Model;

import java.sql.Time;

public class Visitor {

    private String VName;
    private String VEmail;
    private int VNumber;
    private String localtime;
    private String role;
    private String checkouttime;




    public Visitor(String name, String email, int phone, String localtime , String role,String checkouttime) {
        this.VName = name;
        this.VEmail = email;
        this.VNumber= phone;
        this.localtime= localtime;
        this.role= role;
        this.checkouttime= checkouttime;

    }

    public String getVName() {
        return VName;
    }

    public String getVEmail() {
        return VEmail;
    }

    public int getVNumber() {
        return VNumber;
    }

    public String getLocaltime() {
        return localtime;
    }

    public String getRole() {
        return role;
    }


    public void setVName(String VName) {
        this.VName = VName;
    }

    public void setVEmail(String VEmail) {
        this.VEmail = VEmail;
    }

    public void setVNumber(int VNumber) {
        this.VNumber = VNumber;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
