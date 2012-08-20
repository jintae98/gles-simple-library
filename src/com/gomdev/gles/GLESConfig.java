package com.gomdev.gles;

public class GLESConfig {
	public static final boolean DEBUG = false;
	
	public static final int 		NUM_OF_VERTEX_ELEMENT = 3;
	public static final int 		NUM_OF_NORMAL_ELEMENT = 3;
	public static final int			NUM_OF_TEXCOORD_ELEMENT = 2;
	public static final int 		NUM_OF_INDEX_ELEMENT = 6;
	public static final int 		FLOAT_SIZE_BYTES = 4;
	public static final int 		SHORT_SIZE_BYTES = 2;
	
	// depth
	public enum DepthLevel
	{
		HIGH_LEVEL_DEPTH,		// 100f
		DEFAULT_LEVEL_DEPTH,	// 0f
		LOW_LEVEL_DEPTH;		// -100f
		
		public static float enumToFloat(DepthLevel level)
		{
			switch(level)
			{
			case HIGH_LEVEL_DEPTH:
				return 100f; 
			case DEFAULT_LEVEL_DEPTH:
				return 0f;
			case LOW_LEVEL_DEPTH:
				return -100;
			}
			
			return 0f;
		}
	}
	
	public static final float 		SPACE_SCALE_FACTOR = 4.0f;

	public enum ProjectionType 
	{
		ORTHO,
		FRUSTUM;
	}
	
	public enum ObjectType
	{
		SOLID,
		ALPHA_TESTED,
		TRANPARENT
	}
	
	public enum ChipVendor 
	{
		CHIPSET_TI,
		CHIPSET_QUALCOMM;
	}
	
	public static final ChipVendor	CHIPSET_VENDOR = ChipVendor.CHIPSET_QUALCOMM;
	
	public static final boolean USE_BINARY = false;
	
	public static final boolean USE_VBO = true;
}
