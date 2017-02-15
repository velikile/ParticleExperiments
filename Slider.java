
public class Slider {
 
	int height;//height of the sliding element
	V2 start;
	V2 end;
	float minval;
	float maxval;
	public float Tvalue; //shared value,
	public Slider(int height ,Float value,float min,float max,V2 start,V2 end)
	{
		this.height = height;
		this.Tvalue = value;
		this.start = start;
		this.end = end;
		this.minval=min;
		this.maxval=max;
	}
	public float getFValue()
	{//The pixel is in the value need to be tested before this method is called
			return minval+Tvalue*(maxval-minval);
	}
	public void update(int pixel)
	{//The pixel is in the value need to be tested before this method is called
			Tvalue = 1f-(float)(end.x-pixel)/(end.x-start.x);
	}
	public void draw(){
		Render3d.drawSlider(start, end, Tvalue,height);
	}
	
	public boolean isPinBound(int X, int Y)
	{//The pixel is in the value need to be tested before this method is called
			return R.in(X,start.x,end.x)&& R.in(Y, start.y, start.y+height);
	}
}  
