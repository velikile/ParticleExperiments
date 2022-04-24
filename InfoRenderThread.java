import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.MouseInfo;
public class InfoRenderThread implements Runnable
{
	public enum State {sleep,gravity,energyLoss,eraserSize,particlesShown }
	State state = State.sleep;
	public static Graphics G = null;
	public static boolean work = false;
	public static MouseState MS;
	public static KeysState KS;
	
	public LastRecordTime KeysRecordTime = new LastRecordTime();
	public LastRecordTime MouseRecordTime = new LastRecordTime();
	public String [] values = null;
	public int startLeft = 0;
	public int startTop = 0;
	public int currentRowHeight = 0;
	public int currentLetterWidth = 0;
	public int RecordingCount = 0;
	public char [] Recording = null;
	public Thread ActivatorThread = null;
	public ExecutionCounter EC=  new ExecutionCounter();
	public static Sentinal<V2[]> DrawnSegments = null;
	public int deleteRadius = 5;
	public boolean RecordUpdateTime(LastRecordTime LRT)
	{
		long prevTime = LRT.recordTime;
		return (System.nanoTime()-prevTime)>1e8;//1e9 is a second
	}
	public InfoRenderThread(BufferedImage sprite,
							MouseState MS,
							KeysState KS,	
							Thread ActivatorThread,
							ExecutionCounter EC)
	{
		if(this.KS==null && this.MS ==null)
		{
			this.KS = KS;
			this.MS=  MS;
			this.EC = EC;
			this.ActivatorThread = ActivatorThread;
			this.DrawnSegments = BallPhysicsTest.DrawnSegments;

		}
		if(G==null)
		{
			G = sprite.getGraphics();
			G.setFont(new Font("Tahoma",Font.BOLD,12));
		}
		if(this.work)
		{
			 int a = 1/0;
		}
		else 
			this.work = true;
	}

	public void run()
	{
		if(Recording==null)
		{
			Recording = new char[50];
		}

		int hashTableSize = 20000;
		HashTable LINES = new HashTable(hashTableSize);
		ExecutionCounter T = new ExecutionCounter();
		int topOffset = 25;
		int widthSqueres = 0;
		int heightSqueres= 0;
		int startX = 0;
		int endX = 0;
		int startY = 0;
		int endY = 0;
		LinesGrid LG = new LinesGrid(widthSqueres,heightSqueres,endX,endY);
		while(work)
		{
				T.Start();
				V2 prevPos = null;
				boolean DrawnSegmentsChanged = false;
				Sentinal <V2[]> afterUpdate = new Sentinal<V2[]>();
				
				while(MS.buttons[0])
				{ 	
					DrawnSegmentsChanged = true;
					int Mx = MouseInfo.getPointerInfo().getLocation().x;
					int My = MouseInfo.getPointerInfo().getLocation().y-topOffset;
					if(prevPos != null && Mx!=(int)prevPos.x
										 && My!=(int)prevPos.y)
					{
						if(LINES.add(prevPos.clone(),MS.position.clone()))
						{
							LG.addLine(prevPos.clone(),new V2(Mx,My));
							DrawnSegments.AddValue(new V2[]{prevPos.clone(),
													new V2(Mx,My)});
							prevPos = new V2(Mx,My);
						}
						else 
							break;
					}
					else if(prevPos == null)
					{
						prevPos = new V2(Mx,My);
					}
					DrawValuesAndCheckedClickedChangeState();
					//drawDensityForHashTableForLines(LINES,hashTableSize);
					afterUpdate = drawLinesAndReturnUpdatedDrawableSentinal(DrawnSegments.first);
				}
				while(MS.buttons[2] && DrawnSegments.Len()>0)
				{
					drawCircle(MS.position,deleteRadius);
					Sentinal <V2[]> currentLinePoint = DrawnSegments.first;
					while(currentLinePoint != null)
					{
						if(currentLinePoint.value == null)
						{
							currentLinePoint = currentLinePoint.next;
							continue;
						}
						if(currentLinePoint.value[0].inCircle(MS.position,deleteRadius)||
						   currentLinePoint.value[1].inCircle(MS.position,deleteRadius))
						{
							if(currentLinePoint.active)
							{
								currentLinePoint.DeleteNode();
								LINES.remove(currentLinePoint.value[0],currentLinePoint.value[1]);
								LG.removeLine(currentLinePoint.value[0],currentLinePoint.value[1]);
								//LINES.remove(currentLinePoint.value[1],currentLinePoint.value[0]);
								DrawnSegmentsChanged = true;
								break;
							}
						}
						currentLinePoint = currentLinePoint.next;
					}
					//drawDensityForHashTableForLines(LINES,hashTableSize);
					afterUpdate = drawLinesAndReturnUpdatedDrawableSentinal(DrawnSegments.first);
				}
				if(DrawnSegmentsChanged)
				{	
					if(afterUpdate.first != null)
					{	
						CommandBuffer.command = 2;		
						DrawnSegments = afterUpdate.first;
						ActivatorThread.interrupt();
					}
				}
				else 
				{   G.setColor(MUC.yellow); 
					//drawLDrawableSentinal(DrawnSegments.first);
				}
				if(KS.buttons[K.LEFT]==1)
				{
					CommandBuffer.command = 0;
					ActivatorThread.interrupt();
					while(KS.buttons[K.LEFT]>=1)
					{
						Thread.yield();
					}
				}
				G.setColor(MUC.white);
				if(KS.buttons[K.ENTERKEYCODE]==1&&RecordUpdateTime(MouseRecordTime))
				{

					String s = new String(Recording);
					s = s.trim();
					try
					{
						switch (state)
						{
							case gravity :
								S.gravity.y = Float.parseFloat(s);
							break;
							case sleep :
								S.sleeptime = Integer.parseInt(s);
							break;
							case energyLoss:
								S.energyLoss = Float.parseFloat(s);
							break;
							case particlesShown:
								S.particlesShown = Integer.parseInt(s);
							break;
							case eraserSize:
								deleteRadius = Integer.parseInt(s);
							break;
							default :
							continue;
						}
						CommandBuffer.command =1;
						ActivatorThread.interrupt();
					}
					catch(NumberFormatException ex)
					{
						R.println("once");
						Recording = new char[50];
						RecordingCount = 0;
					}
					MouseRecordTime.Update();
				}
				
				for(int i = 0 ; i<KS.buttons.length ;i++)
				{
					if (i == K.ENTERKEYCODE)
						continue;
					if (KS.buttons[i]==1)
					{
						KS.buttons[i]++;
						if (i==K.BACKSPACECODE)
						{
							RecordingCount = RecordingCount==0 ? 1: RecordingCount;
							Recording[(--RecordingCount)] = (char)i;
						}
						else
						{
							if(RecordingCount <= Recording.length -1)
								Recording[(RecordingCount++)] = (char)i;
						}
					}
					else if(KS.buttons[i] > 1 && RecordUpdateTime(KeysRecordTime))
					 	KS.buttons[i] = 0;
				}
				KeysRecordTime.Update();
				//KeysRecordTime.recordTime = 0;
				String [] Values = new String[]
				{
					"text",new String(Recording),
					"SleepTime", Integer.toString(S.sleeptime),
					"MouseInfo", MS.position.toString(),
					"ExecutionCounter", Long.toString(EC.toMs(EC.lastValue)),
					"EnergyLoss", Float.toString(S.energyLoss),
					"Gravity", S.gravity.toString(),
					"EraserSize", Integer.toString(deleteRadius),
					"SentinalLength",Integer.toString(DrawnSegments.Len()),
				};
				try{Thread.sleep(100000);}
				catch(Exception e)
				{
					SetPrintParams(1000 ,50,10,12,Values);
					DrawValuesAndCheckedClickedChangeState();
				}
				long took = T.getDiffMs();
				//R.println(took);
		}
	}
	public void drawDensityForHashTableForLines(HashTable LINES , int hashTableSize)
	{
				int startX = 1300;
				int startY = 200;
				int sizeX = 100;
				int sizeY = 2;

				int rectCounter=0 ;
				int amountsPerRect = 50;	
				int linesAdded = 0;
				for (int i =0; i< hashTableSize ;i++) 
				{
					if(i%amountsPerRect==0)
					{
						if(linesAdded>0)
						{
							float t= (float)linesAdded/amountsPerRect;
							G.setColor(new Color((int)((255)*(t))|((int)(255*(1-t))<<8)));
							G.drawRect(startX, startY + rectCounter*sizeY ,sizeX, sizeY);
							G.setColor(MUC.white);
						}
						linesAdded = 0;
						rectCounter++;
					}
					if(LINES.data[i]!=null)
					{
						linesAdded++;
						//i +=  (amountsPerRect-1) - i % amountsPerRect;
					}
				}
	}
	public Sentinal<V2[]> drawLinesAndReturnUpdatedDrawableSentinal(Sentinal<V2[]> currentLinePoint)
	{
		Sentinal<V2[]> afterUpdate = new Sentinal<V2[]>();
		while(currentLinePoint != null)
		{	
			if(currentLinePoint.active&&currentLinePoint.value!=null)
			{
				afterUpdate.AddValue(currentLinePoint.value);
				G.drawLine((int)currentLinePoint.value[0].x,
						   (int)currentLinePoint.value[0].y,
						   (int)currentLinePoint.value[1].x,
						   (int)currentLinePoint.value[1].y);
			
			}
			currentLinePoint = currentLinePoint.next;
		}
		return afterUpdate;
	}
	
	public static void drawLDrawableSentinal(Sentinal<V2[]> currentLinePoint)
	{
		ExecutionCounter T = new ExecutionCounter();
		T.Start();
		while(currentLinePoint != null)
		{	
			if(currentLinePoint.active&&currentLinePoint.value!=null)
			{
				G.drawLine((int)currentLinePoint.value[0].x,
						   (int)currentLinePoint.value[0].y,
						   (int)currentLinePoint.value[1].x,
						   (int)currentLinePoint.value[1].y);
			}
			currentLinePoint = currentLinePoint.next;
		}
		if(T.getDiffMs()>0)
		{
		}
	}


	public void SetPrintParams(int x ,int y,int width,int height,String[] Values)
	{	
		startLeft = x;
		startTop = y;
		currentRowHeight = height;
		currentLetterWidth = width;
		values = Values;
	}
	public void DrawValuesAndCheckedClickedChangeState()
	{
			long currentTime = System.nanoTime();
			if(values!= null)
			{
				for(int i = 0; i<values.length;)
				{
					G.drawString(values[i],startLeft,startTop+i*currentRowHeight);

					if(isHot(i))
					{
						G.setColor(MUC.magenta);
						if(MS.buttons[0])
						{
							//change current state 
							switch(i)
							{
								case 2:
									state = State.sleep ;
									break;
								case 8:
									state = State.energyLoss;
									break;
								case 10:
									state = State.gravity;
									break;
								case 12:
							 		state = State.eraserSize;
							}
						}
					}
						G.drawString(values[i+1],
										startLeft+G.getFontMetrics().stringWidth(values[i]),
										startTop+i*currentRowHeight);
	
						G.setColor(MUC.white);
					i+=2;
				}
			}
	}
	public boolean isHot(int i)
	{
		if (values!=null)
		{
			int startX = startLeft+G.getFontMetrics().stringWidth(values[i]);
			int startY = startTop+i*currentRowHeight;
			int width = G.getFontMetrics().stringWidth(values[i+1]);
			return R.in(MS.position.x,startX,startX+width)&&
				   R.in(MS.position.y,startY,startY-currentRowHeight);
		}
		return false;
	}
	public void drawCircle(V2 P,int rad)
	{
		G.drawOval((int)P.x-rad, (int)P.y-rad, (int)rad*2, (int)rad*2);
	}

}
