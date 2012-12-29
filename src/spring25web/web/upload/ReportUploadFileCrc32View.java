package spring25web.web.upload;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

/**
 * view-resolver.xmlによるView定義例
 */
@Component("reportUploadFileCrc32View")
public class ReportUploadFileCrc32View extends AbstractView {

    /**
     * ログ
     */
	private Log log = LogFactory.getLog(getClass());

	/**
	 * プロパティ
	 */
	@Autowired(required = true)
	@Qualifier("props")
	private Properties props;

	
	public ReportUploadFileCrc32View() {
		log.info("ReportUploadFileCrc32View#ctor()");
	}

	@Override
	protected void renderMergedOutputModel(
			@SuppressWarnings({"rawtypes"}) Map model,
			HttpServletRequest req,
			HttpServletResponse res
			) throws Exception {

		String fileName = (String)model.get("fileName");
		byte[] sjisName = fileName.getBytes("MS932");
		String encodedName = new String(sjisName, "ISO8859_1");

		res.setContentType("application/octet-stream");
		res.setHeader(
				"Content-Disposition",
				"attachment; filename=" + encodedName
				);

		// 64kbを超えたらチャンク形式でレスポンスを返す.
		final int bufsiz = 1024 * 64; // 64kb
		res.setBufferSize(bufsiz);

		Long crc32 = (Long)model.get("crc32");

		OutputStream os = res.getOutputStream();
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
			try {
				pw.println("data1" + props.getProperty("data1"));
				pw.println("data2" + props.getProperty("data2"));
				pw.println("data3" + props.getProperty("data3"));

				pw.println("fileName=" + fileName);
				pw.println("crc32=" + crc32);

			} finally {
				pw.close();
			}

		} finally {
			os.close();
		}
	}

}
