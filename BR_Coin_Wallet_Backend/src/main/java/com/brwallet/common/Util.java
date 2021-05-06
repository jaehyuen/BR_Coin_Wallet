package com.brwallet.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.brwallet.common.vo.ResultVo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Util {

	private final RestTemplate restTemplate;

	public <T> ResultVo<T> sendPost(String path, T body) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<T> request = new HttpEntity<T>(body, headers);
		try {
			ResultVo<T> response = restTemplate.postForObject("localhost:8081" + path, request, ResultVo.class);

			return response;
		} catch (Exception e) {

			throw new RuntimeException("post error");
		}

	}

	public <T> T sendGet(String path, Class<?> test) {

		Type              type              = test;
		ParameterizedType parameterizedType = new ParameterizedType() {
												@Override
												public Type[] getActualTypeArguments() {
													return new Type[] { type };
												}

												@Override
												public Type getRawType() {
													return ResultVo.class;
												}

												@Override
												public Type getOwnerType() {
													return null;
												}
											};

		HttpHeaders       headers           = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

//		HttpEntity<T> request = new HttpEntity<T>(body, headers);
		try {

			System.out.println(type);
			System.out.println(test.getGenericSuperclass());
			T response = restTemplate.exchange("http://localhost:8081" + path, HttpMethod.GET, null, new ParameterizedTypeReference<ResultVo<T>>() {
				@Override
				public Type getType() {
					return parameterizedType;
				}

			})
				.getBody()
				.getResultData();

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("get error");
		}

	}

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
