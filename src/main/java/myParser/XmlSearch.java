package myParser;

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
import java.util.stream.Collectors;

public class XmlSearch extends SimpleFileVisitor<Path> {

    //private static Logger log = Logger.getLogger(XmlSearch.class.getName());

    private List<JSONObject> protocols = new ArrayList<>();

    @Override
    public FileVisitResult visitFile (Path path, BasicFileAttributes attributes) throws IOException {
        Pattern patternOpenTag = Pattern.compile("(.*<)([\\w]*:)(.*)");
        Pattern patternCloseTag = Pattern.compile("(.*</)([\\w]*:)(.*)");
        if(path.toString().endsWith(".xml")) {
            String content = Files.lines(path).map((s) -> {
                Matcher matcher = patternOpenTag.matcher(s);
                if (matcher.matches()) s = new StringBuilder(matcher.group(1))
                                            .append(matcher.group(3))
                                            .toString();
                matcher = patternCloseTag.matcher(s);
                if (matcher.matches()) s = new StringBuilder(matcher.group(1))
                        .append(matcher.group(3))
                        .toString();
                return s;
            }).collect(Collectors.joining());
            JSONObject json;
            try {
                json = XML.toJSONObject(content);
                if (json.has("purchaseProtocolZK")) {
                    JSONObject protocolZKData = json.getJSONObject("purchaseProtocolZK")
                            .getJSONObject("body")
                            .getJSONObject("item")
                            .getJSONObject("purchaseProtocolZKData");
                    if (protocolZKData.getJSONObject("lotApplicationsList").has("protocolLotApplications"))
//                        if (protocolZKData.getJSONObject("lotApplicationsList")
//                                .getJSONObject("protocolLotApplications")
//                                .has("application"))
                        protocols.add(protocolZKData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FileVisitResult.CONTINUE;
    }

    public List<JSONObject> getProtocols() {
        return protocols;
    }
}