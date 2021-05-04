package com.brwallet.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.springframework.stereotype.Component;

import com.brwallet.common.vo.ResultVo;

@Component
public class Util {

	public <T> ResultVo<T> setResult(String code, boolean flag, String message, T data) {

		ResultVo<T> resultVo = new ResultVo<T>();

		resultVo.setResultCode(code);
		resultVo.setResultFlag(flag);
		resultVo.setResultMessage(message);
		resultVo.setResultData(data);

		return resultVo;

	}
	public void saveImage(String strUrl, String fileName) throws IOException {

		URL          url = null;
		InputStream  in  = null;
		OutputStream out = null;

		if (makeFolder(System.getProperty("user.dir") + "/otp")) {
			try {

				url = new URL(strUrl);

				in  = url.openStream();

				out = new FileOutputStream(System.getProperty("user.dir") + "/otp/" + fileName + ".jpg"); // 저장경로

				while (true) {
					// 이미지를 읽어온다.
					int data = in.read();
					if (data == -1) {
						break;
					}
					// 이미지를 쓴다.
					out.write(data);

				}

				in.close();
				out.close();

			} catch (Exception e) {

				e.printStackTrace();

			} finally {

				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}

			}
		}

	}

	public boolean makeFolder(String folder) {
		if (folder.length() < 0) {
			return false;
		}
		String path   = folder;
		File   Folder = new File(path);
		if (!Folder.exists()) {
			try {
				Folder.mkdir();

			} catch (Exception e) {
				e.getStackTrace();
			}
		} else {

		}
		return true;
	}

}
