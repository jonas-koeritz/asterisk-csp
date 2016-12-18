package opencsp.util;

public class State {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public State(String name)
    {
        this.name = name;
    }
}
