/**
 * 
 */
package com.tuan.splider.parser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.hunthawk.reader.enhance.util.ToolDateUtil;
import com.tuan.domain.Article;
import com.tuan.splider.ArticleParser;
import com.tuan.util.IoUtils;

/**
 * @author sunquanzhi
 * 
 */
public class XituanParser implements ArticleParser {

	public Article parse(String htmlurl, Integer cityId, Integer fromId)
			throws Exception {

		URL url = new URL(htmlurl);
		URLConnection urlcon = url.openConnection();
		urlcon.setConnectTimeout(10000);
		urlcon.setReadTimeout(10000);
		urlcon.setDoOutput(true);
		HttpURLConnection httpConn = (HttpURLConnection) urlcon;
		httpConn.setInstanceFollowRedirects(false);
		httpConn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
		InputStream in = null;
		in = httpConn.getInputStream();
		String content = IoUtils.pipe(in, "utf-8");

		Article article = new Article();
		article.setUrl(htmlurl);
		article.setCategoryId(1);
		article.setCityId(cityId);

		Date date = new Date();
		date = DateUtils.addDays(date, 1);
		String strDate = ToolDateUtil.dateToString(date, "yyyyMMdd");
		date = ToolDateUtil.stringToDate(strDate, "yyyyMMdd");

		article.setEndTime(date);
		article.setFromId(fromId);
		parserContent(content, article);
		return article;
	}

	public static void parserContent(String content, Article article) {
		int index = content.indexOf("id=\"deal-intro\"");
		if (index > 0) {
			content = content.substring(index + 18);
			index = content.indexOf("</a>");

			if (index >= 0) {
				content = content.substring(index + 4);

				index = content.indexOf("</h1>");
				String title = content.substring(0, index);
				System.out.println(title);
				article.setName(title);
			}

		}
		index = content.indexOf("class=\"deal-price\"");
		if (index > 0) {
			content = content.substring(index + 18);
			index = content.indexOf("<strong>");
			if (index > 0) {
				content = content.substring(index + 8);
				index = content.indexOf("</strong>");
				String nowPrice = content.substring(0, index);
				content = content.substring(index + 9);
				nowPrice = getPrice(nowPrice);
				System.out.println(nowPrice);
				article.setNowPrice(nowPrice);
				index = content.indexOf("class=\"deal-discount\"");
				if (index > 0) {
					content = content.substring(index + 21);
					index = content.indexOf("</tr>");
					content = content.substring(index + 5);
					index = content.indexOf("<td>");
					if (index > 0) {

						content = content.substring(index + 4);
						index = content.indexOf(">");
						content = content.substring(index + 1);
						index = content.indexOf("</span>");
						String oldPrice = content.substring(0, index);
						oldPrice = getPrice(oldPrice);
						System.out.println(oldPrice);
						article.setOriginalPrice(oldPrice);

					}
					index = content.indexOf("<td>");
					if (index >= 0) {
						content = content.substring(index + 4);
						index = content.indexOf("</td>");
						String discount = content.substring(0, index);
						System.out.println(discount);
						article.setDiscount(discount);

					}
					index = content.indexOf("<td>");

					if (index >= 0) {
						content = content.substring(index + 4);
						index = content.indexOf("</td>");
						String saveMoney = content.substring(0, index);
						saveMoney = getPrice(saveMoney);
						System.out.println(saveMoney);
						article.setSaveMoney(saveMoney);

					}
					index = content.indexOf("class=\"deal-buy-cover-img\"");
					if (index >= 0) {
						content = content.substring(index + 26);
						index = content.indexOf("src=\"");
						content = content.substring(index + 5);
						index = content.indexOf("\"");
						String url = content.substring(0, index);
						System.out.println(url);

						article.setImgurl(url);

					}
				}
			}
		}
	}

	private static String getPrice(String str) {
		int index = str.indexOf("¥");
		if (index >= 0) {
			str = str.substring(index + 1);
			String price = "";
			for (int i = 0; i < str.length(); i++) {
				if (StringUtils.isNumeric("" + str.charAt(i))) {
					price += str.charAt(i);
				} else {
					continue;
				}
			}
			str = price;
		}
		return str;
	}

	public static void main(String[] args) throws Exception {
		XituanParser p = new XituanParser();
		p.parse("http://www.xituan.com/index.php?ename=bj", 1, 5);
		//美团网、拉手网、找折网、爱帮团、Yoka优享网、喜团、窝窝团、 团宝、糯米网、满座、乐拼、酷团、可可团、饭统饭团、Like团（之前叫橘子团最近刚改名）、F团爱赴团、58同城、36团、24券
	}
}