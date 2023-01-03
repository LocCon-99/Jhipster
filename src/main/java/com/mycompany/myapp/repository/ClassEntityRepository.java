package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ClassEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClassEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {}
