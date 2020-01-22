package domain;

public class Counter {

    private long num = 0;

    public Long getAndIncrement(){
        return ++num;
    }
}
