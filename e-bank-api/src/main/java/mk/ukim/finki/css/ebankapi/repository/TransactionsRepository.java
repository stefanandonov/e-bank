package mk.ukim.finki.css.ebankapi.repository;

import mk.ukim.finki.css.ebankapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByRecieverOrSender(Long reciever, Long sender);



}
