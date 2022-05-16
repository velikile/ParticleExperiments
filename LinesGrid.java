public class LinesGrid
{

	Sentinal[][] LinesData = null;
	int width;
	int height;
	int maxX;
	int maxY;
	public LinesGrid(int width,int height,int maxX,int maxY)			  
	{
		LinesData = new Sentinal[width][height];
		this.width = width;
		this.height = height;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	public void addLine(V2 A,V2 B)
	{
		int AX =(int)((A.x/maxX)*width);
		int BX =(int)((B.x/maxX)*width);
		int AY =(int)((A.y/maxY)*height);
		int BY =(int)((B.y/maxY)*height);
                if(AX >= this.width || BX >= this.width || AY >= this.height || BY >= this.height)
                    return;
		if(LinesData[AX][AY] == null)
			LinesData[AX][AY] = new Sentinal<V2>();
		LinesData[AX][AY].AddValue(new V2[]{A,B});
		if (AX!=BX ||AY!=BY)
		{
			if (LinesData[BX][BY] == null)
				LinesData[BX][BY] = new Sentinal<V2>();
			LinesData[BX][BY].AddValue(new V2[]{A,B});
		}
	}
	public void removeLine(V2 A,V2 B)
	{
		int AX =(int)((A.x/maxX)*width);
		int BX =(int)((B.x/maxX)*width);
		int AY =(int)((A.y/maxY)*height);
		int BY =(int)((B.y/maxY)*height);

		Sentinal <V2[]> temp = LinesData[AX][AY].first;
		Sentinal prev = LinesData[AX][AY].first;
		while (temp!=null)
		{
			if(temp.value[0] != null && temp.value[1]!=null)
			{
				if(temp.value[0].x == A.x && A.y == temp.value[0].y
				 &&temp.value[1].x == B.x && B.y == temp.value[1].y)
					temp = temp.DeleteNode(prev);
			}
			prev=  temp;
			if(temp!=null)
				temp = temp.next;
		}
		if(prev!=null)
			LinesData[AX][AY].first = prev.first;
		else 
			LinesData[AX][AY] = null;
	}
}
