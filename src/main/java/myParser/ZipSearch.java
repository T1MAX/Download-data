package myParser;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Logger;


public class ZipSearch extends SimpleFileVisitor<Path> {

    //private static Logger log = Logger.getLogger(ZipSearch.class.getName());

    private ArrayList<Path> archived = new ArrayList<>();

    // ищем файлы, имя которых заканчивается на .zip, и добавляем их в список
    @Override
    public FileVisitResult visitFile (Path path, BasicFileAttributes attributes) {
        if(path.toString().endsWith(".zip"))
            archived.add(path);
        return FileVisitResult.CONTINUE;
    }

    public ArrayList<Path> getArchived() {
        return archived;
    }
}