package stargroup;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

public class Test {

    // 数据头包含4字节的起始码，和四字节的加密数据长度
    private static final byte[] DATA_HEADER = new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};

    private static final byte[] KEY = new byte[]{
                (byte)0xD6, (byte)0x71, (byte)0x3F, (byte)0xC0, (byte)0x65, (byte)0x87, (byte)0xD5, (byte)0x8B, (byte)0x56, (byte)0x3C, (byte)0x6E, (byte)0xF9, (byte)0xED, (byte)0xAD, (byte)0x24, (byte)0x92,
                (byte)0x70, (byte)0xFC, (byte)0x96, (byte)0x10, (byte)0x89, (byte)0x9C, (byte)0xFA, (byte)0x45, (byte)0xB6, (byte)0x52, (byte)0xB,  (byte)0xCD, (byte)0x6C, (byte)0xF6, (byte)0x18, (byte)0x3A,
                (byte)0xC0, (byte)0xBD, (byte)0xBF, (byte)0x5C, (byte)0x6C, (byte)0x3E, (byte)0xD3, (byte)0xCE, (byte)0xAD, (byte)0xEB, (byte)0x7A, (byte)0x2,  (byte)0x37, (byte)0x93, (byte)0x12, (byte)0x6F,
                (byte)0x99, (byte)0xE0, (byte)0x9A, (byte)0xF9, (byte)0x28, (byte)0xC6, (byte)0xB2, (byte)0x8E, (byte)0x1B, (byte)0xBE, (byte)0x7E, (byte)0xCD, (byte)0x6C, (byte)0xEC, (byte)0xED, (byte)0xA5,
                (byte)0xD6, (byte)0x71, (byte)0x3F, (byte)0xC0, (byte)0x65, (byte)0x87, (byte)0xD5, (byte)0x8B, (byte)0x56, (byte)0x3C, (byte)0x6E, (byte)0xF9, (byte)0xED, (byte)0xAD, (byte)0x24, (byte)0x92,
                (byte)0x70, (byte)0xFC, (byte)0x96, (byte)0x10, (byte)0x89, (byte)0x9C, (byte)0xFA, (byte)0x45, (byte)0xB6, (byte)0x52, (byte)0xB,  (byte)0xCD, (byte)0x6C, (byte)0xF6, (byte)0x18, (byte)0x3A,
                (byte)0xC0, (byte)0xBD, (byte)0xBF, (byte)0x5C, (byte)0x6C, (byte)0x3E, (byte)0xD3, (byte)0xCE, (byte)0xAD, (byte)0xEB, (byte)0x7A, (byte)0x2,  (byte)0x37, (byte)0x93, (byte)0x12, (byte)0x6F,
                (byte)0x99, (byte)0xE0, (byte)0x9A, (byte)0xF9, (byte)0x28, (byte)0xC6, (byte)0xB2, (byte)0x8E, (byte)0x1B, (byte)0xBE, (byte)0x7E, (byte)0xCD, (byte)0x6C, (byte)0xEC, (byte)0xED, (byte)0xA5};

    private static final int MAX_READ_LEN = 4096;
    public static void main(String[] args) {
        String inputFilePath;
        String outputFilePath;
        if (args.length < 1) {
            System.out.println("missing parameter!");
            return;
        } else if (args.length == 1){
            inputFilePath = args[0];
            int pos = inputFilePath.lastIndexOf(".");
            if (pos < 0) {
                System.out.println("Input file name error!");
                return;
            } else {
                outputFilePath = inputFilePath.substring(0, pos) + ".dec";
                System.out.println("Output file path:" + outputFilePath);
            }
        } else {
            inputFilePath = args[0];
            int lastDotPos = inputFilePath.lastIndexOf(".");
            if (lastDotPos < 0) {
                System.out.println("Input file name error!");
                return;
            } else {
                if (args[1].equals(".")) {
                    int lastSeparatorPos = inputFilePath.lastIndexOf(File.separator);
                    outputFilePath = System.getProperty("user.dir") + inputFilePath.substring(lastSeparatorPos, lastDotPos) + ".dec";
                } else {
                    outputFilePath = args[1];
                }
                System.out.println("Output file path:" + outputFilePath);
            }
        }

        FileInputStream in = null;
        FileOutputStream out = null;
//        URL inputFileUrl = Test.class.getClassLoader().getResource("zorro.log.enc");
//        System.out.println("getResource:"+inputFileUrl);
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        try {
            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile);

            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            byte[] readBuf = new byte[MAX_READ_LEN];
            byte[] headerBuf = new byte[8];
            byte[] realEncryptLenByte = new byte[4];
            int index = 0;
            while ((in.read(readBuf, 0, 1)) != -1) {
                headerBuf[index] = readBuf[0];
                if (index >= 3) {
                    if (headerBuf[0] == DATA_HEADER[0] && headerBuf[1] == DATA_HEADER[1] && headerBuf[2] == DATA_HEADER[2] && headerBuf[3] == DATA_HEADER[3]) {
                        index = 0;
                        int readLen = in.read(readBuf, 0, 4);
                        if (readLen < 4){
                            System.out.println("no more data! readLen:" + readLen);
                            break;
                        }
                        System.arraycopy(readBuf, 0, realEncryptLenByte,0, realEncryptLenByte.length);
                        int realEncryptLen = bytesToInt(realEncryptLenByte);
                        System.out.println("realEncryptLen:" + realEncryptLen);


                        Arrays.fill(readBuf, (byte) 0);
                        readLen = in.read(readBuf, 0, realEncryptLen);
                        if (readLen < realEncryptLen){
                            System.out.println("no more data!! readLen:" + readLen);
                            break;
                        }
                        xorDecrypt(readBuf, readBuf, realEncryptLen);
                        out.write(readBuf, 0, realEncryptLen);
                        Arrays.fill(readBuf, (byte) 0);
                    } else {
                        byte[] tmp = Arrays.copyOfRange(readBuf, 1,4);
                        System.arraycopy(tmp, 0, readBuf, 0, 3);
                    }
                } else {
                    index++;
                }
//                    System.out.println("readByte:"+Integer.toHexString(readBuf[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
//                    System.out.println("out.close "+(out != null));
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
//                    System.out.println("in.close "+(in != null));
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 数组转int类型
     *
     * @param src
     * @return
     */
    public static int bytesToInt(byte[] src) {
        int offset = 0;
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static int xorEncrypt(byte[] in, byte[] out, int inLen) {
        if (in != null && out != null) {
            int keyLen = KEY.length;
            for (int i = 0; i < inLen / keyLen + 1; i++) {
                for (int j = 0; j < keyLen && j < inLen - i * keyLen; j++) {
                    out[i * keyLen + j] = (byte) (in[i * keyLen + j] ^ KEY[j]);
                }
            }
            return inLen;
        }
        return -1;
    }

    public static int xorDecrypt(byte[] in, byte[] out, int inLen) {
        return xorEncrypt(in, out, inLen);
    }
}
