package online.dbaltor.demoapi.adapter.persistence;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

    public Optional<Account> findByNumber(String number);
}
