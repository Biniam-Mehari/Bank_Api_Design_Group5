package io.swagger.repository;

import io.swagger.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User,Integer>, CrudRepository<User,Integer> {
    User findByUsername(String username);

    List<User> findAllByAccountsIsNull();

    @Query(value = "SELECT * FROM user LIMIT ?1 OFFSET ?2", nativeQuery = true)
    List<User> findAllUsers(Integer limit, Integer offset);

}