public class ExecutionCounter 
{
	public long t;
	public long lastValue=0;
	public static final double oneMS = 1e6;
	public static final double oneS = 1e9;
	public void Start()
	{
		t = System.nanoTime();
	}
	public long getDiffNano()
	{
		lastValue = System.nanoTime() - t;
		return lastValue;
	}
	public long getDiffMs()
	{
		return  toMs(System.nanoTime() - t);
	}
	public long toMs(long t)
	{
		return t/1000000 ;

	}
}