package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Student entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select s from Student s where 1=1" +
           " and :name IS NULL OR (UPPER(s.name) LIKE CONCAT('%',UPPER(:name),'%'))" +
           " and :age IS NULL OR s.age=:age")
    Page<Student> search(@Param("name") String name,@Param("age") Long age, Pageable pageable);
}
