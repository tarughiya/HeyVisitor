package com.example.heyvisitor.Model;

public class Host {

    private String Name;
    private String Email;
    private int Number;


    public Host(){
        //required for Firebase.
    }



    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }


    public void setEmail(String email) {
        Email = email;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }


}
