package com.internlink.internlink.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;

public class BertModel {
    private final ZooModel<String, float[]> model;
    private final Predictor<String, float[]> predictor;

    public BertModel() throws ModelException, IOException {
        model = ModelZoo.loadModel(
                Criteria.builder()
                        .setTypes(String.class, float[].class)
                        .optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2")
                        .optTranslator(new BertTranslator())
                        .build());

        predictor = model.newPredictor();
    }

    public List<Float> getEmbedding(String text) throws TranslateException {

        float[] embedding = predictor.predict(text);

        List<Float> embeddingList = new ArrayList<>();
        for (float value : embedding) {
            embeddingList.add(value);
        }
        return embeddingList;
    }
}