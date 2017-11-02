package com.mccorby.federatedlearning.server.core.domain.model;

import org.deeplearning4j.nn.gradient.Gradient;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface FederatedModel {

    String getId();

    void updateWeights(INDArray remoteGradient);

    INDArray getGradientAsArray();

    void updateWeights(Gradient averageGradient);

    Gradient getGradient();

    void buildModel();

    void train(FederatedDataSet federatedDataSet);

    String evaluate(FederatedDataSet federatedDataSet);

}