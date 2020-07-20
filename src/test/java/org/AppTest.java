package org;

import org.junit.Test;

import static org.App.getColorNumber;
import static org.App.reduceColor;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void getColorNumberTest() {
        Float[] nativePixel = {1f, 0.082f, 0.082f};
        assertEquals("0", getColorNumber(nativePixel));
    }

    @Test
    public void reduceColorTest(){
        float[] nativePixel = {1f, 0.082f, 0.082f};
        Float[] cutPixel = {0.81f, 0.15f, 0.15f};
        assertArrayEquals(cutPixel, reduceColor(nativePixel));
    }

}

