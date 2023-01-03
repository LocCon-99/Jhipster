package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ClassEntity;
import com.mycompany.myapp.repository.ClassEntityRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClassEntityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClassEntityResourceIT {

    private static final Integer DEFAULT_CLASS_ID = 1;
    private static final Integer UPDATED_CLASS_ID = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/class-entities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClassEntityRepository classEntityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClassEntityMockMvc;

    private ClassEntity classEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassEntity createEntity(EntityManager em) {
        ClassEntity classEntity = new ClassEntity().classId(DEFAULT_CLASS_ID).name(DEFAULT_NAME);
        return classEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassEntity createUpdatedEntity(EntityManager em) {
        ClassEntity classEntity = new ClassEntity().classId(UPDATED_CLASS_ID).name(UPDATED_NAME);
        return classEntity;
    }

    @BeforeEach
    public void initTest() {
        classEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createClassEntity() throws Exception {
        int databaseSizeBeforeCreate = classEntityRepository.findAll().size();
        // Create the ClassEntity
        restClassEntityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classEntity)))
            .andExpect(status().isCreated());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeCreate + 1);
        ClassEntity testClassEntity = classEntityList.get(classEntityList.size() - 1);
        assertThat(testClassEntity.getClassId()).isEqualTo(DEFAULT_CLASS_ID);
        assertThat(testClassEntity.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createClassEntityWithExistingId() throws Exception {
        // Create the ClassEntity with an existing ID
        classEntity.setId(1L);

        int databaseSizeBeforeCreate = classEntityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassEntityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classEntity)))
            .andExpect(status().isBadRequest());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClassEntities() throws Exception {
        // Initialize the database
        classEntityRepository.saveAndFlush(classEntity);

        // Get all the classEntityList
        restClassEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].classId").value(hasItem(DEFAULT_CLASS_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getClassEntity() throws Exception {
        // Initialize the database
        classEntityRepository.saveAndFlush(classEntity);

        // Get the classEntity
        restClassEntityMockMvc
            .perform(get(ENTITY_API_URL_ID, classEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classEntity.getId().intValue()))
            .andExpect(jsonPath("$.classId").value(DEFAULT_CLASS_ID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingClassEntity() throws Exception {
        // Get the classEntity
        restClassEntityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClassEntity() throws Exception {
        // Initialize the database
        classEntityRepository.saveAndFlush(classEntity);

        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();

        // Update the classEntity
        ClassEntity updatedClassEntity = classEntityRepository.findById(classEntity.getId()).get();
        // Disconnect from session so that the updates on updatedClassEntity are not directly saved in db
        em.detach(updatedClassEntity);
        updatedClassEntity.classId(UPDATED_CLASS_ID).name(UPDATED_NAME);

        restClassEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClassEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClassEntity))
            )
            .andExpect(status().isOk());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
        ClassEntity testClassEntity = classEntityList.get(classEntityList.size() - 1);
        assertThat(testClassEntity.getClassId()).isEqualTo(UPDATED_CLASS_ID);
        assertThat(testClassEntity.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingClassEntity() throws Exception {
        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();
        classEntity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(classEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClassEntity() throws Exception {
        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();
        classEntity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(classEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClassEntity() throws Exception {
        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();
        classEntity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassEntityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClassEntityWithPatch() throws Exception {
        // Initialize the database
        classEntityRepository.saveAndFlush(classEntity);

        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();

        // Update the classEntity using partial update
        ClassEntity partialUpdatedClassEntity = new ClassEntity();
        partialUpdatedClassEntity.setId(classEntity.getId());

        partialUpdatedClassEntity.classId(UPDATED_CLASS_ID);

        restClassEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClassEntity))
            )
            .andExpect(status().isOk());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
        ClassEntity testClassEntity = classEntityList.get(classEntityList.size() - 1);
        assertThat(testClassEntity.getClassId()).isEqualTo(UPDATED_CLASS_ID);
        assertThat(testClassEntity.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateClassEntityWithPatch() throws Exception {
        // Initialize the database
        classEntityRepository.saveAndFlush(classEntity);

        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();

        // Update the classEntity using partial update
        ClassEntity partialUpdatedClassEntity = new ClassEntity();
        partialUpdatedClassEntity.setId(classEntity.getId());

        partialUpdatedClassEntity.classId(UPDATED_CLASS_ID).name(UPDATED_NAME);

        restClassEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClassEntity))
            )
            .andExpect(status().isOk());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
        ClassEntity testClassEntity = classEntityList.get(classEntityList.size() - 1);
        assertThat(testClassEntity.getClassId()).isEqualTo(UPDATED_CLASS_ID);
        assertThat(testClassEntity.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingClassEntity() throws Exception {
        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();
        classEntity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, classEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(classEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClassEntity() throws Exception {
        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();
        classEntity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(classEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClassEntity() throws Exception {
        int databaseSizeBeforeUpdate = classEntityRepository.findAll().size();
        classEntity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassEntityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(classEntity))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassEntity in the database
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClassEntity() throws Exception {
        // Initialize the database
        classEntityRepository.saveAndFlush(classEntity);

        int databaseSizeBeforeDelete = classEntityRepository.findAll().size();

        // Delete the classEntity
        restClassEntityMockMvc
            .perform(delete(ENTITY_API_URL_ID, classEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClassEntity> classEntityList = classEntityRepository.findAll();
        assertThat(classEntityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
