package solutions.theta.msbadmin.Classes;

/**
 * Created by SALAH UD DIN on 27/11/2016.
 */

public class User {

    int Id;
    String Username;
    String Mobilenumber;
    String Addressline1;
    String City;
    String Role;
    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMobilenumber() {
        return Mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        Mobilenumber = mobilenumber;
    }

    public String getAddressline1() {
        return Addressline1;
    }

    public void setAddressline1(String addressline1) {
        Addressline1 = addressline1;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

}
