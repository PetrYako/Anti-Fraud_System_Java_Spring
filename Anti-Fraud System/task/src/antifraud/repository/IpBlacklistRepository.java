package antifraud.repository;

import antifraud.model.IpBlacklist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpBlacklistRepository extends CrudRepository<IpBlacklist, Long> {
    Optional<IpBlacklist> findByIp(String ip);
    List<IpBlacklist> findAllByOrderByIdAsc();
}
