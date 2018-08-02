package backend.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import backend.repo.AddressRepository;
import backend.util.Debug;
import backend.model.Address;

@Configuration
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Boolean saveNewAddress(String _loc, String _addr, double _long, double _lati) {
        // Check if address already exist
        Address addr = new Address();
        addr.setLocation(_loc);
        addr.setAddress(_addr);
        addr.setLongtitude(_long);
        addr.setLatitude(_lati);
        return saveNewAddress(addr);
    }

    public Boolean saveNewAddress(Address addr) {
        // Check if address already exist
        if (addressRepository.countByLongtitude(addr.getLongtitude()) > 0 && addressRepository.countByLatitude(addr.getLatitude()) > 0 )
            return true;
        // Get autoincrement id
        Address lastaddr = addressRepository.findOneByOrderByIdDesc();
        Long lastid;
        if (lastaddr == null) {
            lastid = 0l;
        } else {
            lastid = lastaddr.getId();
        }
        Debug.Log(String.format("AddressService: %s", lastid.toString()));
        addr.setId(lastid + 1);
        addressRepository.save(addr);
        return false;
    }

    public Address getAddressById(Long _id) {
        return addressRepository.findFirstById(_id);
    }
}