package nl.bioinf.wekainterface.webcontrol;

import nl.bioinf.wekainterface.model.AlgortihmsInformation;
import nl.bioinf.wekainterface.model.DataReader;
import nl.bioinf.wekainterface.model.LabelCounter;
import nl.bioinf.wekainterface.model.WekaClassifier;
import nl.bioinf.wekainterface.service.ClassificationService;
import nl.bioinf.wekainterface.service.FileService;
import nl.bioinf.wekainterface.service.SerializationService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.text.SimpleDateFormat;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Marijke Eggink, Jelle Becirspahic, Bart Engels
 */

@Controller
public class ExplorerController {
    @Value("${example.data.path}")
    private String exampleFilesFolder;
    @Value("${temp.data.path}")
    private String tempFolder;
    @Autowired
    private DataReader dataReader;
    @Autowired
    private LabelCounter labelCounter;
    @Autowired
    private WekaClassifier wekaClassifier;
    @Autowired
    private ClassificationService classificationService;
    @Autowired
    private SerializationService serializationService;

    @GetMapping(value = "/upload")
    public String getFileUpload(Model model){
        List<String> filenames = dataReader.getDataSetNames();
        model.addAttribute("filenames", filenames);
        return "fileUpload";
    }

    @PostMapping(value = "/upload")
    public String postFileUpload(@RequestParam(name = "filename") String fileName,
                                 Model model, RedirectAttributes redirect) throws Exception {
        String arffFilePath = exampleFilesFolder + '/' + fileName;

        labelCounter.readData(new File(arffFilePath));
        labelCounter.setGroups();
        labelCounter.countLabels();
        //redirect.addFlashAttribute("filename", fileName);
        redirect.addFlashAttribute("data", labelCounter.mapToJSON());
        redirect.addFlashAttribute("attributes", labelCounter.getAttributeArray());
        redirect.addFlashAttribute("classLabel", labelCounter.getClassLabel());
        return "redirect:/explorer";
    }

    @GetMapping(value = "/explorer")
    public String getExplorerPage(Model model){
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        //String filename = (String)model.getAttribute("filename");
        //System.out.println(filename);
        //model.addAttribute("filename", filename);
        return "explorer";
    }

    @PostMapping(value = "/explorer")
    public String postExplorerPage(@RequestParam(name = "inputFile", required = false) MultipartFile multipart,
                                   @RequestParam(name = "filename", required = false) String demoFile,
                                   @RequestParam(name = "classifier") String classifierName,
                                   Model model, RedirectAttributes redirect,
                                   HttpSession httpSession) throws Exception {
        File arffFile;

        if (!multipart.isEmpty()){
            arffFile = File.createTempFile("temp-", ".arff", new File(tempFolder));
            InputStream inputStream = multipart.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, arffFile);
        } else {
            String arffFilePath = exampleFilesFolder + '/' + demoFile;
            arffFile = new File(arffFilePath);
        }
        if (httpSession.getAttribute("history") == null){
            ArrayList<AlgortihmsInformation> algorithmsInformation = new ArrayList<>();
            httpSession.setAttribute("history", algorithmsInformation);

        }

        if (httpSession.getAttribute("uniqueId") == null){
            String uniqueId = UUID.randomUUID().toString();
            File serFile = File.createTempFile(uniqueId, ".ser", new File("/tmp/"));
            httpSession.setAttribute("uniqueId", serFile);
        }

        Evaluation evaluation = classificationService.classify(arffFile, classifierName);
        ArrayList<AlgortihmsInformation> history = (ArrayList<AlgortihmsInformation>)httpSession.getAttribute("history");
        history.add(new AlgortihmsInformation(demoFile, classifierName, new SimpleDateFormat("HH:mm:ss"), evaluation));
        serializationService.serialization(history, (File) httpSession.getAttribute("uniqueId"));

        redirect.addFlashAttribute("evaluation", evaluation);
        System.out.println(evaluation);
        return "redirect:/explorer/results";
    }

    @GetMapping(value = "/explorer/results")
    public String getResultsPage(Model model) throws Exception {
        return "results";
    }

    @GetMapping(value = "/test")
    public String plotWeatherData(Model model) throws IOException {
        String file = exampleFilesFolder + '/' + "weather.nominal.arff";
        labelCounter.readData(new File(file));
        labelCounter.setGroups();
        labelCounter.countLabels();
        model.addAttribute("data", labelCounter.mapToJSON());
        model.addAttribute("attributes", labelCounter.getAttributeArray());
        model.addAttribute("classLabel", labelCounter.getClassLabel());
        return "dataExplorer";
    }
}