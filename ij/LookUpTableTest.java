package ij;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.awt.image.*;

import ij.process.*;

public class LookUpTableTest {

	//  *********    instance vars of LookUpTest
	
	IndexColorModel cm;
	byte[] reds, greens, blues;
	LookUpTable lut;
	
	//  *********  helper methods of LookupTest
	
	// helper - create a list of bytes in ascending order
	private byte[] ascending(int count)
	{
		byte[] output = new byte[count];
		for (int i = 0; i < count; i++)
			output[i] = (byte)i;
		return output;
	}
	
	// helper - create a list of bytes in descending order
	private byte[] descending(int count)
	{
		byte[] output = new byte[count];
		for (int i = 0; i < count; i++)
			output[i] = (byte)(count-i-1);
		return output;
	}
	
	// helper - create a list of repeated bytes
	private byte[] repeated(int count, int value)
	{
		byte[] output = new byte[count];
		for (int i = 0; i < count; i++)
			output[i] = (byte)value;
		return output;
	}
	
	// helper - create a list of random bytes from 0 .. range-1
	private byte[] random(int count, int range)
	{
		byte[] output = new byte[count];
		for (int i = 0; i < count; i++)
			output[i] = (byte)Math.floor(range*Math.random());
		return output;
	}
	
	//  **********     Test code for LookUpTest follows
	
	// default code run before every test
	@Before
	public void setup()
	{
		reds = ascending(256);
		greens = descending(256);
		blues = repeated(256,73);
		cm = new IndexColorModel(8, 256, reds, greens, blues);
		lut = new LookUpTable(cm);
	}
	
	// create a LookUpTable from an awt.Image
	@Test
	public void testLookUpTableImage() {
		BufferedImage bi = new BufferedImage(5,3,BufferedImage.TYPE_BYTE_INDEXED,cm);
		lut = new LookUpTable(bi);
		assertNotNull(lut);
		assertEquals(cm,lut.getColorModel());
	}

	// create a LookUpTable from a ColorModel
	@Test
	public void testLookUpTableColorModel() {
		byte[] tmp = new byte[256];
		
		assertNotNull(lut);
		cm.getReds(tmp);
		assertArrayEquals(tmp,lut.getReds());
		cm.getGreens(tmp);
		assertArrayEquals(tmp,lut.getGreens());
		cm.getBlues(tmp);
		assertArrayEquals(tmp,lut.getBlues());
	}

	@Test
	public void testGetMapSize() {
		// default case
		assertNotNull(lut);
		assertEquals(256,lut.getMapSize());
		
		// try a non-256 color case
		reds = repeated(16,0);
		greens = repeated(16,1);
		blues = repeated(16,2);
		cm = new IndexColorModel(4,16,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertNotNull(lut);
		assertEquals(16,lut.getMapSize());
	}

	@Test
	public void testGetRedsBluesGreens() {
		// test default case
		assertNotNull(lut);
		assertArrayEquals(reds,lut.getReds());
		assertArrayEquals(greens,lut.getGreens());
		assertArrayEquals(blues,lut.getBlues());
	}

	@Test
	public void testGetColorModel() {
		// test default case
		assertEquals(cm,lut.getColorModel());
	}

	@Test
	public void testIsGrayscale() {
		
		// THESE ARE NOT GRAYSCALE

		// test default LUT
		assertFalse(lut.isGrayscale());
		
		// test another random LUT
		reds = repeated(256,22);
		greens = repeated(256,33);
		blues = repeated(256,44);
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertFalse(lut.isGrayscale());
		
		// test off by one in reds
		reds = repeated(256,21);
		greens = repeated(256,22);
		blues = repeated(256,22);
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertFalse(lut.isGrayscale());
		
		// test off by one in greens
		reds = repeated(256,22);
		greens = repeated(256,21);
		blues = repeated(256,22);
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertFalse(lut.isGrayscale());
		
		// test off by one in blues
		reds = repeated(256,22);
		greens = repeated(256,22);
		blues = repeated(256,21);
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertFalse(lut.isGrayscale());
		
		// test lutsize != 256
		reds = repeated(255,22);
		greens = repeated(255,22);
		blues = repeated(255,22);
		cm = new IndexColorModel(8,255,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertFalse(lut.isGrayscale());
		
		// THESE ARE GRAYSCALE
		
		// test an ascending LUT
		reds = ascending(256);
		greens = ascending(256);
		blues = ascending(256);
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertTrue(lut.isGrayscale());
		
		// test a descending LUT
		reds = descending(256);
		greens = descending(256);
		blues = descending(256);
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertTrue(lut.isGrayscale());
		
		// test a repeating LUT
		reds = repeated(256,22);
		greens = repeated(256,22);
		blues = repeated(256,22);
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertTrue(lut.isGrayscale());
		
		// test a random LUT where the r,g,b values agree
		reds = random(256,256);
		greens = reds.clone();
		blues = reds.clone();
		cm = new IndexColorModel(8,256,reds,greens,blues);
		lut = new LookUpTable(cm);
		assertTrue(lut.isGrayscale());
	}

	/*
	   -- can't test this method as it pokes the onscreen graphics.
	@Test
	public void testDrawColorBar() {
		FakeGraphics gr = new FakeGraphics();
		lut.drawColorBar(gr, 1, 1, 2, 2);
	}
	*/
	
	@Test
	public void testDrawUnscaledColorBar() {
	
		ImageProcessor proc;
		ColorModel c;
		
		int maxW = 20;
		int maxH = 6;
		
		reds = ascending(256);
		greens = ascending(256);
		blues = repeated(256,44);

		// case 1 : byteproc and indexcolormodel
		
		proc = new ByteProcessor(maxW,maxH);
		for (int i = 0; i < maxW; i++)
			for (int j = 0; j < maxH; j++)
				proc.set(i, j, 0);

		c = new IndexColorModel(8, 256, reds, greens, blues);
		lut = new LookUpTable(c);

		lut.drawUnscaledColorBar(proc, 0+1, 0+1, maxW-2, maxH-2);
		
		assertEquals(0,proc.get(0,2));
		assertEquals(15,proc.get(1,2));
		assertEquals(15,proc.get(2,2));
		assertEquals(16,proc.get(3,2));
		assertEquals(17,proc.get(4,2));
		assertEquals(17,proc.get(5,2));
		assertEquals(18,proc.get(6,2));
		assertEquals(19,proc.get(7,2));
		assertEquals(19,proc.get(8,2));
		assertEquals(20,proc.get(9,2));
		assertEquals(21,proc.get(10,2));
		assertEquals(21,proc.get(11,2));
		assertEquals(22,proc.get(12,2));
		assertEquals(23,proc.get(13,2));
		assertEquals(23,proc.get(14,2));
		assertEquals(24,proc.get(15,2));
		assertEquals(25,proc.get(16,2));
		assertEquals(25,proc.get(17,2));
		assertEquals(26,proc.get(18,2));
		assertEquals(0,proc.get(19,2));
		
		// case 2 : colorproc and indexcolormodel
		
		proc = new ColorProcessor(maxW,maxH);
		for (int i = 0; i < maxW; i++)
			for (int j = 0; j < maxH; j++)
				proc.set(i, j, 0);
		
		c = new IndexColorModel(8, 256, reds, greens, blues);
		lut = new LookUpTable(c);

		lut.drawUnscaledColorBar(proc, 0+1, 0+1, maxW-2, maxH-2);

		/*
		for (int j = maxH-1; j >= 0; j--)
		{
			System.out.print("[");
			for (int i = 0; i < maxW; i++)
			{
				System.out.print(proc.get(i,j));
				if (i != maxW-1)
					System.out.print(",");
			}
			System.out.println("]");
		}
		*/
		
		assertEquals(-16777216,proc.get(0,2));
		assertEquals(-16777172,proc.get(1,2));
		assertEquals(-16711380,proc.get(2,2));
		assertEquals(-16645588,proc.get(3,2));
		assertEquals(-16579796,proc.get(4,2));
		assertEquals(-16514004,proc.get(5,2));
		assertEquals(-16448212,proc.get(6,2));
		assertEquals(-16382420,proc.get(7,2));
		assertEquals(-16316628,proc.get(8,2));
		assertEquals(-16250836,proc.get(9,2));
		assertEquals(-16185044,proc.get(10,2));
		assertEquals(-16119252,proc.get(11,2));
		assertEquals(-16053460,proc.get(12,2));
		assertEquals(-15987668,proc.get(13,2));
		assertEquals(-15921876,proc.get(14,2));
		assertEquals(-15856084,proc.get(15,2));
		assertEquals(-15790292,proc.get(16,2));
		assertEquals(-15724500,proc.get(17,2));
		assertEquals(-15658708,proc.get(18,2));
		assertEquals(-16777216,proc.get(19,2));

		// case 3 : byteproc and not indexcolormodel
		//   I don't think this combo arises here
		
		// case 4 : colorproc and not indexcolormodel
		//   I don't think this combo arises here
	}

	@Test
	public void testCreateGrayscaleColorModel() {
		ColorModel c;
		
		// setup channel data for comparison
		reds = ascending(256);
		greens = ascending(256);
		blues = ascending(256);

		// try an ascending grayscale color model
		c = LookUpTable.createGrayscaleColorModel(false);
		
		for (int i = 0; i < 256; i++)
		{
			assertEquals(reds[i],(byte)c.getRed(i));
			assertEquals(greens[i],(byte)c.getGreen(i));
			assertEquals(blues[i],(byte)c.getBlue(i));
		}
		
		// try an inverted grayscale color model
		c = LookUpTable.createGrayscaleColorModel(true);
		
		for (int i = 0; i < 256; i++)
		{
			assertEquals(reds[i],(byte)(255 - c.getRed(i)));
			assertEquals(greens[i],(byte)(255 - c.getGreen(i)));
			assertEquals(blues[i],(byte)(255 - c.getBlue(i)));
		}
	}

}
