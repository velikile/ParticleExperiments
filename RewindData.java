public class RewindData
{
	//store 1000 frames and let me rewind back all the way back 
	public int FrameCount  =0;
	public int StoredFrameCount = 0; // the number maximum frames stored approx 60fps
	public int ParticlesCount = 0; // the number maximum frames stored
	public int timePerRecord= 10; // samples per second
	public V3[][]positions=new V3[ParticlesCount][StoredFrameCount];
	public V3[][]directions = new V3[ParticlesCount][StoredFrameCount];

	//After rewind start recording from the first index 0 stop rewind when frame count=0 
	// each 100 ms record data foreach particle test time once before every particles loop 
	//on click on left key on arrow keys start rewind or just take the directions from the positions and directions from previous frame 

	public RewindData(int ParticlesCount,int StoredFrameCount)
	{
		this.ParticlesCount = ParticlesCount;
		this.StoredFrameCount = StoredFrameCount;
	}
	// I'll create a circuler buffer where the current positiong goes Back while the data is valid 
	// first step just do simple substitution for direction and positions for each frame 
}