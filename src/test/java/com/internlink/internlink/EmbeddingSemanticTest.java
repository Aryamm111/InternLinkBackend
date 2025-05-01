package com.internlink.internlink;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.internlink.internlink.model.Internship;
import com.internlink.internlink.model.Student;
import com.internlink.internlink.service.EmbeddingService;
import com.internlink.internlink.service.InternshipService;
import com.internlink.internlink.service.StudentService;

@SpringBootTest
public class EmbeddingSemanticTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private InternshipService internshipService;

    @Test
    public void testSimilarityBetweenStudentAndInternships() throws Exception {
        // Computer science student
        Student student = studentService.getStudentById("421233");
        // Python Automation Developer Intern
        Internship internship1 = internshipService.getInternshipById("680e322cf1eb742e8ad8f41c");
        // Analytical Chemistry Lab Assistant Intern
        Internship internship2 = internshipService.getInternshipById("680e9ef1c3ba2e0908aa1e43");
        // Law
        Internship internship3 = internshipService.getInternshipById("680e945aa1c65016cbd9c401");
        // Generate embeddings
        List<Float> studentVec = student.getEmbedding();
        List<Float> relevantVec = internship1.getEmbedding();
        List<Float> unrelatedVec = internship2.getEmbedding();
        List<Float> unrelatedVec2 = internship3.getEmbedding();

        // Compare
        float sim1 = cosineSimilarity(studentVec, relevantVec);
        float sim2 = cosineSimilarity(studentVec, unrelatedVec);
        float sim3 = cosineSimilarity(studentVec, unrelatedVec2);

        System.out.println("Relevant similarity: " + sim1);
        System.out.println("Unrelated similarity: " + sim2);
        System.out.println("Unrelated similarity: " + sim3);

    }

    public static float cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        if (vec1 == null || vec2 == null || vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must be non-null and the same length.");
        }

        float dot = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;

        for (int i = 0; i < vec1.size(); i++) {
            float a = vec1.get(i);
            float b = vec2.get(i);
            dot += a * b;
            norm1 += a * a;
            norm2 += b * b;
        }

        return (float) (dot / (Math.sqrt(norm1) * Math.sqrt(norm2)));
    }
}
