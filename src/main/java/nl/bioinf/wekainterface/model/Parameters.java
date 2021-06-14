package nl.bioinf.wekainterface.model;

import weka.core.neighboursearch.*;

import java.util.Map;

public class Parameters {
    private final String classifier;
    private final Map<String, String> parameters;

    public Parameters(Map<String, String> parameters) {
        this.classifier = parameters.get("classifier");
        this.parameters = parameters;
    }

    public String getBatchSize() {
        return parameters.get("batchsize-" + classifier);
    }

    public boolean isDebug() {
        return Boolean.parseBoolean(parameters.get("debug-" + classifier));
    }

    public boolean isCapabilities() {
        return Boolean.parseBoolean(parameters.get("doNotCheckCapabilities-" + classifier));
    }

    public int getDecimal() {
        return Integer.parseInt(parameters.get("numDecimalPlaces-" + classifier));
    }

    public int getMinBucketSize() {
        return Integer.parseInt(parameters.get("minBucketSize-" + classifier));
    }

    public float getConfidenceFactor() {
        return Float.parseFloat(parameters.get("confidenceFactor-"  + classifier));
    }

    public int getNumObj() {
        return Integer.parseInt(parameters.get("minNumObj-" + classifier));
    }

    public int getNumFolds() {
        return Integer.parseInt(parameters.get("numFolds-" + classifier));
    }

    public boolean getPruned() {
        return !Boolean.parseBoolean(parameters.get("pruned-" + classifier));
    }

    public int getKNN() {
        return Integer.parseInt(parameters.get("KNN-" + classifier));
    }

    public boolean getCrossValidate() {
        return Boolean.parseBoolean(parameters.get("crossValidate-" + classifier));
    }

    public NearestNeighbourSearch getSearchAlgorithm() {
        return getAlgorithm(parameters.get("nnSearchAlgorithm-" + classifier));
    }

    /**
     * This method gets the search nearest neighbour search algorithm for IBK.
     * @param algorithm name of the search algorithm
     * @return NearestNeighbourSearch algorithm
     */
    public NearestNeighbourSearch getAlgorithm(String algorithm){
        NearestNeighbourSearch nearestNeighbourSearch;
        switch (algorithm){
            default:
                nearestNeighbourSearch = new BallTree();
                break;
            case "CoverTree":
                nearestNeighbourSearch = new CoverTree();
                break;
            case "FilteredNeighbourSearch":
                nearestNeighbourSearch = new FilteredNeighbourSearch();
                break;
            case "KDTree":
                nearestNeighbourSearch = new KDTree();
                break;
            case "LinearNNSearch":
                nearestNeighbourSearch = new LinearNNSearch();
                break;
        }
        return nearestNeighbourSearch;
    }
}
