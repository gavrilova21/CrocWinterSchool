import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SystemStorage {

    private ArrayList<Executor> executors = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();
    private String fileName = "TaskData.txt";

    public SystemStorage(){
        super();
        checkFileExists();
    }

    public SystemStorage(String fileName){
        super();
        this.fileName = fileName;
    }
    public void run(){
        readFromFile();
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object

        System.out.println("Enter new command");
        String command = myObj.nextLine();
        while (!command.equals("exit")){
            process(command);
            System.out.println("Enter new command");
            command = myObj.nextLine();
        }


    }
    private void printInfo(Task task){
        System.out.println("name " + task.getName());
        System.out.println("code " + task.getCode());
        System.out.println("executor "+ task.getExecutor().getName());
        System.out.println("status "+ task.getStatus());
    }
    private boolean checkUnique(String name){
        for (Task task: tasks){
            if(task.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    private Executor getExecutor(String name){
        for (Executor executor: executors){
            if(executor.getName().equals(name)){
                return executor;
            }
        }
        Executor executor = new Executor(name);
        executors.add(executor);
        return executor;
    }

    private void saveToFile(){
        ObjectMapper mapperWrite = new ObjectMapper();
        try {
            String json = mapperWrite.writeValueAsString(tasks);
            FileWriter file = new FileWriter(fileName);
            file.write(json);
            file.flush();
            file.close();
        } catch (IOException e) {
            System.out.println("Can not write to the file(");
        }
    }

    private void checkFileExists(){
        File f = new File(fileName);
        if(! f.exists()){
            try {
                f.createNewFile();
                saveToFile();
            } catch (IOException e) {
                System.out.println("Cannot create file");
            }
        }
    }

    private void readFromFile() {
        StringBuilder contentBuilder = new StringBuilder();
        ObjectMapper mapperRead = new ObjectMapper();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine);
            }
            br.close();
            tasks = mapperRead.readValue(contentBuilder.toString(), new TypeReference<List<Task>>() {});

        }
        catch (IOException ignored) {
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }
    public void process(String command){
        Scanner myObj = new Scanner(System.in);
        switch (command){
            case "create": {
                System.out.println("Enter task name");
                String name = myObj.nextLine();
                while (!checkUnique(name)){
                    System.out.println("This task name already exists! Try again");
                    name = myObj.nextLine();
                }
                System.out.println("Enter task code");
                String code = myObj.nextLine();
                System.out.println("Enter task executor name");
                String executor_name = myObj.nextLine();
                Executor executor = getExecutor(executor_name);
                System.out.println("Enter task status");
                String status = myObj.nextLine();
                Task task = new Task(name, code, executor, status);
                tasks.add(task);
                saveToFile();
                break;
            }
            case "change task": {
                System.out.println("Enter task name");
                String name = myObj.nextLine();
                if (checkUnique(name)){
                    System.out.println("Task with this name does not exist");
                } else {
                    System.out.println("Enter new executor");
                    String executor_name = myObj.nextLine();
                    System.out.println("Enter new status");
                    String status = myObj.nextLine();

                    for (Task task: tasks){
                        if(task.getName().equals(name)){
                            task.setExecutor(getExecutor(executor_name));
                            task.setStatus(status);
                        }
                    }
                    saveToFile();
                }
                break;
            }
            case "show task":{
                System.out.println("Enter task name");
                String name = myObj.nextLine();
                if (checkUnique(name)){
                    System.out.println("Task with this name does not exist");
                } else {
                    for (Task task: tasks){
                        if (task.getName().equals(name)){
                            printInfo(task);
                            break;
                        }
                    }
                    saveToFile();
                }
                break;
            }
            case "show all":{
                for (Task task: tasks){
                    printInfo(task);
                    System.out.println();
                }

                break;
            }
            default:
                System.out.println("unknown command");
                break;
        }
    }

    public static void main(String[] args) {
        SystemStorage storage = new SystemStorage();
        storage.run();
    }
}
