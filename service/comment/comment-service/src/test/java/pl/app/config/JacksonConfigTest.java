package pl.app.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JacksonConfigTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws JsonProcessingException {
        ObjectId id = new ObjectId("669ba617e3f38965343a34ec");
        String json = objectMapper.writeValueAsString(new TestObject(id));
        String expectedJson = """
                {"id":"669ba617e3f38965343a34ec"}
                """.trim();
        assertThat(json).isEqualTo(expectedJson);
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = """
                {"id":"669ba617e3f38965343a34ec"}
                """.trim();
        TestObject testObject = objectMapper.readValue(json, TestObject.class);
        assertThat(testObject.getId()).isEqualTo(new ObjectId("669ba617e3f38965343a34ec"));
    }

    private static class TestObject {
        private ObjectId id;

        public TestObject() {
        }

        public TestObject(ObjectId id) {
            this.id = id;
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }
    }
}