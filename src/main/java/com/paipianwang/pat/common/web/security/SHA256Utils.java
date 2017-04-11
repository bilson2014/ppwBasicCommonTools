package com.paipianwang.pat.common.web.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA256Utils {
	public static final String ALGORITHM = "SHA-256";

	public static String SHA256Encrypt(String orignal) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (null != md) {
			byte[] origBytes = orignal.getBytes();
			md.update(origBytes);
			byte[] digestRes = md.digest();
			return Base64.getEncoder().encodeToString(digestRes);
		}
		return null;
	}

}
