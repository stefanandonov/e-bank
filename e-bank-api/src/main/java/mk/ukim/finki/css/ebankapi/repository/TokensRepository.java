package mk.ukim.finki.css.ebankapi.repository;

import mk.ukim.finki.css.ebankapi.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokensRepository extends JpaRepository<Token , Long> {

    Optional<Token> findByItemNumber(Integer number) ;

    List<Token> findAllByUserId(Long id);

    Optional<Token> findByUserIdAndItemNumber(Long userId, Integer itemNumber);




}