public class Sentinal<T>
{
	public Sentinal <T> last = null;
	public Sentinal <T> first = null;
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
			if (value != null)
			{
				first.last.next.value = val;
				first.last.next.first = first;
				first.last = first.last.next;
			}
			else
			{
				value = val;
			}
	}
	public void DeleteNode()
	{
		if(active)
		{
			first.lengthinchunks--;
			active = false;
		}
	}
	public Sentinal DeleteNode(Sentinal prev)
	{
		if(this == first)
		{
			if(next!=null)
			{
				next.first = next;
				next.first.lengthinchunks = lengthinchunks-1;
				last.first = next;
				next.last = last;
				return next;
			}
			else return null;

		}
		if(this==first.last)
		{
			first.last=prev;
		}
		prev.next = next;
		next = null;
		first.lengthinchunks--;
		
		return prev;
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