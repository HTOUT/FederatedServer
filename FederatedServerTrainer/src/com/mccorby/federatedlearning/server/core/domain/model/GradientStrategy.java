package com.mccorby.federatedlearning.server.core.domain.model;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface GradientStrategy {
    INDArray processGradient(INDArray averageFlattenGradient, INDArray gradient);
}
