
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;


public class SystemStorageTest {


    @Test
    public void testCreate() {
        SystemStorage storage = new SystemStorage("Test1.txt");
        String input = "1\n1\n1\n1\n1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        storage.process("create");
        Assertions.assertEquals(storage.getTasks().size(), 1);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("Test1.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String currentLine = null;
        try {
            currentLine = br.readLine();
        } catch (IOException ignored) {
        }
        Assertions.assertEquals(currentLine, "[{\"name\":\"1\",\"code\":\"1\",\"executor\":" +
                "{\"name\":\"1\"},\"status\":\"1\"}]");
        File file = new File("Test1.txt");
        Assertions.assertTrue(file.delete());

    }

    @Test
    public void testChange() {
        SystemStorage storage = new SystemStorage("Test1.txt");
        String input = "1\n1\n1\n1\n1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        storage.process("create");

        input = "1\n2\n2\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        storage.process("change task");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("Test1.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String currentLine = null;
        try {
            currentLine = br.readLine();
        } catch (IOException ignored) {
        }
        Assertions.assertEquals(currentLine, "[{\"name\":\"1\",\"code\":\"1\",\"executor\":" +
                "{\"name\":\"2\"},\"status\":\"2\"}]");
        File file = new File("Test1.txt");
        Assertions.assertTrue(file.delete());
    }
    @Test

    public void testShowCommands() {
        SystemStorage storage = new SystemStorage("Test1.txt");
        String input = "1\n1\n1\n1\n1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        storage.process("create");

        input = "2\n2\n2\n2\n2\n";;
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        storage.process("create");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        storage.process("show all");
        String output = "name 1\n" +
                "code 1\n" +
                "executor 1\n" +
                "status 1\n" +
                "\n" +
                "name 2\n" +
                "code 2\n" +
                "executor 2\n" +
                "status 2\n\n";
        Assertions.assertEquals(outContent.toString(), output);

        input = "1\n";;
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        storage.process("show task");
        output = "Enter task name\nname 1\n" +
                "code 1\n" +
                "executor 1\n" +
                "status 1\n" ;
        Assertions.assertEquals(outContent.toString(), output);

        File file = new File("Test1.txt");
        Assertions.assertTrue(file.delete());
    }

}
