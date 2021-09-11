import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Common {
	static Object serializeObject(Object obj, String filePath) {
		new ObjectOutputStream(new FileOutputStream(filePath)).withCloseable {
			it.writeObject(obj)
			return obj
		}
	}

	static Object deserializeObject(String filePath) {
		if (new File(filePath).exists()) {
			new ObjectInputStream(new FileInputStream(filePath)).withCloseable {
				return it.readObject()
			}
		} else {
			return null
		}
	}

	static String binanceGet(HttpClient client, String call, String key) {
		new GetMethod(call).with {get ->
			get.setRequestHeader("X-MBX-APIKEY", key)
			get.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded')
			client.executeMethod(get)
			return get.responseBodyAsString
		}
	}

	static String createHmac(String secretKey, String data, String type = "HmacSHA256") {
		def mac = Mac.getInstance(type)
		mac.init(new SecretKeySpec(secretKey.getBytes(), type))
		def digest = mac.doFinal(data.getBytes())
		def hex = digest.encodeHex().toString()
		return hex
	}

	static String createBinanceSignature(List<String> params, String secret) {
		def hmac = Common.createHmac(secret, params.join('&'))
		return "signature=${hmac}"
	}

	static String createBinanceTimestamp() {
		// https://api.binance.com/api/v1/time ??
		// def serverTime = new JsonSlurper().parse(new URL('https://api.binance.com/api/v1/time'))['serverTime']
		// params << "timestamp=$serverTime"
		return "timestamp=${new Date().time}"
	}
}
