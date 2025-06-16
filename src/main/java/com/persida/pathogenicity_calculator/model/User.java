package com.persida.pathogenicity_calculator.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private int enabled;
    private String email;
    private String phone;
    private String groupName;
    private String groupType;
    private String roleName;
    private String company;
    private String jobOrOccupationName;
    private String dateCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getEnabled() { return enabled; }

    public void setEnabled(int enabled) { this.enabled = enabled; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getGroupType() { return groupType; }

    public void setGroupType(String groupType) { this.groupType = groupType; }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getCompany() { return company; }

    public void setCompany(String company) { this.company = company; }

    public String getJobOrOccupationName() {
        return jobOrOccupationName;
    }

    public void setJobOrOccupationName(String jobOrOccupationName) {
        this.jobOrOccupationName = jobOrOccupationName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
