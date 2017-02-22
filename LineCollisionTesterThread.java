public class LineCollisionTesterThread implements Runnable
{
	public static  volatile Sentinal<V2[]> DrawnSegments;
	public V3 [] positions = null;
	public V3 [] directions = null;
	public Thread Activator = null;
	public V2[][] Lines = null;
	public int lineCounter =0;
	public int particleThreadsCount= 4;
	public Thread[] ParitcleThreads = new Thread[particleThreadsCount];

	public LineCollisionTesterThread(Thread Activator,Sentinal<V2[]> DrawnSegments,
									V3[] positions,
									V3[] directions)
	{
		this.DrawnSegments = DrawnSegments;
		this.Activator = Activator;
		this.positions = positions;
		this.directions = directions;
	}
	public void run()
	{
		boolean firstIteration = true;
		long t = System.nanoTime();
		int prevLineCounter = 0 ;
		while(true)
		{	
			if((System.nanoTime()-t)/1e6>1000)
			{
				t = System.nanoTime();
			}
			
			if(DrawnSegments.first != BallPhysicsTest.DrawnSegments.first || firstIteration)
			{
				R.println("Lines updated");
				lineCounter = 0;
				firstIteration = false;

				Lines = new V2[DrawnSegments.Len()][2];
				DrawnSegments = BallPhysicsTest.DrawnSegments;			
				{
					Sentinal <V2[]> currentLinePoint = DrawnSegments.first;
					
					while(currentLinePoint != null)
					{
						if(currentLinePoint.value!=null&&
							currentLinePoint.active)
						{
							Lines[lineCounter][0] = currentLinePoint.value[0].clone();
							Lines[lineCounter++][1] = currentLinePoint.value[1].clone();

							if(Lines[lineCounter-1][0].y>Lines[lineCounter-1][1].y)
							{
								R.swap(Lines[lineCounter-1],0,1);
							}
						}
						if(lineCounter>=Lines.length)
							break;
						currentLinePoint = currentLinePoint.next;
					}
					LinesSort(Lines,0,lineCounter-1);	
					prevLineCounter = lineCounter;
				}
			}
			// if(prevLineCounter>0)
			{
				for(int x = 0 ; x<particleThreadsCount;x++)
				{	
					if(ParitcleThreads[x] == null || !ParitcleThreads[x].isAlive())
					{
						ParitcleThreads[x]=new Thread(new positionsWorker(x,directions.length,particleThreadsCount));
						ParitcleThreads[x].start();
					}
				}
			}
			Thread.yield();
				//handleParticles(0,directions.length);
		}
	}
	public class positionsWorker implements Runnable
	{	
		int ThreadsCount = 0;
		int positionsWorkerId = 0; 
		int particlesCount = 0;
		public positionsWorker(int Id,int particlesCount,int ThreadsCount)
		{
			positionsWorkerId =Id; 
			this.particlesCount = particlesCount;
			this.ThreadsCount = ThreadsCount;
		}
		public void run()
		{	
			int xx = positionsWorkerId;
			
			int positionsPerThread = particlesCount/ThreadsCount;

				while(true)
				{
					try
					{
						handleParticles(xx*positionsPerThread,(xx+1)*positionsPerThread);	
						try{Thread.sleep(1);}
						catch(Exception e)
						{
							R.println("positionsWorker interrupted");
						}
					}
						catch(Exception e)
						{
							R.println(e);
						}
				}
		}
	}

	public void handleParticles(int start,int end)
	{
				for (int i = start; i<end;i++)
			{
				V3 position = positions[i];
				V3 direction = directions[i];
				if(position== null || direction == null)
					continue;
				V3 posdir = V3.add(position,direction);


				float posdirY = posdir.y;
				float positionY = position.y;
				if(lineCounter>=1)
				{
					float prevDist = Float.POSITIVE_INFINITY;
					for(int l =0 ;l<Lines.length;l++)
					{	

						if(position.y<Lines[l][0].y&&posdirY<Lines[l][0].y)
								continue;
						if(Lines[l][0]!=null&&Lines[l][1]!=null)
						{
						 	V3 posIntersection = R.IntersectionPoint(
						 			position,
						 			direction,
						 			Lines[l][0].toV3(),
						 			V2.sub(Lines[l][1],Lines[l][0]).toV3());

							if(!(posIntersection.x==0)||
								!(posIntersection.y==0))
							{					
								DeflectParticle
								(
									Lines[l][0].toV3(),
									Lines[l][1].toV3(),
									direction,
									S.energyLoss);
								//break;
							}
							else 
							{
								float currentDist = V3.sub(posdir,Lines[l][0]).len();
								if(prevDist>currentDist)
								{
									prevDist = currentDist;
								}
							}
							
						}
					}
				}
			}


	}
	public static void dumpLinesData(String fileName , V2[][]Lines,V3 position,V3 posDir,int lineCount)
	{
		StringBuffer buff= new StringBuffer();

		float MinYPos = R.min(position.y,posDir.y);
		float MaxYPos = R.max(position.y,posDir.y);
		int start =BinarySearchLinesOnY(Lines,MinYPos,0,lineCount-1,1);
		int end = BinarySearchLinesOnY(Lines,MaxYPos,0,lineCount-1,1);
		buff.append(Float.toString(MinYPos)).append("-").append(Float.toString(MaxYPos)).append("\n");
		for(int i = 0;i<Lines.length;i++)
		{
			if(Lines[i]!=null)
			{
				if(Lines[i][0]!=null && Lines[i][1] !=null)
					buff =buff.append(Float.toString(Lines[i][0].y)).append(" ").append(Float.toString(Lines[i][1].y)).append("* ").append(Integer.toString(start)).append(" _ ").append(Integer.toString(end)).append("\n");
				else 
					buff =buff.append(Integer.toString(i)).append("notFound");
			}		
		}
		String toDump = buff.toString();	
		R.OpenAndWriteToFile(fileName, toDump);
	}

	public Range GetClosestRangeForIntersection(V2[][]lines,V3 position,V3 direction,V3 posdir)
	{
		int start  = 0 ,end= 20;
		float distanceFromIntersectionPoint = Float.POSITIVE_INFINITY;
		Range currentRange = new Range(0,end);
		boolean FoundPoint = false;
		if (lines.length > 20)
		{
			for (int i=0; i<lines.length-end ; i++) 
			{
				int l = i+end;
				V3 intersectionPoint =  R.IntersectionPoint(
						 			position,
						 			direction,
						 			Lines[i][0].toV3(),
						 			V2.sub(Lines[l][1],Lines[i][0]).toV3());

				if(intersectionPoint.x != 0 ||intersectionPoint.y !=0)
				{
					FoundPoint = true;
				}
				float currentLength = V3.sub(intersectionPoint,posdir).len();

				if(distanceFromIntersectionPoint>currentLength)
				{
					distanceFromIntersectionPoint = currentLength;
					currentRange.start = i;
					currentRange.end = l;
				}
			}
		}
		if(FoundPoint)
			return currentRange;
		return new Range(0,0);
	}
	public static int LinesPartition(V2[][]lines,int s, int f)
	{		
		float t = lines[s][0].y;
		int i=s;
		for (int j = f; i<j;)
		{
			if(lines[i]==null || lines[j] == null)
				break;
			float iValue =lines[i][0].y;
			float jValue =lines[j][0].y;
			if (iValue<t)
				i++;
			if(jValue>t)
				j--;
			if(iValue>jValue)
			{	
				R.swap(lines,i,j);
			}
			if (iValue==jValue&&j!=i)
				i++;
		}
		return i;
	}
	public static void LinesSort(V2[][] lines,int s,int f)
	{	
		if(s>=f||s<0||f>=lines.length)
			return;
		int q=LinesPartition(lines,s, f);
		LinesSort(lines,s,q-1);
		LinesSort(lines,q+1,f);

	}
	public static void DeflectParticle(V3 positionA,V3 positionB,V3 direction,float energyLoss)
	{
		V3 d0 =  V3.sub(positionA,positionB);
		d0.normalize();
		V3 d1 = d0.l90();
		direction.sMul(-1);
		float l2 = direction.dot(d1);
		d1.sMul(l2); // 
		V3 a = V3.sub(direction,d1);
		a.sMul(2);
		direction.sub(a);
		direction.sMul(energyLoss);
	}	
	public static int BinarySearchLinesOnY(V2[][]Lines,float y,int s,int f,float epsilon)
	{
		int arrayRLimit = f;		
		if(y<Lines[s][0].y)
			return -1;
		else if (y>Lines[f][1].y)
			return f;
		while(f>s)
		{
			int currentIndex= (s+f)/2;
			float currentValMin = Lines[currentIndex][0].y;
			if (currentValMin>y)
			{
				f=currentIndex-1;
			}
			else if (currentValMin<y)
			{
				s=currentIndex+1;
			}
			else if (R.eq(y,currentValMin,epsilon))
			{
				return s-1;
			}
			if (s==f)
			{
				return s;
			}
		}
		return (int)R.min(arrayRLimit,f);
	}

}