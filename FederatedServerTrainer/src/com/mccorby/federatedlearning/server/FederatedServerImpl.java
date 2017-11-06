package com.mccorby.federatedlearning.server;


import com.mccorby.federatedlearning.server.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.server.core.domain.model.GradientStrategy;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This object mocks what an actual server would do in a complete system
 * In real life, the server would send a notification to the clients indicating a new
 * average gradient is available. It would be then responsibility of the client to decide
 * when to download it and process it
 */
public class FederatedServerImpl implements FederatedServer {

    private static FederatedServerImpl sInstance;
    private List<FederatedModel> registeredModels;
    private INDArray averageFlattenGradient;
    private GradientStrategy strategy;
    private Logger logger;
    private int models;

    public static FederatedServerImpl getInstance() {
        if (sInstance == null) {
            Logger logger = System.out::println;
            sInstance = new FederatedServerImpl(new AverageGradientStrategy(logger), logger);
        }

        return sInstance;
    }

    private FederatedServerImpl(GradientStrategy strategy, Logger logger) {
        this.strategy = strategy;
        this.logger = logger;
    }

    @Override
    public void registerModel(FederatedModel model) {
        // This is only for Push notifications of changes to the gradients
        if (registeredModels == null) {
            registeredModels = new ArrayList<>();
        }
        registeredModels.add(model);
    }


    private void processGradient(INDArray gradient) {
        averageFlattenGradient = strategy.processGradient(averageFlattenGradient, gradient);
    }

    @Override
    public byte[] sendUpdatedGradient() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Nd4j.write(outputStream, averageFlattenGradient);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log("Sending gradient to the clients!");
        return outputStream.toByteArray();
    }

    public void pushGradient(byte[] clientGradient) {
        logger.log("Gradient received " + (clientGradient != null ? clientGradient.toString() : "null"));
        try {
            assert clientGradient != null;
            INDArray gradient = Nd4j.fromByteArray(clientGradient);
            processGradient(gradient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushGradient(InputStream is) {
        logger.log("Gradient received " + (is != null ? is.toString() : "null"));
        try {
            INDArray gradient = Nd4j.read(is);
            processGradient(gradient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer registerNewModel() {
        return ++models;
    }
}
