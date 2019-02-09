package mk.ukim.finki.css.ebankapi.model;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "transacations")
public class Transaction {
    @Id
    private Long id;
    public Double amount;
    public ZonedDateTime date;

    @ManyToOne
    public User employee;

    @ManyToOne
    public User sender;

    @ManyToOne
    public User reciever;

    public Transaction () {

    }

    public Transaction(User sender, User reciever, User employee, Double amount, ZonedDateTime date) {
        this.sender=sender;
        this.reciever=reciever;
        this.employee=employee;
        this.amount=amount;
        this.date=date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReciever() {
        return reciever;
    }

    public void setReciever(User reciever) {
        this.reciever = reciever;
    }
}
