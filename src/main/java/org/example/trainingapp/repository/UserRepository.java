package org.example.trainingapp.repository;

import org.example.trainingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.username FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    Set<String> findUsernamesByFirstNameAndLastName(@Param("firstName") String firstName,
                                                    @Param("lastName") String lastName);
}
