class M3x3
{
public float [][] rows;
public float [][] cols;
public int dim;
public M3x3(){rows =new float[3][3];cols =new float[3][3]; dim = 3;}

public M3x3(float[][] rows,float[][] cols)
{
	assert(rows.length ==3 && cols.length==3);
	this.rows =rows;
	this.cols =cols;
	this.dim =3;	
}
public M3x3(float[][] rows)
{
	assert(rows.length ==3 && rows[0].length==3 );
	
	this.rows = rows;
	initCols();
	this.dim =3;
}
private void initCols()
{	
	this.cols = new float[3][3];
	for(int i =0 ; i<3;i++)
		for(int j=0; j<3;j++)
			cols[i][j]=rows[j][i];

}
public static M3x3 mul(M3x3 a,M3x3 b)
{
	M3x3 ret = new M3x3();
	ret.rows[0][0] = a.rows[0][0]*b.cols[0][0] +a.rows[0][1]*b.cols[0][1]+a.rows[0][2]*b.cols[0][2];
	ret.rows[0][1] = a.rows[0][0]*b.cols[1][0] +a.rows[0][1]*b.cols[1][1]+a.rows[0][2]*b.cols[1][2];
	ret.rows[0][2] = a.rows[0][0]*b.cols[2][0] +a.rows[0][1]*b.cols[2][1]+a.rows[0][2]*b.cols[2][2];
	
	ret.rows[1][0] = a.rows[1][0]*b.cols[0][0] +a.rows[1][1]*b.cols[0][1]+a.rows[1][2]*b.cols[0][2];
	ret.rows[1][1] = a.rows[1][0]*b.cols[1][0] +a.rows[1][1]*b.cols[1][1]+a.rows[1][2]*b.cols[1][2];
	ret.rows[1][2] = a.rows[1][0]*b.cols[2][0] +a.rows[1][1]*b.cols[2][1]+a.rows[1][2]*b.cols[2][2];
	
	ret.rows[2][0] = a.rows[2][0]*b.cols[0][0] +a.rows[2][1]*b.cols[0][1]+a.rows[2][2]*b.cols[0][2];
	ret.rows[2][1] = a.rows[2][0]*b.cols[1][0] +a.rows[2][1]*b.cols[1][1]+a.rows[2][2]*b.cols[1][2];
	ret.rows[2][2] = a.rows[2][0]*b.cols[2][0] +a.rows[2][1]*b.cols[2][1]+a.rows[2][2]*b.cols[2][2];
	
	ret.initCols();
	return ret;
}

public M3x3 inverse()
{
	float d = det();
	return new M3x3(new float [][]
	{
		{(cols[1][1]*cols[2][2]-cols[1][2]*cols[2][1])/d,-(cols[1][0]*cols[2][2]-cols[1][2]*cols[2][0])/d,(cols[1][0]*cols[2][1]-cols[1][1]*cols[2][0])/d},
		{-(cols[0][1]*cols[2][2]-cols[0][2]*cols[2][1])/d,(cols[0][0]*cols[2][2]-cols[0][2]*cols[2][0])/d,-(cols[0][0]*cols[2][1]-cols[0][1]*cols[2][0])/d},
		{(cols[0][1]*cols[1][2]-cols[0][2]*cols[1][1])/d,-(cols[0][0]*cols[1][2]-cols[0][2]*cols[1][0])/d,(cols[0][0]*cols[1][1]-cols[0][1]*cols[1][0])/d}
	}
	);
}
public float det()
{	
	return rows[0][0]*(rows[1][1]*rows[2][2]-rows[2][1]*rows[1][2])+
		   rows[1][0]*(rows[0][0]*rows[0][2]-rows[2][0]*rows[2][2])+
		   rows[2][0]*(rows[0][1]*rows[1][2]-rows[1][1]*rows[0][2]);
}

public V3 mul(V3 a)
{	//this*a 
	return new V3(a.x*rows[0][0] +a.y*rows[0][1] +a.z*rows[0][2],
				  a.x*rows[1][0] +a.y*rows[1][1] +a.z*rows[1][2],
				  a.x*rows[2][0] +a.y*rows[2][1] +a.z*rows[2][2]
				 );
	
}
public void print()
{
	for(int i =0 ; i<dim;i++)
	{
		for(int j=0; j<dim;j++)
		{	
			if(j!=0 && j!=dim)
				R.print(",");
			R.print(rows[i][j]);
		}
		R.ln();
		
			
	}
	
}

}