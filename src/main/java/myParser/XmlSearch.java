package myParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class XmlSearch extends SimpleFileVisitor<Path> {
    private List<JSONObject> contracts = new ArrayList<>();

    @Override
    public FileVisitResult visitFile (Path path, BasicFileAttributes attributes) throws IOException {
        Pattern patternOpenTag = Pattern.compile("(.*)(<[\\w]*:)(.*)");
        Pattern patternCloseTag = Pattern.compile("(.*)(</[\\w]*:)(.*)");
        if(path.toString().endsWith(".xml")) {
            String content = Files.lines(path).map((s) -> {
                Matcher matcher = patternOpenTag.matcher(s);
                if (matcher.matches()) s = new StringBuilder(matcher.group(1))
                                            .append("<")
                                            .append(matcher.group(3))
                                            .toString();
                matcher = patternCloseTag.matcher(s);
                if (matcher.matches()) s = new StringBuilder(matcher.group(1))
                        .append("</")
                        .append(matcher.group(3))
                        .toString();
                return s;
            }).collect(Collectors.joining());
            JSONObject json;
            try {
                json = XML.toJSONObject(content);
                if (json.has("contract"))
                    contracts.add(json.getJSONObject("contract")
                            .getJSONObject("body")
                            .getJSONObject("item")
                            .getJSONObject("contractData"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FileVisitResult.CONTINUE;
    }

    public void addContracts(String body, boolean highPriority) throws JSONException {

    }
    public List<JSONObject> getContracts() {
        return contracts;
    }
}