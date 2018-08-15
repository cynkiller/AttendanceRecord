package backend.repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import backend.model.Rehearsal;

public interface RehearsalRepository extends MongoRepository<Rehearsal, Long> {
    Rehearsal findFirstByDate(String date);
    Rehearsal findFirstByOrderByStartTimestampDesc();
    List<Rehearsal> findAllByOrderByStartTimestampDesc();
    long countByDate(String date);
    Rehearsal save(Rehearsal r);
}
