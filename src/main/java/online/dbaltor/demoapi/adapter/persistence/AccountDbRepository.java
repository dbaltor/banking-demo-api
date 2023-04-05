package online.dbaltor.demoapi.adapter.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountDbRepository extends CrudRepository<AccountDb, Long> {

    public Optional<AccountDb> findByNumber(String number);
}
