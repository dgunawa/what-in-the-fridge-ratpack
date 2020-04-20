package com.maskapai;

import com.maskapai.model.CountingProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class CountingProcessorTest {
    private final CountingProcessor countingProcessor = new CountingProcessor();

    @Test
    public void existTest() {
        assertEquals(true, countingProcessor.isId("1"));
        assertEquals(false, countingProcessor.isId("4"));
        assertEquals(true, countingProcessor.isItem("1", "soda"));
        assertEquals(false, countingProcessor.isItem("1", "meat"));
    }

    @Test
    public void getItem() {
        assertEquals("{\"item\":{\"soda\":3},\"refrigerator\":\"1\"}", countingProcessor.getItem("1", "soda"));
    }

    @Test
    public void postItem() {
        assertEquals("{\"item\":{\"soda\":7},\"refrigerator\":\"1\"}", countingProcessor.postItem("1", "soda", "4"));
    }

    @Test
    public void postItemOver12() {
        assertEquals("{\"item\":{\"soda\":12},\"refrigerator\":\"1\"}", countingProcessor.postItem("1", "soda", "40"));
    }

    @Test
    public void putItem() {
        assertEquals("{\"item\":{\"soda\":5},\"refrigerator\":\"1\"}", countingProcessor.putItem("1", "soda", "5"));
    }

    @Test
    public void deleteItem() {
        assertEquals("{\"item\":{\"soda\":1},\"refrigerator\":\"1\"}", countingProcessor.deleteItem("1", "soda", "2"));
    }

    @Test
    public void deleteItemBelowZero() {
        assertEquals("{\"item\":{\"soda\":0},\"refrigerator\":\"1\"}", countingProcessor.deleteItem("1", "soda", "3"));
    }

}
