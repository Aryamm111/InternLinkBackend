// package com.internlink.internlink.model;

// import ai.djl.huggingface.tokenizers.Encoding;
// import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
// import ai.djl.ndarray.NDArray;
// import ai.djl.ndarray.NDList;
// import ai.djl.translate.Batchifier;
// import ai.djl.translate.Translator;
// import ai.djl.translate.TranslatorContext;

// public class BertTranslator implements Translator<String, float[]> {
//     private HuggingFaceTokenizer tokenizer;

//     public BertTranslator() {
//         tokenizer = HuggingFaceTokenizer.newInstance("bert-base-uncased");
//     }

//     @Override
//     public NDList processInput(TranslatorContext ctx, String input) {
//         Encoding encoding = tokenizer.encode(input);
//         NDArray inputIds = ctx.getNDManager().create(encoding.getIds()).reshape(1, -1);
//         NDArray attentionMask = ctx.getNDManager().create(encoding.getAttentionMask()).reshape(1, -1);

//         return new NDList(inputIds, attentionMask);
//     }

//     @Override
//     public float[] processOutput(TranslatorContext ctx, NDList list) {
//         System.out.println("Entering processOutput method");
//         System.out.println("NDList size: " + list.size());

//         NDArray output = list.get(0); // First element contains the embeddings
//         System.out.println("First output shape: " + output.getShape());

//         // Ensure the output is (1, 384) as expected
//         float[] embeddings = output.squeeze().toFloatArray(); // Flatten the array properly
//         System.out.println("Final embedding size: " + embeddings.length);

//         if (embeddings.length != 384) {
//             throw new IllegalStateException(
//                     "Unexpected model output shape! Expected 384, but got: " + embeddings.length);
//         }

//         return embeddings;
//     }

//     @Override
//     public Batchifier getBatchifier() {
//         return null;
//     }
// }
