package spring25web.form;

import org.springframework.web.multipart.commons.CommonsMultipartFile;


/**
 * ファイルのチェックサム計算用フォーム 
 */
public class ChecksumForm {

    /**
     * ファイル名
     */
	private String name;

	/**
	 * アップロードファイル
	 */
	private CommonsMultipartFile fileData;

	/**
	 * 計算されたチェクサム
	 */
	private long checksum;

	
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public CommonsMultipartFile getFileData()
	{
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData)
	{
		this.fileData = fileData;
	}

	public long getChecksum() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}
	
	@Override
	public String toString() {
	    StringBuilder buf = new StringBuilder();
	    buf.append("(name=").append(name);
        buf.append(", fileData=").append(fileData);
        buf.append(", checksum=").append(checksum);
        buf.append(")");
	    return buf.toString();
	}
}
