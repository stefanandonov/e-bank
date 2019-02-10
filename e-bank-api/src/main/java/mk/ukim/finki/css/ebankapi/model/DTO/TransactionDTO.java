package mk.ukim.finki.css.ebankapi.model.DTO;

import java.time.ZonedDateTime;

public class TransactionDTO {

    public ZonedDateTime date;
    public String senderOrRecieverName;
    public Double amount;

    public TransactionDTO(ZonedDateTime date, String senderOrRecieverName, Double amount) {
        this.date = date;
        this.senderOrRecieverName = senderOrRecieverName;
        this.amount = amount;
    }

}
