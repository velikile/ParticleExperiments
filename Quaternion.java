class Quaternion 
{
	public float a, i, j, k;
	public Quaternion(){ a=0;i=0;j=0;k=0;};
	public Quaternion(float a,V3 vec)
	{
		//assert vec.len()<=1.0f;
		this.a = a;
		i = vec.x;
		j = vec.y;
		k = vec.z;
	}
	public Quaternion(float a,V3 vec,boolean convertFlag)
	{
		//assert vec.len()<=1.0f;
		this.a = a;
		i = vec.x;
		j = vec.y;
		k = vec.z;
		if(convertFlag)
			convert();
	}
	public Quaternion(float a,float i,float j,float k)
	{
		//assert will pass when the statement is true
		//assert new V3(i,j,k).len()<=1f; //only accept unit vectors
		this.a = a;
		this.i = i;
		this.j = j;
		this.k = k;
	}
	public Quaternion(V3 vec)
	{
		//assert vec.len()<=1.0f;
		a = 0;
		i = vec.x;
		j = vec.y;
		k = vec.z;
	}
	public Quaternion(float a,float i,float j,float k,boolean convertFlag)
	{
		this.a = a;
		this.i = j;
		this.j = j;
		this.k = k;
		if(convertFlag)
			convert();
	}
	public void convert()
	{
		i *= (float)Math.sin((double)a/2);
		j *= (float)Math.sin((double)a/2);
		k *= (float)Math.sin((double)a/2);
		a = (float)Math.cos((double)a/2);
	}
	
	public float len(){return (float)Math.sqrt(a*a+getAxis().len());}
	
	public V3 getAxis()
	{
		return new V3(i,j,k);
	}
	public Quaternion conjugate()
	{
		return new Quaternion(a,-i,-j,-k);
	}
	public Quaternion inverse()
	{
		float l = len();
		Quaternion ret = conjugate();
		ret.sMul(1f/(l*l));
		return ret;
	}
	public Quaternion mul(Quaternion a)
	{
		return Quaternion.mul2(this,a);
	}
	
	public static Quaternion mul(Quaternion a , Quaternion b)
	{
		Quaternion ret = new Quaternion();
		ret.a = a.a*b.a - a.i*b.i - a.j*b.j - a.k*b.k;
		ret.i = a.a*b.i + a.i*b.a + a.j*b.k - a.k*b.j;
		ret.j = a.a*b.j + a.j*b.a - a.i*b.k + a.k*b.i;
		ret.k = a.a*b.k + a.k*b.a + a.i*b.j - a.j*b.i;
		return ret; 
	}
	public Quaternion clone()
	{
		return new Quaternion(a,i,j,k);
		
	}
	public static Quaternion mul2(Quaternion a, Quaternion b) 
	{
		V3 v0 = a.getAxis();

		V3 v1 = b.getAxis();
		return new Quaternion(a.a*b.a - V3.dot(v0,v1),
						      V3.add(V3.cross(v0,v1),V3.add(V3.sMul(a.a,v1),V3.sMul(b.a,v0))));
	}
	public void sMul(float x){a*=x;i*=x;j*=x;k*=x;}
	
	public void print()
	{
		R.print("{"+a+","+i+","+j+","+k+"}");
	}

}