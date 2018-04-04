package com.yvan;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";

	private String ip;
	private Integer port;
	private String serviceName;
	private String url;

	public HttpUtil(String url) {
		this.url = url;
	}

	public HttpUtil(String ip, Integer port, String serviceName) {
		this.ip = ip;
		this.port = port;
		this.serviceName = serviceName;
		url = "http://" + this.ip + (this.port == null ? "" : (":" + this.port)) + "/" + this.serviceName;
	}

	public String get() throws ParseException, IOException {
		return get(null);
	}

	public String post() throws ParseException, IOException {
		return post(null);
	}

	public String get(Map<String, Object> param) throws ParseException, IOException {
		return get(null, param);
	}

	public String post(Map<String, Object> param) throws ParseException, IOException {
		return post(null, param);
	}

	public String get(String cmd, Map<String, Object> param) throws ParseException, IOException {
		return send(cmd, GET, param);
	}

	public String post(String cmd, Map<String, Object> param) throws ParseException, IOException {
		return send(cmd, POST, param);
	}

	public String put(String cmd, Map<String, Object> param) throws ParseException, IOException {
		return send(cmd, PUT, param);
	}

	public String send(String cmd, String method, Map<String, Object> param) throws ParseException, IOException {
		return send(cmd, method, (Object) param);
	}

	public String send(String cmd, Map<String, Object> param, Boolean getTruePostFalse) throws ParseException, IOException {
		return http(url + StringUtil.valueOfEmpty(cmd), param, getTruePostFalse);
	}

	public static String http(String url, Map<String, Object> param, Boolean getTruePostFalse) throws ParseException, IOException {
		return http(getTruePostFalse ? GET : POST, url, param);
	}

	/**
	 * 
	 * @param cmd
	 * @param method
	 * @param param 参数类型:<br>
	 *            <table border=1>
	 *            <tr>
	 *            <th>类型</th>
	 *            <th>格式示例</th>
	 *            </tr>
	 *            <tr>
	 *            <td>String</td>
	 *            <td>name1&value1&name2&value2</td>
	 *            </tr>
	 *            <tr>
	 *            <td>Map&lt;String, Object></td>
	 *            <td></td>
	 *            </tr>
	 *            <tr>
	 *            <td>HttpEntity</td>
	 *            <td></td>
	 *            </tr>
	 *            </table>
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String send(String cmd, String method, Object param) throws ParseException, IOException {
		return http(method, url + StringUtil.valueOfEmpty(cmd), param);
	}

	/**
	 * 调用http接口
	 * 
	 * @param url
	 * @param method
	 * @param param 参数类型:<br>
	 *            <table border=1>
	 *            <tr>
	 *            <th>类型</th>
	 *            <th>格式示例</th>
	 *            </tr>
	 *            <tr>
	 *            <td>String</td>
	 *            <td>name1&value1&name2&value2</td>
	 *            </tr>
	 *            <tr>
	 *            <td>Map&lt;String, Object></td>
	 *            <td></td>
	 *            </tr>
	 *            <tr>
	 *            <td>HttpEntity</td>
	 *            <td></td>
	 *            </tr>
	 *            </table>
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String http(String method, String url, Object param) throws ParseException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();

		HttpEntity entity = null;
		if (param instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) param;
			List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
			if (param != null) {
				for (Entry<String, Object> entry : map.entrySet()) {
					list.add(new BasicNameValuePair(entry.getKey(), StringUtil.valueOfNull(entry.getValue())));
				}
			}
			entity = new UrlEncodedFormEntity(list, Consts.UTF_8);
		} else if (param instanceof HttpEntity) {
			entity = (HttpEntity) param;
		} else if (param instanceof String) {
			if (url.indexOf("?") > -1) {
				url += "&" + param;
			} else {
				url += "?" + param;
			}
		}

		HttpRequestBase http = null;
		if (method == null) {
			method = GET;
		}
		switch (method) {
		case GET: {
			if (entity != null) {
				String paramStr = EntityUtils.toString(entity);
				if (url.indexOf("?") > -1) {
					http = new HttpGet(url + "&" + paramStr);
				} else {
					http = new HttpGet(url + "?" + paramStr);
				}
			} else {
				http = new HttpGet(url);
			}
			break;
		}
		case POST: {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(entity);
			http = httpPost;
			break;
		}
		case PUT: {
			HttpPut httpPut = new HttpPut(url);
			httpPut.setEntity(entity);
			http = httpPut;
			break;
		}
		default:
			throw new IllegalArgumentException("requestMethod Exception");
		}

		CloseableHttpResponse response = client.execute(http);
		HttpEntity reEntity = response.getEntity();
		if (reEntity != null) {
			return EntityUtils.toString(reEntity, "UTF-8");
		}
		response.close();
		client.close();
		return null;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
