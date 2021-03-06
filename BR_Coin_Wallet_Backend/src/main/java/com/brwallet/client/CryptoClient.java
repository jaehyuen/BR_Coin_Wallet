package com.brwallet.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.brwallet.wallet.vo.KeyPairVo;

@Component
public class CryptoClient {

	final int      KEY_SIZE = 1024;
	private Logger logger   = LoggerFactory.getLogger(this.getClass());

	public KeyPairVo generateKeys() throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
		generator.initialize(KEY_SIZE);

		KeyPair       keyPair   = generator.generateKeyPair();

		RSAPrivateKey priv      = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey  pub       = (RSAPublicKey) keyPair.getPublic();

		KeyPairVo     keyPairVo = new KeyPairVo();

		keyPairVo.setPrivateKey(getKeyString(priv, "BRCOIN PRIVATE KEY"));
		keyPairVo.setPublicKey(getKeyString(pub, "BRCOIN PUBLIC KEY"));

		return keyPairVo;

	}

	private String getKeyString(Key key, String desc) throws IOException {

		// OutputStream ??????
		OutputStream output    = new OutputStream() {
									private StringBuilder string = new StringBuilder();

									@Override
									public void write(int b) throws IOException {
										this.string.append((char) b);
									}

									// Netbeans IDE automatically overrides this toString()
									public String toString() {
										return this.string.toString();
									}
								};
		PemObject    pem       = new PemObject(desc, key.getEncoded());
		PemWriter    pemWriter = new PemWriter(new OutputStreamWriter(output));

		try {
			pemWriter.writeObject(pem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pemWriter.close();

		}
		return output.toString();
	}

	public String testEncode(String plainData, String privKey) {
		String encryptedData = null;
		try {
			// ????????? string??? ????????? ????????? ??????

			PrivateKey privateKey = getPrivateKey(privKey);

			// ???????????? ?????????????????? ???????????? ?????????????????? ??????
			Cipher     cipher     = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);

			// ????????? ?????????
			byte[] byteEncryptedData = cipher.doFinal(plainData.getBytes());
			encryptedData = Base64.getEncoder()
				.encodeToString(byteEncryptedData);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedData;

	}

	public String testDecode(String encryptedData, String pubKey) {
		String decryptedData = null;
		try {
			// ????????? string??? ????????? ????????? ??????

			PublicKey publicKey = getPublicKey(pubKey);

			// ???????????? ?????????????????? ???????????? ?????????????????? ???????????? ??????
			Cipher    cipher    = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);

			// ???????????? ?????????
			byte[] byteEncryptedData = Base64.getDecoder()
				.decode(encryptedData.getBytes());
			byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
			decryptedData = new String(byteDecryptedData);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return decryptedData;
	}

	private PublicKey getPublicKey(String stringPublicKey) {
		PublicKey publicKey = null;
		try {

			// key string??? ??????????????? ?????????
			stringPublicKey = stringPublicKey.replaceAll("\n", "")
				.replace("-----BEGIN BRCOIN PUBLIC KEY-----", "")
				.replace("-----END BRCOIN PUBLIC KEY-----", "");

			// ????????? string??? ????????? ????????? ??????
			KeyFactory         keyFactory    = KeyFactory.getInstance("RSA");
			byte[]             bytePublicKey = Base64.getDecoder()
				.decode(stringPublicKey.getBytes());
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
			publicKey = keyFactory.generatePublic(publicKeySpec);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return publicKey;
	}

	private PrivateKey getPrivateKey(String stringPrivateKey) {
		PrivateKey privateKey = null;
		try {

			// key string??? ??????????????? ?????????
			stringPrivateKey = stringPrivateKey.replaceAll("\n", "")
				.replace("-----BEGIN BRCOIN PRIVATE KEY-----", "")
				.replace("-----END BRCOIN PRIVATE KEY-----", "");

			// ????????? string??? ????????? ????????? ??????
			KeyFactory          keyFactory     = KeyFactory.getInstance("RSA");
			byte[]              bytePrivateKey = Base64.getDecoder()
				.decode(stringPrivateKey.getBytes());
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
			privateKey = keyFactory.generatePrivate(privateKeySpec);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return privateKey;
	}

	public String sign(String plainText, String stringPrivateKey) {
		try {

			// ????????? string??? ????????? ????????? ??????
			PrivateKey privateKey       = getPrivateKey(stringPrivateKey);
			Signature  privateSignature = Signature.getInstance("SHA256withRSA");

			// ???????????? ????????? ??????
			privateSignature.initSign(privateKey);
			privateSignature.update(plainText.getBytes("UTF-8"));
			byte[] signature = privateSignature.sign();
			return Base64.getEncoder()
				.encodeToString(signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException | SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean verifySignarue(String plainText, String signature, String stringPublicKey) {
		Signature sig;
		try {
			// ????????? string??? ????????? ????????? ??????
			PublicKey publicKey = getPublicKey(stringPublicKey);
			sig = Signature.getInstance("SHA256withRSA");

			// ???????????? ?????? ????????? ??????
			sig.initVerify(publicKey);
			sig.update(plainText.getBytes());

			if (!sig.verify(Base64.getDecoder()
				.decode(signature)))
				;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
