package processor;

import model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by chiranz on 7/14/17.
 */
public class Extactor {
    private static final Logger log = Logger.getLogger(Extactor.class.getName());

    private String PATTERN = ".";

    public List<Element> getElements(String bookPath, String pattern, boolean withparent, boolean withresult) throws Exception {
        PATTERN = pattern;
        final int[] total = {0};
        final int[] totalFilecount = {0};

        List<Element> elementsList = new ArrayList<>();
        List<Result> results = new ArrayList<>();
        List<File> files = getFiles(bookPath, "XHTML");
        files.stream().forEach(p -> {
                    try {
                        totalFilecount[0] += 1;
                        Document doc = Jsoup.parse(p, "UTF-8");
                        Elements elements = doc.select(PATTERN);
                        if (elements.size() > 0)
                            System.out.println("*************************************************************");

//                        List<String> ids = divs.stream().map(n ->
//                                n.attr("id")
//                        ).collect(Collectors.toList());
                        Result result = new Result();
                        result.setFilename(p.getName());
                        elementsList.addAll(elements);
                        ArrayList<String> strings = new ArrayList<>();
                        elements.forEach(element -> {
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            if (withparent) {
                                log.log(Level.INFO, p.getName() + " : \n" + element.parent().toString());
                                strings.add(element.parent().toString());
                            } else {
                                log.log(Level.INFO, p.getName() + " : \n" + element.toString());
                                strings.add(element.toString());
                            }
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//                            System.out.println(p.getName()+" : "+element.toString());
                            result.setElements(strings);
                        });
                        total[0] += elements.size();
                        if (elements.size() > 0) {
                            result.setElementCount(elements.size());
                            results.add(result);
                            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                        System.out.println(p.getName()+" has  "+elements.size()+" elements");
                            log.log(Level.INFO, p.getName() + " has  " + elements.size() + " elements");
                            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                        }
                        if (elements.size() > 0)
                            System.out.println("*************************************************************");
                    } catch (IOException e) {
                        log.log(Level.SEVERE, e.getMessage());

                        e.printStackTrace();
                    }

                }
        );
//        System.out.println(bookPath + " has  " + total[0] + " elements");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        if (withresult) {
            System.out.println("@@@@@@@@         FINAL              RESULT @@@@@@@@@@@@@@@@@@");
            log.log(Level.INFO, bookPath + " Result  " + results.toString());
        }
        System.out.println("@@@@@@@@@@@@@@@@      SUMMARY   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.log(Level.INFO, bookPath + " has  " + total[0] + " Elements");
        log.log(Level.INFO, bookPath + " has  " + results.size() + " XHTML files with relevant elements");
        log.log(Level.INFO, bookPath + " has  " + totalFilecount[0] + " XHTMLFiles");

        return elementsList;
    }

    public List<File> getFiles(String path, String extention) throws IOException {
        return Files.walk(Paths.get(path)).filter(p -> p.toString().toUpperCase().endsWith("." + extention.toUpperCase())).distinct().
                map(n -> n.toFile()).collect(Collectors.toList());
    }
}
