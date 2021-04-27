package com.brcoin.wallet.common;

import org.springframework.stereotype.Component;

import com.brcoin.wallet.vo.ResultVo;

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

}
