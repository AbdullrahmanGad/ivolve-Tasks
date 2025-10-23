package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AppTest {

    @Test
    public void testMainOutput() {
        // Capture System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Call main method
            App.main(new String[]{});

            // Expected output (note the newline added by println)
            String expectedOutput = "Hello Ivolve Trainee\n";

            assertEquals(expectedOutput, outContent.toString());
        } finally {
            // Restore System.out
            System.setOut(originalOut);
        }
    }
}
