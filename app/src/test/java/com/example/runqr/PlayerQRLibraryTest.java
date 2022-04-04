package com.example.runqr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests Player and QRLibrary classes
 */
public class PlayerQRLibraryTest {

    Hasher testHasher = new Hasher();

    String testString1 = "my name is tester";
    String hashedTestString1 = testHasher.hashQRCode(testString1);
    QRCode testCode1 = new QRCode(hashedTestString1);

    String testString2 = "ThisIsCMPUT";
    String hashedTestString2 = testHasher.hashQRCode(testString2);
    QRCode testCode2 = new QRCode(hashedTestString2);



    @Test
    public void testPlayer(){
        Player testPlayer = new Player (new Account(), new QRLibrary());
        QRLibrary testLibrary = testPlayer.getPlayerQRLibrary();

        assertEquals(testLibrary.returnSize(), 0);
        testPlayer.addQRCode(testCode1);
        assertEquals(testLibrary.returnSize(),1);
        testPlayer.addQRCode(testCode2);
        assertEquals(testLibrary.returnSize(),2);
        assertEquals(testLibrary.getQRCode(0), testCode1);

        assertEquals(testLibrary.getQRLibraryScore(), testCode1.getScore()+testCode2.getScore());
        testPlayer.deleteQRCode(testCode1);
        assertEquals(testLibrary.returnSize(),1);

    }

}
