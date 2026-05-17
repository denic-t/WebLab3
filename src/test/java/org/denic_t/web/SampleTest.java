package org.denic_t.web;

import org.junit.Test;
import static org.junit.Assert.*;

public class SampleTest {

    @Test
    public void testAddition() {
        System.out.println("Выполняется SampleTest: testAddition()");
        int expected = 4;
        int actual = 2 + 2;
        assertEquals("Проверка сложения: 2 + 2 должно равняться 4", expected, actual);
    }
}
