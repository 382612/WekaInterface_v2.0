package nl.bioinf.wekainterface.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to process and save datasets as arff's
 * @author Jelle, Marijke
 */

@Component
public class DataReader implements Reader{
    @Value("${example.data.path}")
    private String exampleFilesFolder;
    @Value("${temp.data.path}")
    private String tempFolder;

    /**
     * This method reads an arff file and returns the instances.
     * @param file file as a File object
     * @return Dataset instances
     * @throws IOException if the file doesn't exist
     */
    @Override
    public Instances readArff(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArffLoader.ArffReader arffReader = new ArffLoader.ArffReader(reader);
        Instances data = arffReader.getData();
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    /**
     * Convert a CSV file to arff Format
     * @param file file as File object
     * @return Instances data
     * @throws IOException if file doesn't exist
     */
    @Override
    public Instances readCsv(File file, String delimiter) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(file);
        loader.setFieldSeparator(delimiter);
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    /**
     * This method creates a list of the demo data names.
     * @return List of demo filenames
     */
    @Override
    public List<String> getDataSetNames() {
        File folder = new File(exampleFilesFolder);
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (File file: listOfFiles){
            fileNames.add(file.getName());
        }
        return fileNames;
    }
}
