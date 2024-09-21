package antifraud.repository;

import antifraud.model.Limit;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LimitRepository extends CrudRepository<Limit, Long> {
    Optional<Limit> findByIdOrderById(Long id);
}
