package com.ivolve;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AppTest {

    @Test
    public void testMainOutput() {
        // Step 1: Capture System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Step 2: Call main()
            App.main(new String[]{});

            // Step 3: Compare output
            String expectedOutput = "Hello iVolve Trainee\n"; 
            assertEquals(expectedOutput, outContent.toString());
        } finally {
            // Step 4: Restore System.out
            System.setOut(originalOut);
        }
    }
}

