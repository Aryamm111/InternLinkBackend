
package com.internlink.internlink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.internlink.internlink.model.BertModel;

import ai.djl.translate.TranslateException;

@Service
public class EmbeddingService {

    private final BertModel bertModel;
    private static final int EXPECTED_EMBEDDING_SIZE = 384; // Expected size of the embedding vector

    // Constructor to initialize BertModel
    public EmbeddingService(BertModel bertModel) {
        this.bertModel = bertModel;
    }

    // Generates an embedding vector for the given text input
    public List<Float> generateEmbedding(String text) throws TranslateException {
        List<Float> embedding = bertModel.getEmbedding(text);

        // Validates that the generated embedding has the expected size
        if (embedding.size() != EXPECTED_EMBEDDING_SIZE) {
            throw new IllegalStateException("Embedding size mismatch! Expected: " + EXPECTED_EMBEDDING_SIZE +
                    ", but got: " + embedding.size());
        }

        return embedding;
    }
}
