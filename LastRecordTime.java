public class LastRecordTime
{
	public long recordTime = 0;
	public void Update()
	{
		recordTime = System.nanoTime();
	}
}