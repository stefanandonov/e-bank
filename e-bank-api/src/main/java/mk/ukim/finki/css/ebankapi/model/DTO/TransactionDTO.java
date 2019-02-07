package mk.ukim.finki.css.ebankapi.model.DTO;

import java.time.ZonedDateTime;

public class TransactionDTO {

    private ZonedDateTime date;
    private String senderOrRecieverName;
    private Double amount;

    public TransactionDTO(ZonedDateTime date, String senderOrRecieverName, Double amount) {
        this.date = date;
        this.senderOrRecieverName = senderOrRecieverName;
        this.amount = amount;
    }

}
