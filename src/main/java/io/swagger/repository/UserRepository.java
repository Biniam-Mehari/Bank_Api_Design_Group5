package io.swagger.repository;

import io.swagger.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer>{
    User findByUsername(String username);

    User findByEmail(String email);

}
