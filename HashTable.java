public class HashTable
{
	public V2[][] data = null;
	public int elementsCount = 0;
	int [] trials = new int []{3,5,7,11,17,23,29,31,37,41,43,47,97};
	public HashTable(int size)
	{
		data = new V2[size][2];
		for (int i = 0; i<size ;i++ ) 
		{
			data[i] = null;
		}
		
	}
	public boolean add(V2 a,V2 b)
	{
		if(elementsCount< data.length-data.length/200)
		{	
			int index = R.LineHash(a,b);
			if(index>=data.length)
				index = index % data.length;
			int performanceCounter =  0;
			int maxTrialsCount =100 * trials.length;
			while(data[index]!=null)
			{
				performanceCounter++;
				if(performanceCounter>= maxTrialsCount)
				{
					R.println(index);
					return false;
				}
				index = (index*trials[performanceCounter%trials.length]+performanceCounter)%(data.length);
			}			
			elementsCount++;
			data[index]=new V2[2];
			data[index][0] = a;
			data[index][1] = b;
			return true;
		}
		return false;
	}
	public boolean remove(V2 a, V2 b)
	{
		if(elementsCount == 0||a==null||b==null) { return false;}

		int indexA = R.LineHash(a,b) %  data.length;
		int indexB = R.LineHash(b,a) %  data.length;

		int iterationCounter = 0;
		int maxTrialsCount = 100 * trials.length;

		while(iterationCounter != maxTrialsCount)
		{		
			if((data[indexA]!=null)&&(
			  (a.eq(data[indexA][0])&&b.eq(data[indexA][1]))
			||(b.eq(data[indexA][0])&&a.eq(data[indexA][1]))))
			{
				data[indexA]=null;
				elementsCount--;
				return true;
			}
			else if(data[indexB]!=null&&
				(
			  (a.eq(data[indexB][0])&&b.eq(data[indexB][1]))
			||(b.eq(data[indexB][0])&&a.eq(data[indexB][1]))
				))
			{
				data[indexB]=null;
				elementsCount--;
				return true;
			}
			iterationCounter++;
			indexA = (indexA*trials[iterationCounter%trials.length]+iterationCounter)%(data.length);
			indexB = (indexB*trials[iterationCounter%trials.length]+iterationCounter)%(data.length);
			
		}
		R.println("Couldn't find the line");
		return false;
	}
	}
