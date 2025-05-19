package models;

public class User {
    private int id;
    private String email;
    private String password;
    private String dpi;
    private String phone;
    private String address;
    private int roleId;
    private boolean active;
    private String name;
    
    public User(int id, String email, String password, String dpi, String phone, String address, int roleId, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.dpi = dpi;
        this.phone = phone;
        this.address = address;
        this.roleId = roleId;
        this.active = active;
    }

    public User() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}