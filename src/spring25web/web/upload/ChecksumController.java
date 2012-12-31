package spring25web.web.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import spring25web.form.ChecksumForm;


@Controller
@RequestMapping("/upload/checksum.do")
public class ChecksumController {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * GETアクセス.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleInitRequest() {
		log.debug("handleInitRequest");

		ModelAndView model = new ModelAndView();

		ChecksumForm form = new ChecksumForm();
		form.setName("無題.txt");

		model.addObject("checksumForm", form);

		return model;
	}

	/**
	 * POSTアクセス(Check).
	 */
	@RequestMapping(method = RequestMethod.POST, params = {"doCheck"})
	public ModelAndView handleUploadRequest(
			ChecksumForm form, BindingResult result) throws IOException {
		log.debug("handleUploadRequest");

		if (!result.hasErrors()) {
			CommonsMultipartFile file = form.getFileData();
			if (file.isEmpty()) {
			    // ファイルがない場合
			    result.rejectValue("fileData", "required");

			} else {
    			Long crc32 = calculate(file);
    			if (crc32 != null) {
    				form.setChecksum(crc32);
    			}
			}
		}

		ModelAndView model = new ModelAndView();

		return model;
	}

	/**
	 * POSTアクセス(Report).
	 */
	@RequestMapping(method = RequestMethod.POST, params = {"doReport"})
	public ModelAndView handleUploadAndReportRequest(
			ChecksumForm form, BindingResult result) throws IOException {
		log.debug("handleUploadAndReportRequest");

		ModelAndView model = new ModelAndView();

		if (!result.hasErrors()) {
			CommonsMultipartFile file = form.getFileData();
            if (file.isEmpty()) {
                // ファイルがない場合
                result.rejectValue("fileData", "required");

            } else {
    			Long crc32 = calculate(file);
    			if (crc32 != null) {
    				// 出力ファイル名
    				String fileName = form.getName();
    				if (fileName == null || fileName.trim().length() == 0) {
    					fileName = file.getName();
    				}
    				if (fileName == null || fileName.trim().length() == 0) {
    					fileName = "不明.txt";
    				}
    
    				// モデルに設定し、ビューに遷移
    				model.addObject("crc32", Long.toHexString(crc32));
    				model.addObject("fileName", fileName);
    				model.setViewName("reportUploadFileCrc32View");
    			}
            }
		}

		return model;
	}

	/**
	 * CRC32を計算する.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected Long calculate(CommonsMultipartFile file) throws IOException {
		if (file != null) {
			log.info("storageDescription=" + file.getStorageDescription());

			CRC32 crc32 = new CRC32();
			InputStream is = file.getInputStream();
			try {
				byte[] buf = new byte[1024];
				int rd;
				while ((rd = is.read(buf)) > 0) {
					crc32.update(buf, 0, rd);
				}
			} finally {
				is.close();
			}

			return crc32.getValue();
		}
		return null;
	}
}
