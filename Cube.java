class Cube
{
	public V3 q,w,a,s;//close
	public V3 e,r,d,f;//far
	public V3 center;
	float width;
	float height;
	float depth;
	public Cube(Cube c)
	{
		w=c.w.clone();
		q=c.q.clone();
		a=c.a.clone();
		s=c.s.clone();
		e=c.e.clone();
		r=c.r.clone();
		d=c.d.clone();
		f=c.f.clone();
		center = c.center.clone();
		height = c.height;
		width = c.width;
		depth = c.depth;
	}
	public Cube clone(){return new Cube(this);}
	public Cube(V3 center ,float width, float height, float depth)
	{
		this.center = center;
		this.width= width;
		this.height= height;
		this.depth = depth;
		q = new V3(center.x - width/2, center.y - height/2,center.z-depth/2);
		w = new V3(center.x + width/2, center.y - height/2,center.z-depth/2);
		a = new V3(center.x - width/2, center.y + height/2,center.z-depth/2);
		s = new V3(center.x + width/2, center.y + height/2,center.z-depth/2);

		e = new V3(center.x - width/2, center.y - height/2,center.z+depth/2);
		r = new V3(center.x + width/2, center.y - height/2,center.z+depth/2);
		d = new V3(center.x - width/2, center.y + height/2,center.z+depth/2);
		f = new V3(center.x + width/2, center.y + height/2,center.z+depth/2);
	}
	public void rotate(float angle)
	{
		angle = (float)Math.toRadians(angle);
		Quaternion rot = new Quaternion(angle,(new V3(0f,1f,0)));	
		rot.convert();
		center.rotate(rot);
		q.rotate(rot);
		w.rotate(rot);
		a.rotate(rot);
		s.rotate(rot);
		e.rotate(rot);
		r.rotate(rot);
		d.rotate(rot);
		f.rotate(rot);
	}

	public void rotate(Quaternion rot)
	{
		center.rotate(rot);
		q.rotate(rot);
		w.rotate(rot);
		a.rotate(rot);
		s.rotate(rot);
		e.rotate(rot);
		r.rotate(rot);
		d.rotate(rot);
		f.rotate(rot);
	}
	public void translate(V3 di)
	{
		center.add(di);
		q.add(di);
		w.add(di);
		a.add(di);
		s.add(di);
		e.add(di);
		r.add(di);
		d.add(di);
		f.add(di);

	}

	public void draw(Camera c ){

		Rectangle[] faces = getFaces();
		for (Rectangle r:faces)
			Render3d.draw3dRect(r.q.clone(), r.w.clone(),r.s.clone(), r.a.clone());

	}
	public Rectangle[] getFaces()
	{
		Rectangle[] ret = new Rectangle[6];
		ret[0] =new Rectangle();
		ret[1] =new Rectangle();
		ret[2] =new Rectangle();
		ret[3] =new Rectangle();
		ret[4] =new Rectangle();
		ret[5] =new Rectangle();

		ret[0].q=q;ret[0].w=w;ret[0].s=s;ret[0].a=a;
		ret[1].q=e;ret[1].w=r;ret[1].s=f;ret[1].a=d;	
		ret[2].q=e;ret[2].w=q;ret[2].s=a;ret[2].a=d;
		ret[3].q=r;ret[3].w=w;ret[3].s=s;ret[3].a=f;
		ret[4].q=d;ret[4].w=f;ret[4].s=s;ret[4].a=a;
		ret[5].q=e;ret[5].w=r;ret[5].s=w;ret[5].a=q;
		
		return ret;
	}
	
	public Rectangle[] getRectTopFaces(Camera c)
	{
		Rectangle[] ret = getFaces();
		
		float []  sizes = new float [6];
		for(int i =0  ; i<sizes.length;i++)
		{
			sizes[i] = V3.sub(c.position, ret[i].q).len() +V3.sub(c.position, ret[i].w).len()  +V3.sub(c.position, ret[i].s).len()+V3.sub(c.position, ret[i].a).len();	
		}
		
		float min0=sizes[0];
		float min1=sizes[0];
		float min2=sizes[0];
		int [] indexes = new int[3];
		for (int i = 0; i < sizes.length; i++) 
		{
			if (sizes[i]<min0){min0=sizes[i]; indexes[0] =i;}
			
		}
		for (int i = 0; i < sizes.length; i++) 
		{
			if (sizes[i]>min0&&sizes[i]<min1){min1=sizes[i]; indexes[1] =i;}
			
		}
		for (int i = 0; i < sizes.length; i++) 
		{
			if (sizes[i]>min0&&sizes[i]>min1&&sizes[i]<min2){min2=sizes[i]; indexes[2] =i;}
		}
		System.out.println(sizes[indexes[0]]+","+sizes[indexes[1]]+","+sizes[indexes[2]]);
	
		return new Rectangle[] {ret[indexes[0]],ret[indexes[1]],ret[indexes[2]]};
		
	}
	public Cube toPixel(float w,float h)
	{
		Cube c = new Cube(this);
		c.q.x*=w;c.q.y*=h;
		c.w.x*=w;c.w.y*=h;
		c.a.x*=w;c.a.y*=h;
		c.s.x*=w;c.s.y*=h;
		c.e.x*=w;c.e.y*=h;
		c.d.x*=w;c.d.y*=h;
		c.r.x*=w;c.r.y*=h;
		c.f.x*=w;c.f.y*=h;

		return c;
	}
	public boolean CubeTouches(Cube subject)
	{
		return V3.in(center.x,subject.center.x-subject.width/2,subject.center.x+subject.width/2)&&
				V3.in(center.y,subject.center.y-subject.height/2,subject.center.y+subject.height/2)&&
				V3.in(center.z,subject.center.z-subject.depth/2,subject.center.z+depth/2);

	}
	public void print()
	{
		System.out.print("(");q.print();System.out.print(")(");w.print();System.out.println(")");
		System.out.print("(");a.print();System.out.print(")(");s.print();System.out.println(")");
		System.out.print("(");e.print();System.out.print(")(");r.print();System.out.println(")");
		System.out.print("(");d.print();System.out.print(")(");f.print();System.out.println(")");
	}

	// stack a matrix 


}