public class Sentinal<T>
{
	public Sentinal <T> last;
	public Sentinal <T> first;
	public Sentinal <T> tempSentinal=null;
	public Sentinal <T>next =null;
	public T value = null;
	public int lengthinchunks = 0;
	public boolean active = true;
	
	public void AddValue(T val)
	{
			
			if(first == null)
				first = this;
			if(first.last == null)			
				first.last =this;
			first.lengthinchunks++;
			first.last.next = new Sentinal<T>();
			first.last.next.value = val;
			first.last.next.first = first;
			first.last = first.last.next;
	}
	public void DeleteNode()
	{
		if(active)
		{
			first.lengthinchunks--;
			active = false;
		}
	}
	public int Len(){return first==null?0:first.lengthinchunks;}
	public void swapBuffer()
	{
		if(tempSentinal!=null)
		{
			this.first = tempSentinal.first;
		}
	}
}