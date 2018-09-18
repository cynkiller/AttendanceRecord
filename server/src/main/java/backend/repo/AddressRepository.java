package backend.repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import backend.model.Address;

public interface AddressRepository extends MongoRepository<Address, Long> {
    Address findFirstById(Long id);
    //Address findOneByOrderByIdDesc();
    List<Address> findAllByOrderByIdDesc();
    List<Address> removeByLongtitudeAndLatitude(double _long, double _lati);
    Address findFirstByLongtitudeAndLatitude(double _long, double _lati);
    int countById(Long id);
    int countByLongtitude(double _long);
    int countByLatitude(double _lati);
}
