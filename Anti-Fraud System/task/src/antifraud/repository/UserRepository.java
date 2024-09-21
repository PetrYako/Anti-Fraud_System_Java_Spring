package antifraud.repository;

import antifraud.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsernameIgnoreCase(String username);
    List<User> findAllByOrderByIdAsc();
    Optional<User> findByAuthority(String authority);
}
