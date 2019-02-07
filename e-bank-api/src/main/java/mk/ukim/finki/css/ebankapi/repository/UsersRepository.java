package mk.ukim.finki.css.ebankapi.repository;


import mk.ukim.finki.css.ebankapi.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);

    public Optional<User> findByAccoutNumber(String accountNumber);

}
