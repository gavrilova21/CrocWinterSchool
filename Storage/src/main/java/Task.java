public class Task {
    private String name;
    private String code;
    private Executor executor;
    private String status;

    public Task() {
        super();
    }
    public Task(String name, String code, Executor executor, String status) {
        this.name = name;
        this.code = code;
        this.executor = executor;
        this.status = status;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public Executor getExecutor() {
        return executor;
    }
}
