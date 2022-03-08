public class Counter {
    private int currentValue;
    public Counter()
    {
        currentValue = 0;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void add(int num)
    {
        currentValue += num;
    }
    public void subtract(int num)
    {
        currentValue -= num;
    }

}
