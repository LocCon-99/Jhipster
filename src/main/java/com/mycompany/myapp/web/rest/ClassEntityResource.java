package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ClassEntity;
import com.mycompany.myapp.repository.ClassEntityRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.ClassEntity}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClassEntityResource {

    private final Logger log = LoggerFactory.getLogger(ClassEntityResource.class);

    private static final String ENTITY_NAME = "classEntity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassEntityRepository classEntityRepository;

    public ClassEntityResource(ClassEntityRepository classEntityRepository) {
        this.classEntityRepository = classEntityRepository;
    }

    /**
     * {@code POST  /class-entities} : Create a new classEntity.
     *
     * @param classEntity the classEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classEntity, or with status {@code 400 (Bad Request)} if the classEntity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/class-entities")
    public ResponseEntity<ClassEntity> createClassEntity(@RequestBody ClassEntity classEntity) throws URISyntaxException {
        log.debug("REST request to save ClassEntity : {}", classEntity);
        if (classEntity.getId() != null) {
            throw new BadRequestAlertException("A new classEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClassEntity result = classEntityRepository.save(classEntity);
        return ResponseEntity
            .created(new URI("/api/class-entities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /class-entities/:id} : Updates an existing classEntity.
     *
     * @param id the id of the classEntity to save.
     * @param classEntity the classEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classEntity,
     * or with status {@code 400 (Bad Request)} if the classEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/class-entities/{id}")
    public ResponseEntity<ClassEntity> updateClassEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClassEntity classEntity
    ) throws URISyntaxException {
        log.debug("REST request to update ClassEntity : {}, {}", id, classEntity);
        if (classEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClassEntity result = classEntityRepository.save(classEntity);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, classEntity.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /class-entities/:id} : Partial updates given fields of an existing classEntity, field will ignore if it is null
     *
     * @param id the id of the classEntity to save.
     * @param classEntity the classEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classEntity,
     * or with status {@code 400 (Bad Request)} if the classEntity is not valid,
     * or with status {@code 404 (Not Found)} if the classEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the classEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/class-entities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClassEntity> partialUpdateClassEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClassEntity classEntity
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClassEntity partially : {}, {}", id, classEntity);
        if (classEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClassEntity> result = classEntityRepository
            .findById(classEntity.getId())
            .map(existingClassEntity -> {
                if (classEntity.getClassId() != null) {
                    existingClassEntity.setClassId(classEntity.getClassId());
                }
                if (classEntity.getName() != null) {
                    existingClassEntity.setName(classEntity.getName());
                }

                return existingClassEntity;
            })
            .map(classEntityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, classEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /class-entities} : get all the classEntities.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classEntities in body.
     */
    @GetMapping("/class-entities")
    public ResponseEntity<List<ClassEntity>> getAllClassEntities(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ClassEntities");
        Page<ClassEntity> page = classEntityRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /class-entities/:id} : get the "id" classEntity.
     *
     * @param id the id of the classEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/class-entities/{id}")
    public ResponseEntity<ClassEntity> getClassEntity(@PathVariable Long id) {
        log.debug("REST request to get ClassEntity : {}", id);
        Optional<ClassEntity> classEntity = classEntityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(classEntity);
    }

    /**
     * {@code DELETE  /class-entities/:id} : delete the "id" classEntity.
     *
     * @param id the id of the classEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/class-entities/{id}")
    public ResponseEntity<Void> deleteClassEntity(@PathVariable Long id) {
        log.debug("REST request to delete ClassEntity : {}", id);
        classEntityRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
