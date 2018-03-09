package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import java.io.*;
import org.apache.commons.io.IOUtils;

public final class TaskImplementation implements FileEncoder {
    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fin));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fout));
        ) {
            byte[] b = new byte[3];
            int num;
            while ((num = bis.read(b)) != -1) {
                int b1, b2, b3, b4;
                b1 = (b[0]&0xFC) >>> 2;
                b2 = ((b[0]&0x3) << 4) + ((b[1]&0xF0) >>> 4);
                b3 = ((b[1]&0xf) << 2) + ((b[2]&0xC0) >>> 6);
                b4 = b[2]&0x3f;
                StringBuilder result = new StringBuilder();
                if (num == 3) {
                    result.append(toBase64[b1])
                            .append(toBase64[b2])
                            .append(toBase64[b3])
                            .append(toBase64[b4]);
                } else if (num==2) {
                    result.append(toBase64[b1])
                            .append(toBase64[b2])
                            .append(toBase64[b3])
                            .append('=');
                } else if (num==1) {
                    result.append(toBase64[b1])
                            .append(toBase64[b2])
                            .append('=')
                            .append('=');
                } else {
                    System.out.println("Smth is wrong");
                }
                byte[] buffer = result.toString().getBytes();
                bos.write(buffer, 0, buffer.length);
                b = new byte[]{0,0,0};
            }
        }

        return fout;
    }


    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws Exception {
        final FileEncoder encoder = new TaskImplementation();
//         NOTE: open http://localhost:9000/ in your web browser
        (new Bootstrapper(args, encoder))
                .bootstrap("", new InetSocketAddress("127.0.0.1", 9000));

    }

}
