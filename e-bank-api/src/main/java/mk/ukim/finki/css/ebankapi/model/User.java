package mk.ukim.finki.css.ebankapi.model;

import mk.ukim.finki.css.ebankapi.model.enums.Role;

import javax.persistence.*;

;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    public String username;
    private String password;
    public String name;
    public String telephone;
    public Double balance;
    public Double startBalance;
    @Column(unique = true)
    public String accoutNumber;
    public String address;
    public Long employeeId;
    public Role role;

    public User () {

    }

    public User(String username, String password, String name, String telephone, Double startBalance, String accoutNumber, String address) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.telephone = telephone;
        this.startBalance = startBalance;
        this.accoutNumber = accoutNumber;
        this.address = address;
        balance = startBalance;
        role=Role.CLIENT;
    }

    public Long getId() {
        return id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(Double startBalance) {
        this.startBalance = startBalance;
    }

    public String getAccoutNumber() {
        return accoutNumber;
    }

    public void setAccoutNumber(String accoutNumber) {
        this.accoutNumber = accoutNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
