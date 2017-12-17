package myParser;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZipReader {

    public static List<JSONObject> readZipAndSearchXml(Path pathZipFile) throws IOException {
        FileSystem zipFileSystem = createZipFileSystem(pathZipFile);
        Path root = zipFileSystem.getPath("/");
        XmlSearch xmlSearch = new XmlSearch();
        Files.walkFileTree(root, xmlSearch);
        return xmlSearch.getContracts();
    }

    private static FileSystem createZipFileSystem(Path path) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URI uri = URI.create("jar:file:" + path.toUri().getPath());
        return FileSystems.newFileSystem(uri, env);
    }

    public static List<Path> searchZipFiles(Path path) throws IOException {
        ZipSearch zipSearch = new ZipSearch();
        Files.walkFileTree(path, zipSearch);
        return zipSearch.getArchived();
    }
}