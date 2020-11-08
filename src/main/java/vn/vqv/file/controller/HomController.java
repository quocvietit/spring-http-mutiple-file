package vn.vqv.file.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Create by VietVQ at 11/8/2020
 * Gmail: vuongquocviet1996@gmail.com
 */
@Controller
@Slf4j
public class HomController {
    @GetMapping()
    public String view() {
        return "index";
    }

    @ResponseBody
    @GetMapping("/download-file")
    public void download(HttpServletResponse httpResponse) {
        try {
            List<File> files = new ArrayList<>();

            File folder = new File("files");
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles()))
                if (!fileEntry.isDirectory())
                    files.add(fileEntry);

            httpResponse.setContentType("multipart/x-mixed-replace;boundary=END");

            ServletOutputStream out = httpResponse.getOutputStream();
            out.println();
            out.println("--END");

            for (File file : files) writeFile(out, file);

            out.println("--END--");
            out.flush();
            out.close();
        } catch (Exception ex) {
            log.error("Request Exception!", ex);
        }
    }

    private void writeFile(ServletOutputStream out, File file) throws IOException {
        log.info("File: {}", file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {

            BufferedInputStream fif = new BufferedInputStream(fis);

            out.println("Content-type: text/rtf");
            out.println("Content-Disposition: attachment; filename=" + file.getName());
            out.println();

            int data;
            while ((data = fif.read()) != -1) out.write(data);
            fif.close();

        } catch (Exception ex) {
            log.error("", ex);
        }

        out.println();
        out.println("--END");
        out.flush();
    }
}
