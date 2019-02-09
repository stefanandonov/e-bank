package mk.ukim.finki.css.ebankapi.model;



import javax.persistence.Entity;
import javax.persistence.*;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Integer itemNumber;
    @Column(unique = true)
    public Long tokenNumber;

    @ManyToOne
    private User user;

    public Token () {}

    public Token(Integer itemNumber, Long tokenNumber, User user) {
        this.itemNumber = itemNumber;
        this.tokenNumber = tokenNumber;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public Long getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(Long tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
