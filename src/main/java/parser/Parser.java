package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Parser {
    public void parseFileAndRunProductions(String pathname) {
        Scanner scanner;
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource(pathname)).getFile());
        try {
            scanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        while(scanner.hasNext()){
            String lineDelimited = scanner.nextLine();
            String[] numbers = lineDelimited.split(", ");
            System.out.println(Arrays.toString(numbers));
        }
    }
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.parseFileAndRunProductions("production.txt");
    }
}
