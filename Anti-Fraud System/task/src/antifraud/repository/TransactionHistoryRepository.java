package antifraud.repository;

import antifraud.model.TransactionHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends CrudRepository<TransactionHistory, Long> {
    List<TransactionHistory> findByNumberAndCreatedAtAfterAndCreatedAtBefore(String number, LocalDateTime afterDate, LocalDateTime beforeDate);
    List<TransactionHistory> findAllByOrderByIdAsc();
    List<TransactionHistory> findAllByNumberOrderByIdAsc(String number);
}
