import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.image.DataBufferInt;
import java.awt.BasicStroke;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.awt.*;

public class BallPhysicsTest extends JFrame
{
	public static int ENTERKEYCODE = 10;
	public static int ONEKEYCODE  =49;
	public static int TWOKEYCODE  =50;
	public static int THREEKEYCODE  =51;
	public static int[] imagePixelData;
	public static int [] drawData;
	public static int height = 1050;
	public static int width  = 1680;


	public static int maxHeight = height-200;
	public static int maxWidth = width -1000;
	public static int ParticlesCount = 2000;
	public static VolatileImage fastersprite = null;
	public static BufferedImage sprite = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); 
	public static BufferedImage canvas = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); 
	public static volatile MouseState MouseInfo= new MouseState(3);
	public static volatile KeysState KeysInfo= new KeysState(256);
	public static Graphics G;
	public static SP panel = null;
	public static float epsilon = 0.1f;
	public static V3 colLeft = new V3(-5,-2,0);
	public static volatile Sentinal<V2[]> DrawnSegments= new Sentinal<V2[]>();
	public static SleepTime SLEEPTIME = new SleepTime();
	public static ExecutionCounter ExecCounter = new ExecutionCounter();

	public BallPhysicsTest()
	{
		DrawnSegments.last = DrawnSegments;
		DrawnSegments.first = DrawnSegments;
		GraphicsConfiguration gc = getGraphicsConfiguration();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIgnoreRepaint( true );
		setSize(width,height);
		setTitle("Java Swing - JPanel Draw Filled Circle with Random Colors");
		imagePixelData = ((DataBufferInt)sprite.getRaster().getDataBuffer()).getData();
		drawData = ((DataBufferInt)canvas.getRaster().getDataBuffer()).getData();
		//fastersprite = gc.createCompatibleVolatileImage(getWidth(), getHeight()); 
		Container container = getContentPane();
		panel = new SP(sprite);
		
		
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		addKeyListener(KeysInfo);
		panel.addMouseListener(MouseInfo);
		panel.addMouseMotionListener(MouseInfo);
		container.add(panel);
		setVisible(true);
		fastersprite= panel.createVolatileImage(width,height);
		fastersprite.setAccelerationPriority(1.0f);
		initGraphics();

	}
	public static void initGraphics(){
		if(G==null)
		{
			G = fastersprite.createGraphics();
			((Graphics2D)G).setBackground(Color.BLACK);
		}

	}
	public static void flipAcrossX(V3 dir)
	{
		dir.sMul(-1f);
		dir.x *= -1;
	}
	public static void flipAcrossY(V3 dir)
	{
		dir.sMul(-1f);
		dir.y *= -1;
	}	
	public static void FillDirectionAndPositionWithRandom(V3[]positions,
														  V3[]directions,
														  int size,
														  int maxHeight)
	{
		Random r = new Random();
		if(positions == null || directions == null)
			return; 
		for(int i = 0;i<size;i++)
		{
			float posY = maxHeight * Math.abs(r.nextFloat());
			float dirX = r.nextFloat();
			if(dirX < 0)
				dirX*=-1;
			positions[i] = new V3(0,posY,0);			
			directions[i] = new V3(dirX,0,0);
		}
	}

	public static void ParticlesQsort(V3[]p,V3[]d,int s,int f)
	{
		if(s>=f||s<0||f>=p.length)
			return;
		int q=ByYPartition(p,d,s, f);
		ParticlesQsort(p,d,s,q-1);
		ParticlesQsort(p,d,q+1,f);
	}
	public static int ByYPartition(V3[]p,V3[]d,int start, int finish)
	{
		float t = p[start].y;
		int i=start;
		for (int j = finish; i<j;)
		{
			float iValue =p[i].y;
			float jValue =p[j].y;
			if (iValue<t)
				i++;
			if(jValue>t)
				j--;
			if(iValue>jValue)
			{	
				R.swap(p,i,j);
				R.swap(d,i,j);
			}
			if (iValue==jValue&&j!=i)
				i++;
		}
		return i;
	}
	public static int BinarySearch(int[] arr, int val,int start, int end)
	{
		if(arr[start]>val || arr[end]<val)
			return -1;
		if(end-start == 1 &&arr[end]>val&&arr[start]<val)
			return -1;
		int n = end + start;
		
		if(arr[n/2] == val)
			return n/2;
		else if (arr[n/2] > val)
			return BinarySearch(arr,val,start,n/2-1);
		else
			return BinarySearch(arr,val,n/2+1,end);
	}
	public static int BinarySearch(float[] arr, float val,int start, int end,float epsilon)
	{
		if(arr[start]>val || arr[end]<val)
			return -1;
		if(end-start == 1 &&arr[end]>val&&arr[start]<val)
			return -1;
		int n = end + start;
		
		if(R.eq(arr[n/2],val,epsilon))
			return n/2;
		else if (arr[n/2] > val)
			return BinarySearch(arr,val,start,n/2-1,epsilon);
		else
			return BinarySearch(arr,val,n/2+1,end,epsilon);
	}
	public static Range GetRangeByLowestHighestYValues(V3[] p,float low,float high,int start ,int end,float epsilon)
	{
		if (p==null || end<start || start<0 || end>=p.length)
			return null;
		else 
		{
			int Flow = (int)BinarySearchOnYEqualOrClosest(p,low,start,end,epsilon),
	 		Fhigh = (int)BinarySearchOnYEqualOrClosest(p,high,start,end,epsilon);
		if(Fhigh>=Flow)
			return new Range(Flow,Fhigh);
		else return null;

		}
	}
	public static int IterationCounter =0;
	public static float BinarySearchOnYEqualOrClosest(V3 [] p,float val,int start,int end,float epsilon)
	{
		if(start>end){return end+1;}
		if(end == start)
		{
			if(R.eq(p[end].y,val,epsilon))
				return end;
			else 
			{
				if(start+1<p.length&&p[start].y<val && p[start+1].y>val)
					return (float)start;
				if(start-1>=0&&p[start].y>val && p[start-1].y<val)
					return (float)start;
				else
				{
					return -1;
				}
			}
		}
		if(end-start == 1 &&
		   p[end].y>val&&
		   p[start].y<val)
		{
			return (float)start;
		}
		int n = end + start;
		
		if(R.eq(p[n/2].y,val,epsilon))
			return (float)n/2;
		else if (p[n/2].y > val)
			return BinarySearchOnYEqualOrClosest(p,val,start,n/2-1,epsilon);
		else
			return BinarySearchOnYEqualOrClosest(p,val,n/2+1,end,epsilon);
	}

	public static  float BinarySearchOnEqualOrClosest(float [] p,float val,int start,int end,float epsilon)
	{
		if(end == start)
		{
			if(R.eq(p[end],val,epsilon))
				return end;
			else 
			{
				if(start+1<p.length&&p[start]<val && p[start+1]>val)
					return (2f*start+1)/2f;
				if(start-1>=0&&p[start]>val && p[start-1]<val)
					return (2f*start-1)/2f;
				else
					return -1;
			}
		}
		if(end-start == 1 &&
		   p[end]>val&&
		   p[start]<val)
			return (float)(end+start)/2;

		int n = end + start;
		
		if(R.eq(p[n/2],val,epsilon))
			return (float)n/2;
		else if (p[n/2] > val)
			return BinarySearchOnEqualOrClosest(p,val,start,n/2-1,epsilon);
		else
			return BinarySearchOnEqualOrClosest(p,val,n/2+1,end,epsilon);
	}
	public static void DeflectParticle(V3 positionA,V3 positionB,V3 direction,float energyLoss)
	{
		V3 d0 =  V3.sub(positionA,positionB);
		d0.normalize();
		V3 d1 = d0.l90();
		float l2 = direction.dot(d1);
		if(R.in(l2,-epsilon,epsilon))
		{
			return;
		}
		d1.sMul(l2);
		direction.sub(V3.sMul(2,d1));
		direction.sMul(-1f);
	}
	public static boolean PositionInRange(V3 a ,V3 c,float radius)
	{
		V3 res = V3.sub(a,c);
		return res.x2()+res.y2() <= R.sq(radius);
	}
	public static void drawCircle(V2 P,int rad)
	{
		G.drawOval((int)P.x-rad, (int)P.y-rad, (int)rad*2, (int)rad*2);
	}
	public static boolean EnterIsPressed()
	{
		return KeysInfo.buttons[ENTERKEYCODE]==1;
	}

	public static void SavePosition()
	{
		if(R.in(MouseInfo.position.y,0,height)&& R.in(MouseInfo.position.x,0,width))
			drawData[(int)MouseInfo.position.y*width+ (int)MouseInfo.position.x]=-1;
	}
	public static V2[] getArcPoints(V2 A,V2 B,V2 C, int numberofpoints)
	{
		float increment = 1f/numberofpoints;


		V2 [] retVal = new V2[numberofpoints+1];
		//A-2B+C
		V2 T2 = V2.add(V2.sub(A,V2.sMul(2,B)),C);
		//2B - 2C
		V2 T = V2.sub(V2.sMul(2f,B),V2.sMul(2,C));
		// C
		int counter = 0;
		for(float i=0;counter<=numberofpoints;i+=increment)
		{	
			retVal[counter++] = V2.add(V2.add(V2.sMul(i*(i),T2),V2.sMul((i),T)),C);
		}
		return retVal;
	}
	
	public static V3 gravity = new V3(0f,1f,0f);

	public static class ArcRenderVersion implements Runnable
	{
		public void run()
		{
			while(true)
			{
				Sentinal <V2[]> currentLinePoints = DrawnSegments.first;
				Sentinal <V2[]> prevLinePoints = null;
				while(currentLinePoints != null)
				{
					if(currentLinePoints.value == null)
					{
						currentLinePoints = currentLinePoints.next;
						continue;
					}					
					else if(prevLinePoints!=null)
					{
						V2[] prevPoints = prevLinePoints.value;
						V2[] currentPoints = currentLinePoints.value;
						if(currentPoints!=null && prevPoints!=null)
						{
							//R.println(currentPoints[0]+"-"+prevPoints[1]);
							if(currentPoints[0].eq(prevPoints[1],10))
							{
								V2[]ArcPoints = getArcPoints(prevPoints[0],prevPoints[1],currentPoints[0],4);
								for (int i = 0;  i<ArcPoints.length-1;i++)
								{
									V2 A = ArcPoints[i++];
									V2 B = ArcPoints[i];
									synchronized(G)
									{
										G.drawLine((int)A.x,(int)A.y,(int)B.x,(int)B.y);
									}
								}
							}
							else 
							{
								V2 A = prevPoints[0];
								V2 B = prevPoints[1];
								V2 C = currentPoints[0];
								V2 D = currentPoints[1];
								synchronized(G)
									{
										G.drawLine((int)A.x,(int)A.y,(int)B.x,(int)B.y);
										G.drawLine((int)C.x,(int)C.y,(int)D.x,(int)D.y);
									}
							}
						}
					}

					prevLinePoints = currentLinePoints;
					currentLinePoints = currentLinePoints.next;
					
				}

			}
		}

	}
	public static void main(String argv[])
	{
		System.setProperty("java.awt.headless", "false");
		//int [] binarySearchTest = new int[]{2,4,5,6,7,10,12,13,20,21,25,27,33,34};
		float [] binarySearchTest = new float[]{2f,4f,5f,6f,7f,10f,12f,13f,20f,21f,25f,27f,33f,34f};
		R.println(BinarySearchOnEqualOrClosest(binarySearchTest,8f,0,binarySearchTest.length-1,epsilon));
		
		SLEEPTIME.sleeptime = 14;
		int maxCountForSelectedParticles = 2;
		V3[]directions = new V3[ParticlesCount];
		V3[]positions = new V3[ParticlesCount];

		//new Thread(new ArcRenderVersion()).start();
		FillDirectionAndPositionWithRandom(positions,directions,ParticlesCount,maxHeight);
		
		boolean run = true;
		int UpdateSleepState =1,
			DefaultState=0;
		int CurrentState = 0;

		
		V3 colLeft = new V3(-5,-2,0);
		BallPhysicsTest b = new BallPhysicsTest();
		int counter =0 ;
		char [] Recording = null;
		int RecordingCount = 0;
		byte xPartition = 1;
		byte sign = 1;
		int startX =0;
		int startY =0;
		Color lineColor = MUC.white;
		EnergyLoss energyLoss = new EnergyLoss();
		energyLoss.energyloss  = 1f/1.2f;
		int col = 0; 
		Random ran = new Random();
		int [] particleRefreshCounter = new int[ParticlesCount];
		G.setFont(new Font("Tahoma",Font.BOLD,12));
		// UI info 
		float radius = 50f;
		InfoRenderThread INFOPRINTTHREAD  = new InfoRenderThread(sprite,
																 MouseInfo,
																 KeysInfo,
																 SLEEPTIME,
															 Thread.currentThread()
																 ,ExecCounter,
																 energyLoss,
																 gravity);
		Thread InfoRenderer =  new Thread(INFOPRINTTHREAD);
		InfoRenderer.start();
		Thread ColDetectionDeflection = new Thread(new LineCollisionTesterThread(Thread.currentThread(),
																				 DrawnSegments,
																				 positions,
																				 directions,
																				 energyLoss));
		ColDetectionDeflection.start();

		Sentinal<V2[]> afterUpdate=new Sentinal<V2[]>();
		V2 A = new V2(400,400),
		   B = new V2(500,600),
		   C = new V2(300,500),
		   D = new V2(200,200);
		int arcDrawPoint = 0;

		int[][] densityGrid = new int[20][20];


		//__________________MAIN LOOP__________________
		//go through particles
		//deflect add gravity 


		while(run)
		{	
			ExecCounter.Start();
			synchronized(G)
			{
			 	G.setColor(Color.BLACK);
			 	G.clearRect(0,0,width,height);
			 }
			//G.drawLine((int)A.x,(int)A.y,(int)C.x,(int)C.y);
			// G.drawLine((int)B.x,(int)B.y,(int)D.x,(int)D.y);

			// V2[] points = getArcPoints(A,C,B,15);
			// for (int i = 0; i<points.length-1;i++)
			// {
			// 	G.drawLine((int)points[i].x,
			// 			   (int)points[i].y,
			// 			   (int)points[i+1].x,
			// 			   (int)points[i+1].y);
			// }


			if(arcDrawPoint==0&&MouseInfo.buttons[1] )
			{
				A.x = MouseInfo.position.x;
				A.y =MouseInfo.position.y;
			}
			else if(arcDrawPoint==1&&MouseInfo.buttons[1])
			{
				B.x = MouseInfo.position.x;
				B.y =MouseInfo.position.y;
			}
			else if(arcDrawPoint==2&&MouseInfo.buttons[1])
			{
				C.x = MouseInfo.position.x;
				C.y =MouseInfo.position.y;
			}
			if(KeysInfo.buttons[ONEKEYCODE]==1)
					arcDrawPoint=1;
			if(KeysInfo.buttons[TWOKEYCODE]==1)
					arcDrawPoint=2;
			if(KeysInfo.buttons[THREEKEYCODE]==1)
					arcDrawPoint=0;
			if(CurrentState == DefaultState)
			{
				ParticlesQsort(positions,directions,0,ParticlesCount-1);
				
				int currentIndex = ((ran.nextInt() % ParticlesCount )+ ParticlesCount)/2;
				particleRefreshCounter[currentIndex]++;

				drawDensityGrid(densityGrid);
				clearDensityGrid(densityGrid);

				G.setColor(MUC.white);
				for(int i = 0; i<positions.length ; i++)
				{
					if(positions[i] == null)
						continue;


					V3 position = positions[i];
					V3 direction = directions[i];
					FillDensityGrid(densityGrid,position,direction);
					boolean positionInCircle = PositionInRange(position,MouseInfo.position.toV3(),radius);
					boolean positionDirectionInCircle =
					PositionInRange(V3.add(position,direction),
									MouseInfo.position.toV3(),radius);

					if(positionInCircle&&
					   positionDirectionInCircle)
					{
						direction.sMul(-1f);
						V3 centerFromPointVector = V3.sub(position,MouseInfo.position);
						float length = centerFromPointVector.x2() + centerFromPointVector.y2();
						float ladd = R.sq(radius) - length ;
						position.add(V3.sMul(ladd/length,centerFromPointVector));
					}
				    else if(positionInCircle||
					   positionDirectionInCircle)
						DeflectParticle(position,MouseInfo.position.toV3(),direction,1f);

					if(position.y+direction.y >= maxHeight)
					{
						// lineColor=new Color(new Random().nextInt());
						flipAcrossX(direction);
						position.y = maxHeight;
						direction.sMul(energyLoss.energyloss);
					}
					if (position.y+direction.y < 0)
					{
						// lineColor=new Color(new Random().nextInt());
						position.y = 0;
						flipAcrossX(direction);
					}
					if (position.x+direction.x < 0)
					{
						// lineColor=new Color(new Random().nextInt());
						position.x= 0;
						flipAcrossY(direction);
					}
					if (position.x+direction.x > maxWidth)
					{
						position.x = maxWidth;
						flipAcrossY(direction);
					}

					if(direction.x2()+direction.y2()>1000)
						direction.sMul(1f/2);
					

				 	G.drawLine((int)position.x,(int)position.y,
				 			   (int)position.x+(int)direction.x,
				 			   (int)position.y+(int)direction.y);
					//G.fillOval((int)20, (int)20, 50,50);
					//G.setColor(lineColor);
					position.add(direction);
					{
						direction.add(gravity);
					}
					 if(direction.x<=epsilon&&direction.x>-epsilon)
					 	direction.x = 0f;
					 if(direction.y<=epsilon&&direction.y>-epsilon)
					  	direction.y = 0f; 
					if(direction.x == 0 && direction.y == 0)
					{
						position.z = 0;
					}
				}
				if (particleRefreshCounter[currentIndex]==maxCountForSelectedParticles){
					particleRefreshCounter[currentIndex] = 0;
					float posY = (maxHeight) * Math.abs(ran.nextFloat());
					float dirX = 20*ran.nextFloat();
					if(dirX < 0)
						dirX*=-1;
					positions[currentIndex] =new V3(0,posY,0);
					directions[currentIndex] = new V3(dirX,0,0);
				}
				drawCircle(MouseInfo.position,(int)radius);
			}
			try
			{
				((Graphics2D)G).setStroke(new BasicStroke(2));
				b.repaint();
				G.setColor(new Color(-1));
				
				Thread.sleep(SLEEPTIME.sleeptime);
				ExecCounter.getDiffNano();
			}
			catch(InterruptedException e)
			{
				R.println("main interrupted");
				DrawnSegments = InfoRenderThread.DrawnSegments;
			}
		}
	}
	public static void drawDensityGrid(int[][]densityGrid)
	{
		int rows = densityGrid.length;
		int cols = densityGrid[0].length;
		float heightPerRow = maxHeight/rows; 
		float widthPerCol =  maxWidth/cols;
		for(int j = 0; j < cols ; j++)
		{
			for (int i = 0;i<rows;i++)
			{			
				float t  = (float)(densityGrid[i][j])/ParticlesCount;
				// lerp maxValue * t + minValue *(1-t)
				if(t>0.01)
				{
					//make the difference more visible 
					//adjust the the color value such that that  
					//understanding the model of your data is more important 
					//than the algorithm used in the big o notation 
					G.setColor(new Color((int)((255*t))<<8|(int)(128*(1-t))));
					G.fillRect((int)(i*widthPerCol),(int)(j*heightPerRow),(int)widthPerCol,(int)heightPerRow);
				}
			}
		}
	}
	public static void clearDensityGrid(int [][]densityGrid)
	{
		for (int i =0;i<densityGrid.length ;i++ ) {
			for (int j =0; j<densityGrid[0].length; j++) 
			{
				densityGrid[i][j]= 0;	
			}
		}
	}

	public static void FillDensityGrid(int[][]densityGrid,V3 position,V3 direction)
	{
		int rows = densityGrid.length;
		int cols = densityGrid[0].length;
		if (position.x>=0&&position.y>=0)
		{
			int Y = (int)(position.y*(rows))/maxHeight;
			int X = (int)(position.x*(cols))/maxWidth;
			Y  = Y == rows ? rows-1: Y;
			X  = X == cols ? cols-1: X;
			//R.println(X +"," + Y);

			densityGrid[X][Y]++;
			float t  = (float)(densityGrid[X][Y])/ParticlesCount;
			if(t >= 0.1)
			{
				direction.sub(gravity);

				direction.add(colLeft);
			}

		}
	}
}
