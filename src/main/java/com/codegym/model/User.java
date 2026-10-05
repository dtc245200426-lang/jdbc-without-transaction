package com.codegym.model;
public class User {
 private final int id; private final String name,email,country;
 public User(String name,String email,String country){this(0,name,email,country);}
 public User(int id,String name,String email,String country){this.id=id;this.name=name;this.email=email;this.country=country;}
 public int getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getCountry(){return country;}
}
