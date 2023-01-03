package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassEntity.class);
        ClassEntity classEntity1 = new ClassEntity();
        classEntity1.setId(1L);
        ClassEntity classEntity2 = new ClassEntity();
        classEntity2.setId(classEntity1.getId());
        assertThat(classEntity1).isEqualTo(classEntity2);
        classEntity2.setId(2L);
        assertThat(classEntity1).isNotEqualTo(classEntity2);
        classEntity1.setId(null);
        assertThat(classEntity1).isNotEqualTo(classEntity2);
    }
}
