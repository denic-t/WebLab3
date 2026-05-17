package org.denic_t.web;

import org.junit.Test;
import static org.junit.Assert.*;

public class LogicTest {

    @Test
    public void testStringLength() {
        System.out.println("Выполняется LogicTest: testStringLength()");
        String text = "WebLab3";
        assertEquals("Длина слова 'WebLab3' должна быть 7", 7, text.length());
    }

    @Test
    public void testTrueCondition() {
        System.out.println("Выполняется LogicTest: testTrueCondition()");
        boolean isGreater = 10 > 5;
        assertTrue("10 должно быть больше 5", isGreater);
    }
}
