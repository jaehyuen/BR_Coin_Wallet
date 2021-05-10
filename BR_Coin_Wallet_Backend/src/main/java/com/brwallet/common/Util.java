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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.brwallet.common.vo.ResultVo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Util {

	private final RestTemplate restTemplate;

	@Value("${rest.url}")
	private String             url;

	/**
	 * rest post 요청을 하는 메소드
	 * 
	 * @param <T>  body로 보낼 타입
	 * @param <S>  리턴 타입
	 * @param path
	 * @param body body 값
	 * @param type 리턴 타입
	 * @return ResultVo<type>
	 */
	public <T, S> ResultVo<S> sendPost(String path, T body, Type type) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<T>               request  = new HttpEntity<T>(body, headers);
		
		System.out.println(request);

		ResponseEntity<ResultVo<S>> response = restTemplate.exchange(url + path, HttpMethod.POST, request, createReturnType(type));

		System.out.println(response);
		return response.getBody();
			

	}

	/**
	 * rest get 요청을 하는 메소드
	 * 
	 * @param <T>  리턴 타입
	 * @param path
	 * @param type 리턴 타입
	 * @return ResultVo<type>
	 */
	public <T> ResultVo<T> sendGet(String path, Type type) {

		ResponseEntity<ResultVo<T>> response = restTemplate.exchange(url + path, HttpMethod.GET, null, createReturnType(type));

		return response.getBody();

	}

	public <T> ResultVo<T> setResult(String code, boolean flag, String message, T data) {

		ResultVo<T> resultVo = new ResultVo<T>();

		resultVo.setResultCode(code);
		resultVo.setResultFlag(flag);
		resultVo.setResultMessage(message);
		resultVo.setResultData(data);

		return resultVo;

	}

	/**
	 * 해당 url의 이미지를 저장하는 메소드
	 * 
	 * @param strUrl   주소
	 * @param fileName 저장할 파일명
	 * @throws IOException
	 */
	public void saveImage(String strUrl, String fileName) throws IOException {

		URL          url = null;
		InputStream  in  = null;
		OutputStream out = null;

		// 폴더를 생성
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

	/**
	 * 폴더를 생성하는 메소드
	 * 
	 * @param path 폴더 경로+폴더
	 * @return 생성 여부 값
	 */
	public boolean makeFolder(String path) {
		if (path.length() < 0) {
			return false;
		}

		File Folder = new File(path);

		// 폴더가 존재하지 않으면 생성
		if (!Folder.exists()) {
			try {
				Folder.mkdirs();

			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		return true;
	}

	/**
	 * ResultVo 의 제네릭 필드 타입 설정 메소드
	 * 
	 * @param <T>
	 * @param type 제네릭 필드 클래스
	 * @return ParameterizedTypeReference
	 */
	private <T> ParameterizedTypeReference<ResultVo<T>> createReturnType(Type type) {

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

		return new ParameterizedTypeReference<ResultVo<T>>() {
			@Override
			public Type getType() {
				return parameterizedType;
			}

		};

	}

}
