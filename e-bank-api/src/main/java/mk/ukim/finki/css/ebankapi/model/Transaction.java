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
    public Long employee;

    @ManyToOne
    public Long sender;

    @ManyToOne
    public Long reciever;

    public Transaction () {

    }

    public Transaction(Long sender, Long reciever, Long employee, Double amount, ZonedDateTime date) {
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

    public Long getEmployee() {
        return employee;
    }

    public void setEmployee(Long employee) {
        this.employee = employee;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReciever() {
        return reciever;
    }

    public void setReciever(Long reciever) {
        this.reciever = reciever;
    }
}
