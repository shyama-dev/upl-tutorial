package com.upl.tutorial.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.upl.tutorial.dto.InstructorMetricsDto;
import com.upl.tutorial.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer>{

    Optional<Users> findByEmail(String email);

    @Query("""
        SELECT new com.upl.tutorial.dto.InstructorMetricsDto(
            COUNT(u),
            COUNT(CASE WHEN u.status = 'Active' THEN 1 END),
            COUNT(CASE WHEN u.status = 'Pending' THEN 1 END),
            COUNT(CASE WHEN u.status = 'Rejected' THEN 1 END)
        )
        FROM Users u
        WHERE u.role = 'INSTRUCTOR'
    """)
    InstructorMetricsDto getInstructorMetrics();

     
}
