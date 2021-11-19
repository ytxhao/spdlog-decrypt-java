package com.stargroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class DecryptProcessor {

    private static final int MAX_READ_LEN = 4096;
    public static void main(String[] args) {
	// write your code here
        System.out.println("hello world");
//        if (args.length < 2) {
//            System.out.println("missing parameter!");
//            return;
//        }

//        String input_file_path = args[1];
//        String inputFilePath = "zorro.log.enc";
        InputStream in = null;
        URL inputFileUrl = DecryptProcessor.class.getClassLoader().getResource("zorro.log.enc");
        System.out.println("getResource:"+inputFileUrl);
        if (inputFileUrl != null && inputFileUrl.getFile() != null) {
            File inputFile = new File(inputFileUrl.getFile());
            try {
                in = new FileInputStream(inputFile);
                byte[] readBuf = new byte[MAX_READ_LEN];
                int readByte;
                int j = 0;
                while ((readByte = in.read()) != -1) {

                    System.out.println("readByte:"+Integer.toHexString(readByte));
                    j++;
                    if (j>10)
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
