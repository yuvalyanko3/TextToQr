import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import javax.media.MediaLocator;
import javax.swing.text.html.HTMLDocument;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Vector;

public class Main {
    private static final String filePath = "resources/qrText.txt";
    private static final String qrCodePath = "qrCodes/qrCode";
    private static String text;

    public static void main(String[] args) {
        try {
            text = readFile(filePath, Charset.defaultCharset());
            String[] strings = separateStrings(text, 200);
            for(int i = 0 ; i<strings.length ; i++)
                generateQR(strings[i], 350, 350, qrCodePath + (i+1) + ".jpeg");
            createVideo("qrVideo.MP4");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the input as a string format.
     * @param path
     * @param set
     * @return
     * @throws IOException
     */
    private static String readFile(String path, Charset set) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return new String(bytes,set);
    }

    /**
     * Separate a string into text blocks of the same size.
     * @param text
     * @param size
     * @return
     */
    private static String[] separateStrings(String text, int size){

        String[] toReturn = new String[text.length()/size + 1];
        for(int i = 0, ind = 0 ; i<text.length() ; i+=size)
            toReturn[ind++] = text.substring(i, Math.min(text.length(), i+size));
        return toReturn;
    }

    /**
     * Generate the qr code images.
     * @param text
     * @param width
     * @param height
     * @param filePath
     * @throws WriterException
     * @throws IOException
     */
    private static void generateQR(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bm = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(bm, "JPEG", new File(filePath).toPath());

    }

    /**
     * Generate a video from the qr code images.
     * @param fileName
     * @throws IOException
     */
    private static void createVideo(String fileName) throws IOException {
        Vector<String> imgList = createPathList("qrCodes");
        JpegImagesToMovie toMovie = new JpegImagesToMovie();
        MediaLocator locator;
        if((locator = toMovie.createMediaLocator(fileName)) == null)
            throw new IOException("Cannot build loactor.");
        int interval = 50;
        toMovie.doIt(350,350,(1000/interval), imgList, locator);
    }

    /**
     * Helper method user in "createVideo".
     * @param imagePath
     * @return
     */
    private static Vector<String> createPathList(String imagePath){
        Vector<String> toReturn = new Vector<>();
        File dir = new File(imagePath);
        File[] fileList = dir.listFiles();
        for(File f : fileList)
            toReturn.add(imagePath + "/" + f.getName());
        return toReturn;
    }
}
