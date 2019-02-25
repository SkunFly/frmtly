import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Search files in a repository by name and by presence of string.
 */
public class FindFile {

    private final static String REP_PATH = "rep/";

    public static void main(String[] args) throws IOException {
        for (String s : findFile("https://github.com/SkunFly/32bits-operative-system", "keyboard.c", "#define ENTER 0x1C")) {
            System.out.println(s);
        }
    }

    public static String[] findFile(String gitRepo, String fileName, String string) {
        File dir = new File(REP_PATH);
        Git git = null;
        deleteFolder(dir);

        try {
            git = Git.cloneRepository()
                    .setURI(gitRepo)
                    .setDirectory(new File(REP_PATH))
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        List<File> results = findByName(dir, fileName);
        if(string != null && !string.equals("*")){
            results = findByString(results, string);
        }

        String[] output = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            Path path = Paths.get(results.get(i).getPath());
            output[i] = path.subpath(1, path.getNameCount()).toString();
        }

        return output;
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private static List<File> findByName(File dir, String name){
        List<File> matchingFiles = new ArrayList<>();
        if(dir != null && dir.isDirectory()){
            for (File file : dir.listFiles()) {
                if(file.isDirectory())
                    matchingFiles.addAll(findByName(file, name));
                else
                    if(file.getName().equals(name))
                        matchingFiles.add(file);
            }
        }
        return matchingFiles;
    }

    private static List<File> findByString(List<File> files, String string){
        List<File> matchingFiles = new ArrayList<>();
        for (File file : files) {
            try {
                if(Files.lines(file.toPath()).anyMatch(s -> s.contains(string)))
                    matchingFiles.add(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return matchingFiles;
    }
}
